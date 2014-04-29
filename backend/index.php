<?php
require_once './flightphp/flight/Flight.php';
require_once './config.php';
require_once './functions.php';

# After 30 mins of inactivity, log user out.
define('MAX_LAST_ACTIVITY', 360);

# After 12 hours of session creation, regenerate id.
define("MAX_CREATE", 1800);     


/**
 * Registration.
 * 
 */
Flight::route('POST /reg', function() {
    
    $json_obj = get_the_json();
    
    /**
     * Complete list of errors that will possibly come up in this service.
     * Is there any other errors I have to look out for other than the one below?
     */
    global $error_list;
    
    # do server side validation.
    #   validate json request
    $valid_json_schema = array(
        'required' => array(
            'email',
            'password'
        ),
        'optional' => array(
            'firstname',
            'lastname',
            'gender',
            'live_in',
            'age',
            'profession',
            'seating_pref',
//            'some_about_you',     // the reason for it not be here is that it is not present the registration screen of the app.
//            'status',             // same reasone above.
            'image' => array(
                'name',
                'type',
                'content'
            )
        )
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # initialize database.
    $db = pdo_setup();
    
    # ERROR scenario. email format validation.
    if(!$em = filter_var($json_obj->required->email, FILTER_VALIDATE_EMAIL)) {
        # Sending error RESPONSE for invalid email
        error_response('x02', $error_list['x02']);
        return;
    }
    
    try {
        $sql  = "SELECT * FROM user ";
        $sql .= "WHERE email=:email";
        $stmt = $db->prepare($sql);
        $stmt->execute(array(':email' => $em));

        $result = $stmt->fetchAll();
        
        # ERROR scenario. if query returned non zero row, then, the email is already in use.
        if(count($result) != 0) {
            # send error RESPONSE for the email is already in use.
            error_response('x03', $error_list['x03']);
            return;
        }
        $sql  = 'INSERT INTO user ( ';
        $sql .= 'email, password, firstname, lastname, gender, live_in, age, profession, seating_pref ';
        $sql .= ') VALUES ( ';
        $sql .= ':email, :password, :firstname, :lastname, :gender, :live_in, :age, :profession, :seating_pref ';
        $sql .= ')';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email'            => $json_obj->required->email,
            ':password'         => $json_obj->required->password,
            ':firstname'        => $json_obj->optional->firstname,
            ':lastname'         => $json_obj->optional->lastname,
            ':gender'           => $json_obj->optional->gender,
            ':live_in'          => $json_obj->optional->live_in,
            ':age'              => $json_obj->optional->age,
            ':profession'       => $json_obj->optional->profession,
            ':seating_pref'     => $json_obj->optional->seating_pref,
        ));
        
        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        $sql  = 'INSERT INTO user_image ( ';
        $sql .= 'user_id, name, type, content ';
        $sql .= ') VALUES ( ';
        $sql .= ':user_id, :name, :type, :content ';
        $sql .= ')';

        $stmt = $db->prepare($sql);
        
        # getting a shorthand.
        $image = $json_obj->optional->image;
        $success = $stmt->execute(array(
            ':user_id'      => $db->lastInsertId(),
            ':name'         => $image->name,
            ':type'         => $image->type,
            ':content'      => base64_decode($image->content)
        ));

        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        # send success RESPONSE.
        $response = array(
            "success" => "true"
        );
        echo json_encode($response);
        return;
    } catch (PDOException $e) {
        echo $e->getTraceAsString();
        return;
    }
});


# Login
Flight::route('POST /login', function() {
    $json_obj = get_the_json();
    
    global $error_list;

    $valid_json_schema = array(
        'email',
        'password'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # validate user email and password.
    # initialize database.
    $db = pdo_setup();
    
    # check against database whether the email and password combination are valid.
    $sql  = "SELECT is_pass_provisional FROM user ";
    $sql .= "WHERE email=:email and password=:password ";
    
    $stmt = $db->prepare($sql);
    $suc = $stmt->execute(array(
        ':email' => $json_obj->email,
        ':password' => $json_obj->password
    ));
    
    # ERROR scenario. Wrong email and password combination.
    if(!($suc && $stmt->rowCount() === 1)) {
        # send error RESPONSE for username password combination is wrong.
        error_response('x04', $error_list['x04']);
        return;
    }
    
    # ERROR scenario. Provisional password expired.
    # provisional password expired
    $result = $stmt->fetchAll();
    $is_prov = (int)$result[0]->is_pass_provisional;
    if($is_prov !== 0) {
        if(time() - $is_prov > PROVISIONAL_PASS_EXPIRE) {
            # send error RESPONSE for provisional password expired. (07)
            error_response('x07', $error_list['x07']);
            return;
        }
        $_SESSION['PROVISIONAL'] = $is_prov;
        
        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'is_pass_provisional=:is_pass_provisional ';
        $sql .= 'WHERE email=:email';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':is_pass_provisional' => 0,
            ':email' => $json_obj->email
        ));
        if(!($success && $stmt->rowCount() === 1)) {
            error_response('x01', $error_list['x01']);
            return;
        }
    }
    
    # check to see whether the user was already logged in.
    $sql  = "SELECT session_id FROM session ";
    $sql .= "WHERE email=:email";
    $stmt = $db->prepare($sql);
    $suc = $stmt->execute(array(
        ':email' => $json_obj->email
    ));
    # if so, the row count should be 1. In that case, destroy the session and delete its reference from database.
    if($suc && $stmt->rowCount() === 1) {
        $result1 = $stmt->fetchAll();
        # destroy session.
        session_id($result1[0]->session_id);
        session_start();
        session_unset();
        session_destroy();
        # delete reference from database.
        $sql  = "DELETE FROM session ";
        $sql .= "WHERE email=:email";
        $stmt = $db->prepare($sql);
        $suc = $stmt->execute(array(
            ':email' => $json_obj->email
        ));
        # ERROR scenario. Database failure.
        if(!$suc) {
            error_response('x01', $error_list['x01']);
            return;
        }
    }
    /**
     * Now, regardless of whether the user was logged in (in that case, the 
     * session was destroyed and at this point of execution, there is no difference
     * between whether the user was previously logged in or not), create new
     * session, save reference to the database, send the sess_id to the client.
     * 
     * this feature is for security reasons during development. A properly
     * designed client (app) should not attempt to log in even though the 
     * user is already logged in. -Mustansir
     * 
     * Really? what about the client wanting to manually refresh session at a time
     * of its own choosing? -Mustansir
     */
    session_start();
    $_SESSION['email'] = $json_obj->email;
    $_SESSION['CREATED'] = time();
    $_SESSION['LAST_ACTIVITY'] = time();

    $sql  = "INSERT INTO session ( ";
    $sql .= "email, session_id ";
    $sql .= ") VALUES ( ";
    $sql .= ":email, :session_id ";
    $sql .= ")";

    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':email' => $json_obj->email,
        ':session_id' => session_id()
    ));
    
    # ERROR scenario. Database failure.
    if(!$success) {
        error_response('x01', $error_list['x01']);
        return;
    }
    # send success RESPONSE.
    $response = array(
        'success' => 'true',
        'PHPSESSID' => session_id()
    );
    echo json_encode($response);
    return;
});

# Logout
Flight::route('POST /logout', function() {
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    delete_session();
    
    # send SUCCESS response.
    $response = array(
        'success' => 'true'
    );
    echo json_encode($response);
    return;
});

# Update user info
Flight::route('PUT /reg', function() {
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'important' => array(
            'password'
        ),
        'optional' => array(
            'firstname',
            'lastname',
            'gender',
            'live_in',
            'age',
            'profession',
            'seating_pref',
            'some_about_you',
            'status',
            'image' => array(
                'name',
                'type',
                'content'
            )
        )
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    # check whether the password is a provisional one.
    $sql  = 'SELECT password, is_pass_provisional ';
    $sql .= 'FROM user ';
    $sql .= 'WHERE email=:email';
    
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':email' => $_SESSION['email']
    ));
    
    # ERROR scenario. Database failure.
    if(!($success && $stmt->rowCount() === 1)) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    $result = $stmt->fetchAll();
    # ERROR scenario. Must change provisional password.
    if((int)$result[0]->is_pass_provisional !== 0 && $result[0]->password === $json_obj->important->password) {
        # send error RESPONSE for provisional password was not changed.
        error_response('x08', $error_list['x08']);
        return;
    }
    
    $sql  = 'UPDATE user ';
    $sql .= 'SET ';
    $sql .= 'password=:password, ';
    $sql .= 'is_pass_provisional=:is_pass_provisional, ';
    $sql .= 'firstname=:firstname, ';
    $sql .= 'lastname=:lastname, ';
    $sql .= 'gender=:gender, ';
    $sql .= 'live_in=:live_in, ';
    $sql .= 'age=:age, ';
    $sql .= 'profession=:profession, ';
    $sql .= 'seating_pref=:seating_pref, ';
    $sql .= 'some_about_you=:some_about_you, ';
    $sql .= 'status=:status ';
    $sql .= 'WHERE email=:email ';
    
//    echo $sql.'<br />';
//    echo $_SESSION['email'].'<br />';
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':email'                => $_SESSION['email'],
        ':password'             => $json_obj->important->password,
        ':is_pass_provisional'  => 0,
        ':firstname'            => $json_obj->optional->firstname,
        ':lastname'             => $json_obj->optional->lastname,
        ':gender'               => $json_obj->optional->gender,
        ':live_in'              => $json_obj->optional->live_in,
        ':age'                  => $json_obj->optional->age,
        ':profession'           => $json_obj->optional->profession,
        ':seating_pref'         => $json_obj->optional->seating_pref,
        ':some_about_you'       => $json_obj->optional->some_about_you,
        ':status'               => $json_obj->optional->status
    ));
    
    # ERROR scenario. Database failure.
    if(!$success) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    
    # getting a shorthand.
    $image = $json_obj->optional->image;
    if(!empty($image->name)) {
        $sql  = 'UPDATE user_image ';
        $sql .= 'SET ';
        $sql .= 'name=:name, ';
        $sql .= 'type=:type, ';
        $sql .= 'content=:content ';
        $sql .= 'WHERE user_id='.sql_get_id_by_email();
        
//        echo $sql;
//        return;
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':name'         => $image->name,
            ':type'         => $image->type,
            ':content'      => base64_decode($image->content),
            ':email'        => $_SESSION['email']
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }
    }
    
    # send SUCCESS response.
    $response = array(
        'success' => 'true'
    );
    echo json_encode($response);
    return;
});

# Update status
Flight::route('POST /statusupdate', function() {
        
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'status'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    $sql  = 'UPDATE user ';
    $sql .= 'SET ';
    $sql .= 'status=:status ';
    $sql .= 'WHERE email=:email ';
    
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':status'   => $json_obj->status,
        ':email'    => $_SESSION['email']
    ));
    
    # ERROR scenario. Database failure.
    if(!$success) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    
    # send SUCCESS response.
    $response = array(
        'success' => 'true'
    );
    echo json_encode($response);
    return;
});

# Reset forgotten password and mail provisional pass
Flight::route('DELETE /passreset', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'forgotpass',
        'email'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
//    echo 'he he he...';
//    return;
    
    $db = pdo_setup();
    
    $sql  = 'SELECT password ';
    $sql .= 'FROM  user ';
    $sql .= 'WHERE email=:email ';
    
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':email' => $json_obj->email
    ));
    
    # ERROR scenario. Database failure.
    if(!$success) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    if(!($stmt->rowCount() == 1)) {
        # send error RESPONSE for the email is not registered.
        error_response('x06', $error_list['x06']);
        return;
    }
    
    $prov_pass = substr(sha1(uniqid()), 0, 8);
    
    $db = pdo_setup();
    
    $sql  = 'UPDATE user ';
    $sql .= 'SET ';
    $sql .= 'password=:password, ';
    
    /**
     * I shall do it later.
     */
    # SEND EMAIL.
//    ini_set("SMTP","ssl://smtp.gmail.com");
//    ini_set("smtp_port","465");
    
    
//    $to = 'mustansirmr@gmail.com';
//    $subject = 'Password Reset';
//    $message  = '<p>Your password has been reset.</p>';
//    $message .= '<p>User the following password to login: </p>';
//    $message .= '<p>'.substr(sha1(uniqid()), 0, 8).'</p>';
//    $message .= "<p>You can use it once within 24 hours from now</p>";
//    
//    $headers  = 'MIME-Version: 1.0' . "\r\n";
//    $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
//    $headers .= 'From: noreply@seatunity.com' . "\r\n";
//    mail($to, $subject, $message, $headers);
});

# Send info of newly added boarding pass (BP)
Flight::route('POST /newbp', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'authentication' => array(
            'PHPSESSID'
        ),
        'version',
        'bpdata' => array(
            'stringform',
            'firstname',
            'lastname',
            'PNR_code',
            'from',
            'to',
            'carrier',
            'flight_no',
            'julian_date',
            'compartment_code',
            'seat',
            'departure',
            'arrival'
        )
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->authentication->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    # store in the database.
    
    # send SUCCESS response.
    $response = array(
        'success'   => 'true',
        'id'        => '235'
    );
    echo json_encode($response);
    return;
});

# Get details of a single boarding pass (BP)
Flight::route('GET /bpdetail/@id:[1-9][0-9]{0,2}', function($id) {
    
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    if($id !== 333) {
        # ERROR scenario. Invalid boarding pass id.
        error_response('x09', $error_list['x09']);
        return;
    }
    
    $response = array(
        "success" => "true",
        "version" => "1",
        "bpdata" => array(
            "stringform" => 'M1HELDT/UWEMR         EYWX9ZS LWOMUCLH 2551     075M024A0008 355>2180O  O3075BOS 022052227001        262202331497901  LH     *30601001205',
            "firstname" => "Uwe",
            "lastname" => "Heldt",
            "PNR_code" => "YWX9ZS",
            "from" => "LWO",
            "to" => "MUC",
            "carrier" => "LH",
            "flight_no" => "2551",
            "julian_date" => "075",
            "compartment_code" => "M",
            "seat" => "24A",
            "departure" => "20:00",
            "arrival" => "21:10"
        )
    );
    echo json_encode($response);
    return;
});

# Get all boarding pass
Flight::route('GET /bplist', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'boarding_pass'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $response = array(
        'success'           => 'true',
        'boarding-pass'     => 'upcoming',
        'count'             => '3',
        'boarding_passess'  => array(
            array(
                'id'        => '1026',
                'version'   => '2',
                'bpdata'    => array(
                    'firstname' => 'Uwe',
                    'lastname'  => 'Heldt',
                    'PNR_code'  => 'YWX9ZS',
                    'from'      => 'LWO',
                    'to'        => 'MUC',
                    'carrier'   => 'LH',
                    'flight_no' => '2551',
                    'julian_no' => '075',
                    'compartment_code' => 'M',
                    'seat'      => '24A',
                    'departure' => '20:00',
                    'arrival'   => '21:10'
                )
            ),
            array(
                'id'        => '2594',
                'version'   => '1',
                'bpdata'    => array(
                    'firstname' => 'Uwe',
                    'lastname'  => 'Heldt',
                    'PNR_code'  => 'YWX9ZS',
                    'from'      => 'LWO',
                    'to'        => 'MUC',
                    'carrier'   => 'LH',
                    'flight_no' => '2551',
                    'julian_no' => '075',
                    'compartment_code' => 'M',
                    'seat'      => '24A',
                    'departure' => '20:00',
                    'arrival'   => '21:10'
                )
            ),
            array(
                'id'        => '1026',
                'version'   => '2',
                'bpdata'    => array(
                    'firstname' => 'Uwe',
                    'lastname'  => 'Heldt',
                    'PNR_code'  => 'YWX9ZS',
                    'from'      => 'LWO',
                    'to'        => 'MUC',
                    'carrier'   => 'LH',
                    'flight_no' => '2551',
                    'julian_no' => '075',
                    'compartment_code' => 'M',
                    'seat'      => '24A',
                    'departure' => '20:00',
                    'arrival'   => '21:10'
                )
            )
        )
    );
    
    # send success RESPONSE.
    echo json_encode($response);
    return;
});

# Check for BP update (is it necessary (meaning in the spec)? if yes, it would 
# still be unnecessary, for it will accomplished by push notification)
Flight::route('GET /bpupdate', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'bpids'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $dump_bp_list = array(573, 369, 333);
    
    foreach ($json_obj->bpids as $value) {
        # ERROR scenario. invalid or unknown boarding pass id.
        if(array_search((int)$value, $dump_bp_list) === FALSE) {
            error_response('x10', $error_list['x10']);
            return;
        }
    }
    
    $response = array(
        'success' => 'true',
        '573' => '2',
        '369' => '1',
        '333' => '3'
    );
    
    echo json_encode($response);
    return;
});

# Delete a boarding pass
Flight::route('DELETE /@bpid:[1-9][0-9]{0,2}', function($bpid) {
    $bpid = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $response = array(
        'success' => 'true'
    );
    
    echo json_encode($response);
    return;
});

# Collect seatmate list
Flight::route('GET /seatmatelist/@bpid:[1-9][0-9]{0,2}', function($bpid) {
    $bpid = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    # ERROR scenario. invalid or unknown boarding pass id.
    if($bpid !== 333) {
        error_response('x10', $error_list['x10']);
        return;
    }
    
    $response = array(
        "success" => "true",
        "count" => "2",
        "256" => array(
            "name" => "Mateja Bartolovic",
            "profession" => "UI/UX Designer",
            "class" => "Bussiness",
            "seat" => "14E",
            "image" => array(
                "name" => "256_mateja_bartolovic.png",
                "type" => "image/png",
                "content" => "<base64>"
            )
        ),
        "133" => array(
            "name" => "Ivan Kovac",
            "profession" => "Senior Programmer",
            "class" => "Bussiness",
            "seat" => "14E",
            "image" => array(
                "name" => "256_ivan_kovac.jpg",
                "type" => "image/jpg",
                "content" => "<base64>"
            )
        )
    );
    echo json_encode($response);
    return;
});

# Collect seatmate profile detail
Flight::route('GET /seatmate/@id:[1-9][0-9]{0,2}', function($id) {
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    # ERROR scenario. invalid or unknown seatmate id.
    if($id !== 369) {
        error_response('x11', $error_list['x11']);
        return;
    }
    
    $response = array(
        "success" => "true",
        "id" => "369",
        "seatmate_data_additional" => array(
            "shared_flight" => '333',
            "age" => "25-30",
            "live_in" => "Germany",
            "seating_pref" => "relax",
            "some_about_you" => "Because I am happy, keep it simple and make it happy",
            "status" => "Nothing much could...if my love is your love."
        )
    );
    echo json_encode($response);
    return;
});

# Send message (email) to a seatmate
Flight::route('POST /messagemate/@id:[1-9][0-9]{0,2}', function($id) {
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'message'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    # ERROR scenario. invalid or unknown seatmate id.
    if($id !== 369) {
        error_response('x11', $error_list['x11']);
        return;
    }
    
    $response = array(
        "success" => "true"
    );
    echo json_encode($response);
    return;
});

# Collect info of shared flight with a particular seatmate
Flight::route('GET /sharedflight/@mateid:[1-9][0-9]{0,10}', function($mateid) {
    $mateid = (int)$mateid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. email format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    # ERROR scenario. invalid or unknown seatmate id.
    if($mateid !== 369) {
        error_response('x11', $error_list['x11']);
        return;
    }
    
    $response = array(
        "success" => "true",
        'ids' => array(
            123, 456, 789
        )
    );
    echo json_encode($response);
    return;
});

# ------------------------------------------------------------------------------
# General debug
Flight::route('GET /', function() {
    echo "Hello World!<br />";
    echo time();
    
    session_start();
});

Flight::route('GET /debug', function() {
    echo "d..d..debug";
});

//session_setup();
Flight::start();