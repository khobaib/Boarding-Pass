<?php
require_once './flightphp/flight/Flight.php';
require_once './config.php';
require_once './functions.php';


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
        
        $reg_confirm_id = substr(sha1(uniqid()), 0, 10);
        $confirm_link = $_SERVER['HTTP_HOST'].'/confirm/'.$reg_confirm_id;
        
        
        $sql  = 'INSERT INTO user ( ';
        $sql .= 'email, password, is_reg_confirmed, firstname, lastname, gender, live_in, age, profession, seating_pref, image_name, image_type, image_content ';
        $sql .= ') VALUES ( ';
        $sql .= ':email, :password, :is_reg_confirmed, :firstname, :lastname, :gender, :live_in, :age, :profession, :seating_pref, :image_name, :image_type, :image_content ';
        $sql .= ')';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email'            => $json_obj->required->email,
            ':password'         => md5($json_obj->required->password),
            ':is_reg_confirmed' => $reg_confirm_id,
            ':firstname'        => $json_obj->optional->firstname,
            ':lastname'         => $json_obj->optional->lastname,
            ':gender'           => $json_obj->optional->gender,
            ':live_in'          => $json_obj->optional->live_in,
            ':age'              => $json_obj->optional->age,
            ':profession'       => $json_obj->optional->profession,
            ':seating_pref'     => $json_obj->optional->seating_pref,
            ':image_name'       => $json_obj->optional->image->name,
            ':image_type'       => $json_obj->optional->image->type,
            ':image_content'    => base64_decode($json_obj->optional->image->content)
        ));
        
        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }
        
        $to = $json_obj->required->email;
        $subject = 'SeatUnity: Registration Confirmation';
        $message  = '<p>Please follow the link below to confirm registration:</p>';
        $message .= '<a href="'.$confirm_link.'">'.$confirm_link.'</a>';

        $headers  = 'MIME-Version: 1.0' . "\r\n";
        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
        $headers .= 'From: noreply@seatunity.com' . "\r\n";
        mail($to, $subject, $message, $headers);
        
        
        # send success RESPONSE.
        $response = array(
            "success" => "true"
        );
        echo json_encode($response);
        return;
        
    } catch (PDOException $e) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01'].'. '.$e->getMessage());
        return;
    }
});


# Confirm registration
Flight::route('GET /confirm/@id:[0-9a-z]{10}', function($id) {
    
    global $error_list;
    
    $db = pdo_setup();
    
    $sql  = 'SELECT email, id ';
    $sql .= 'FROM user ';
    $sql .= 'WHERE is_reg_confirmed=:is_reg_confirmed';
    
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':is_reg_confirmed' => $id
    ));
    
    # ERROR scenario. Database failure.
    if(!($success && $stmt->rowCount() === 1)) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    
    $sql  = 'UPDATE user ';
    $sql .= 'SET ';
    $sql .= 'is_reg_confirmed=:is_reg_confirmed ';
    $sql .= 'WHERE is_reg_confirmed=:confirmed ';
    
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':is_reg_confirmed' => 1,
        ':confirmed'        => $id
    ));
    
    # ERROR scenario. Database failure.
    if(!($success && $stmt->rowCount() === 1)) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    
    echo 'thank you for signing up with us.';
});

# Login
Flight::route('POST /login', function() {
    $json_obj = get_the_json();
    
    global $error_list;

    $valid_json_schema = array(
        'email',
        'password'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # validate user email and password.
    # initialize database.
    $db = pdo_setup();
    
    try {
        # check against database whether the email and password combination are valid.
        $sql  = "SELECT id, is_pass_provisional FROM user ";
        $sql .= "WHERE email=:email and password=:password ";

        $stmt = $db->prepare($sql);
        $suc = $stmt->execute(array(
            ':email' => $json_obj->email,
            ':password' => md5($json_obj->password)
        ));

        # ERROR scenario. Wrong email and password combination.
        if(!($suc && $stmt->rowCount() === 1)) {
            # send error RESPONSE for username password combination is wrong.
            error_response('x04', $error_list['x04']);
            return;
        }

        $result = $stmt->fetchAll();
        
        $user_id = (int)$result[0]->id;
        $is_prov = (int)$result[0]->is_pass_provisional;
        
        if($is_prov !== 0) {
            # ERROR scenario. Provisional password expired.
            if($is_prov === -1 || time() - $is_prov > PROVISIONAL_PASS_EXPIRE) {
                # send error RESPONSE for provisional password expired. (07)
                error_response('x07', $error_list['x07']);
                return;
            }
            $_SESSION['PROVISIONAL'] = $is_prov;

            $sql  = 'UPDATE user ';
            $sql .= 'SET ';
            $sql .= 'is_pass_provisional=:is_pass_provisional ';
            $sql .= 'WHERE email=:email ';

            $stmt = $db->prepare($sql);
            $success = $stmt->execute(array(
                ':is_pass_provisional' => -1,
                ':email' => $json_obj->email
            ));
            
            # ERROR scenario. Database failure.
            if(!($success && $stmt->rowCount() === 1)) {
                # send error RESPONSE for database failure.
                error_response('x01', $error_list['x01']);
                return;
            }
        }

        # check to see whether the user was already logged in.
        $sql  = 'SELECT session_id FROM user ';
        $sql .= 'WHERE email=:email ';
        $stmt = $db->prepare($sql);
        $suc = $stmt->execute(array(
            ':email' => $json_obj->email
        ));
        # if so, the row count should be other than empty string (''). In that case, destroy the session and set its reference in user table to empty string ('').
        $result1 = $stmt->fetchAll();
        if($suc && !empty($result1->session_id)) {
            $result1 = $stmt->fetchAll();
            # destroy session.
            session_id($result1[0]->session_id);
            session_start();
            session_unset();
            session_destroy();
            # remove reference from database.
            $sql  = 'UPDATE user ';
            $sql .= 'SET ';
            $sql .= 'session_id=:session_id ';
            $sql .= 'WHERE email=:email ';
            $stmt = $db->prepare($sql);
            $suc = $stmt->execute(array(
                ':email'        => $json_obj->email,
                ':session_id'   => ''
            ));
            # ERROR scenario. Database failure.
            if(!$suc) {
                # send error RESPONSE for database failure.
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
         * What about the client wanting to manually refresh session at a time
         * of its own choosing? -Mustansir
         */
        session_start();
        $_SESSION['user_id'] = $user_id;
        $_SESSION['email'] = $json_obj->email;
        $_SESSION['CREATED'] = time();
        $_SESSION['LAST_ACTIVITY'] = time();

        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'session_id=:session_id ';
        $sql .= 'WHERE email=:email ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email' => $json_obj->email,
            ':session_id' => session_id()
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }
        # send success RESPONSE.
        $response = array(
            'success' => 'true',
            'PHPSESSID' => session_id()
        );
        if($is_prov > 0) {
            $response['PROVISIONAL'] = 'TRUE';
        }
        echo json_encode($response);
        return;
        
    } catch (PDOException $exc) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }

});

# Logout
Flight::route('POST /logout', function() {
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. JSON format validation.
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
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
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
        $sql .= 'status=:status, ';
        $sql .= 'image_name=:image_name, ';
        $sql .= 'image_type=:image_type, ';
        $sql .= 'image_content=:image_content ';
        $sql .= 'WHERE email=:email ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email'                => $_SESSION['email'],
            ':password'             => md5($json_obj->important->password),
            ':is_pass_provisional'  => 0,
            ':firstname'            => $json_obj->optional->firstname,
            ':lastname'             => $json_obj->optional->lastname,
            ':gender'               => $json_obj->optional->gender,
            ':live_in'              => $json_obj->optional->live_in,
            ':age'                  => $json_obj->optional->age,
            ':profession'           => $json_obj->optional->profession,
            ':seating_pref'         => $json_obj->optional->seating_pref,
            ':some_about_you'       => $json_obj->optional->some_about_you,
            ':status'               => $json_obj->optional->status,
            ':image_name'           => $json_obj->optional->image->name,
            ':image_type'           => $json_obj->optional->image->type,
            ':image_content'        => base64_decode($json_obj->optional->image->content)
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
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Update status
Flight::route('POST /statusupdate', function() {
        
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'status'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
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
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Reset forgotten password and mail provisional pass
Flight::route('DELETE /passreset', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'forgotpass',
        'email'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    $db = pdo_setup();
    
    try {
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
        # ERROR scenario. email not registered.
        if(!($stmt->rowCount() == 1)) {
            # send error RESPONSE for the email is not registered.
            error_response('x06', $error_list['x06']);
            return;
        }

        $prov_pass = substr(sha1(uniqid()), 0, 8);


        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'password=:password, ';
        $sql .= 'is_pass_provisional=:is_pass_provisional ';
        $sql .= 'WHERE email=:email';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':password'             => $prov_pass,
            ':is_pass_provisional'  => (string)time(),
            ':email'                => $json_obj->email
        ));

        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        $to = $json_obj->email;
        $subject = 'Password Reset for SeatUnity';
        $message  = '<p>Your password has been reset.</p>';
        $message .= '<p>User the following password to login: </p>';
        $message .= '<p>'.$prov_pass.'</p>';
        $message .= "<p>You can use it once within 24 hours from now</p>";

        $headers  = 'MIME-Version: 1.0' . "\r\n";
        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
        $headers .= 'From: noreply@seatunity.com' . "\r\n";
        mail($to, $subject, $message, $headers);

        $response = array(
            'success' => 'true'
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
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
        'year',
        'bpdata' => array(
            'stringform',
            'firstname',
            'lastname',
            'PNR',
            'travel_from',
            'travel_to',
            'carrier',
            'flight_no',
            'julian_date',
            'compartment_code',
            'seat',
            'departure',
            'arrival'
        )
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->authentication->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        # check to see whether this boarding pass was already added.
        $sql  = 'SELECT id ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE user_id=:user_id AND carrier=:carrier AND flight_no=:flight_no AND julian_date=:julian_date ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => $_SESSION['user_id'],
            ':carrier'      => $json_obj->bpdata->carrier,
            ':flight_no'    => $json_obj->bpdata->flight_no,
            ':julian_date'  => $json_obj->bpdata->julian_date
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }


        if(!($stmt->rowCount() === 0)) {
            # send error RESPONSE for the boarding pass is already added.
            error_response('x11', $error_list['x11']);
            return;
        }

        # store in the database.
        $sql  = 'INSERT INTO boarding_pass ( ';
        $sql .= 'user_id, stringform, firstname, lastname, PNR, travel_from, travel_to, carrier, flight_no, julian_date, compartment_code, seat, departure, arrival, year ';
        $sql .= ') VALUES ( ';
        $sql .= ':user_id, :stringform, :firstname, :lastname, :PNR, :travel_from, :travel_to, :carrier, :flight_no, :julian_date, :compartment_code, :seat, :departure, :arrival, :year ';
        $sql .= ') ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'          => $_SESSION['user_id'],
            ':stringform'       => $json_obj->bpdata->stringform,
            ':firstname'        => $json_obj->bpdata->firstname,
            ':lastname'         => $json_obj->bpdata->lastname,
            ':PNR'              => $json_obj->bpdata->PNR,
            ':travel_from'      => $json_obj->bpdata->travel_from,
            ':travel_to'        => $json_obj->bpdata->travel_to,
            ':carrier'          => $json_obj->bpdata->carrier,
            ':flight_no'        => $json_obj->bpdata->flight_no,
            ':julian_date'      => $json_obj->bpdata->julian_date,
            ':compartment_code' => $json_obj->bpdata->compartment_code,
            ':seat'             => $json_obj->bpdata->seat,
            ':departure'        => $json_obj->bpdata->departure,
            ':arrival'          => $json_obj->bpdata->arrival,
            ':year'             => $json_obj->year
        ));

        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        # send SUCCESS response.
        $response = array(
            'success'   => 'true',
            'id'        => $db->lastInsertId()
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Get details of a single boarding pass (BP)
Flight::route('GET /bpdetail/@id:[1-9][0-9]{0,10}', function($id) {
    
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT * ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE id=:id ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id' => $id
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        # ERROR scenario. Invalid or unknown boarding pass id.
        if(!($stmt->rowCount() === 1)) {
            # ERROR scenario. Invalid or unknown boarding pass id.
            error_response('x09', $error_list['x09']);
            return;
        }

        $result = $stmt->fetchAll();

        $response = array(
            "success"   => "true",
            "version"   => $result[0]->version,
            "bpdata"    => array(
                "stringform"        => $result[0]->stringform,
                "firstname"         => $result[0]->firstname,
                "lastname"          => $result[0]->lastname,
                "PNR"               => $result[0]->PNR,
                "travel_from"       => $result[0]->travel_from,
                "travel_to"         => $result[0]->travel_to,
                "carrier"           => $result[0]->carrier,
                "flight_no"         => $result[0]->flight_no,
                "julian_date"       => $result[0]->julian_date,
                "compartment_code"  => $result[0]->compartment_code,
                "seat"              => $result[0]->seat,
                "departure"         => $result[0]->departure,
                "arrival"           => $result[0]->arrival
            )
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Get all boarding pass
Flight::route('GET /bplist', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'boarding_pass'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT * ';
        $sql .= 'FROM boarding_pass ';
        if($json_obj->boarding_pass === 'upcoming')  {
            $sql .= 'WHERE year>=:year OR (year=:year AND julian_date>=:julian_date) ';
        } else if($json_obj->boarding_pass === 'past') {
            $sql .= 'WHERE year<=:year OR (year=:year AND julian_date<:julian_date) ';
        } else if($json_obj->boarding_pass === 'all') {
        } else {
            # send error RESPONSE for JSON request is in invalid format.
            error_response('x00', $error_list['x00']);
            return TRUE;
        }
        $sql .= 'ORDER BY year ASC, julian_date DESC';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':year'         => (int)date('Y'),
            ':julian_date'  => gregoriantojd(date('n'), date('j'), date('Y')) - gregoriantojd(1, 1, date('Y')) - 1
        ));

        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        $response = array(
            'success'           => 'true',
            'boarding-pass'     => $json_obj->boarding_pass,
            'count'             => $stmt->rowCount(),
        );

        if($stmt->rowCount() === 0) {
            echo json_encode($response);
            return;
        }

        $result = $stmt->fetchAll();
        

        foreach ($result as $row) {
            $response['boarding_pass'][] = array(
                'id'        => $row->id,
                'version'   => $row->user_id,
                'bpdata'    => array(
                    'firstname'         => $row->firstname,
                    'lastname'          => $row->lastname,
                    'PNR'               => $row->PNR,
                    'travel_from'       => $row->travel_from,
                    'travel_to'         => $row->travel_to,
                    'carrier'           => $row->carrier,
                    'flight_no'         => $row->flight_no,
                    'julian_date'       => $row->julian_date,
                    'compartment_code'  => $row->compartment_code,
                    'seat'              => $row->seat,
                    'departure'         => $row->departure,
                    'arrival'           => $row->arrival
                )
            );
        }
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Check for BP update (is it necessary (meaning in the spec)? if yes, it would 
# still be unnecessary, for it will accomplished by push notification)
Flight::route('GET /bpupdate', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT id, version ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE user_id=:user_id ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id' => $_SESSION['user_id']
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        $result = $stmt->fetchAll();

        $response = array(
            'success' => 'true'
        );
        foreach ($result as $row) {
            $response[$row->id] = $row->version;
        }

        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Delete a boarding pass
Flight::route('DELETE /@bpid:[1-9][0-9]{0,10}', function($bpid) {
    $bpid = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'DELETE FROM boarding_pass ';
        $sql .= 'WHERE id=:id AND user_id=:user_id';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $bpid,
            ':user_id'  => $_SESSION['user_id']
        ));


        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        # ERROR scenario. invalid or unknown boarding pass id.
        if($stmt->rowCount() !== 1) {
            error_response('x09', $error_list['x09']);
            return;
        }

        $response = array(
            'success' => 'true'
        );

        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Collect seatmate list
Flight::route('GET /seatmatelist/@bpid:[1-9][0-9]{0,10}', function($bpid) {
    $bpid = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT id, user_id ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE id=:id AND user_id=:user_id ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $bpid,
            ':user_id'  => $_SESSION['user_id']
        ));
        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }
        
        # ERROR scenario. Invalid or unknown boarding pass id.
        if($stmt->rowCount() !== 1) {
            # send error RESPONSE for database failure.
            error_response('x09', $error_list['x09']);
            return;
        }
        
        $sql  = 'SELECT     p1.id, CONCAT_WS(\' \', p1.firstname, p1.lastname) AS name, p1.profession, p1.image_name, p1.image_type, p1.image_content, ( ';
        $sql .= '   SELECT      b1.class ';
        $sql .= '   FROM        travel_class b1 ';
        $sql .= '   LEFT JOIN   boarding_pass b2 ON b1.compartment_code=b2.compartment_code ';
        $sql .= '   WHERE       b2.user_id=:user_id ';
        $sql .= ') AS class, p2.seat ';
        $sql .= 'FROM       user p1 ';
        $sql .= 'RIGHT JOIN ( ';
        $sql .= '   SELECT DISTINCT s1.user_id, s1.seat ';
        $sql .= '   FROM            boarding_pass s1 ';
        $sql .= '   LEFT JOIN       boarding_pass s2 ';
        $sql .= '   ON              s1.carrier=s2.carrier AND s1.flight_no=s2.flight_no AND s1.julian_date=s2.julian_date AND s1.year=s2.year AND s1.user_id<>s2.user_id ';
        $sql .= '   WHERE           s2.user_id=:user_id AND s2.id=:id';
        $sql .= ') p2 ';
        $sql .= 'ON         p1.id=p2.user_id ';


        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $bpid,
            ':user_id'  => $_SESSION['user_id']
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        $result = $stmt->fetchAll();

        $response = array(
            'success'   => 'true',
            'count'     => count($result)
        );

        for($i = 0; $i < count($result); ++$i) {
            $response[$result[$i]->id] = array(
                'name'          => $result[$i]->name,
                'profession'    => $result[$i]->profession,
                'class'         => $result[$i]->class,
                'seat'          => $result[$i]->seat,
                'image'         => array(
                    'name'          => $result[$i]->image_name,
                    'type'          => $result[$i]->image_type,
                    'content'       => base64_encode($result[$i]->image_content)
                )
            );
        }

        echo json_encode($response);
        return;
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Collect seatmate profile detail
Flight::route('GET /seatmate/@id:[1-9][0-9]{0,10}/@bpid:[1-9][0-9]{0,10}', function($id, $bpid) {
    $id     = (int)$id;
    $bpid   = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT     s1.age, s1.live_in, s1.seating_pref, s1.some_about_you, s1.status ';
        $sql .= 'FROM		user s1 ';
        $sql .= 'RIGHT JOIN	( ';
        $sql .= '   SELECT      p1.user_id ';
        $sql .= '   FROM        boarding_pass p1 ';
        $sql .= '   LEFT JOIN   boarding_pass p2 ';
        $sql .= '   ON          p1.carrier=p2.carrier AND p1.flight_no=p2.flight_no AND p1.julian_date=p2.julian_date AND p1.year=p2.year AND p1.user_id<>p2.user_id ';
        $sql .= '   WHERE       p2.id=:user_id AND p2.user_id=:bpid ';
        $sql .= ') s2 ';
        $sql .= 'ON         s1.id=s2.user_id ';
        $sql .= 'WHERE      s1.id=:seatmate_id ';


        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':bpid'         => $bpid,
            ':user_id'      => $_SESSION['user_id'],
            ':seatmate_id'  => $id
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
            return;
        }

        # ERROR scenario. invalid or unknown boarding pass or seatmate id.
        if($stmt->rowCount() !== 1) {
            error_response('x12', $error_list['x12']);
            return;
        }

        $result = $stmt->fetchAll();
        
        $response = array(
            "success" => "true",
            "id" => $id,
            "seatmate_data_additional" => array(
                "shared_flight" => $bpid,
                "age" => $result[0]->age,
                "live_in" => $result[0]->live_in,
                "seating_pref" => $result[0]->seating_pref,
                "some_about_you" => $result[0]->some_about_you,
                "status" => $result[0]->status
            )
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
    
});

# Send message (email) to a seatmate
Flight::route('POST /messagemate/@id:[1-9][0-9]{0,10}', function($id) {
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'PHPSESSID',
        'message'
    );
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }

    $sql  = 'SELECT email, CONCAT_WS(\' \', firstname, lastname) AS name ';
    $sql .= 'FROM   user ';
    $sql .= 'WHERE  id IN ( :user_id, :seatmate_id ) ';
    
    $stmt = $db->prepare($sql);
    $success = $stmt->execute(array(
        ':user_id'      => (int)$_SESSION['user_id'],
        ':seatmate_id'  => (int)$id
    ));
    
    # ERROR scenario. Database failure.
    if(!$success) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    
    # ERROR scenario. invalid or unknown seatmate id.
    if($stmt->rowCount() !== 2) {
        error_response('x10', $error_list['x10']);
        return;
    }
    $result = $stmt->fetchAll();
//    var_dump($result);
//    return;
    
    $to = $result[1]->email;
    $subject = $result[0]->name.' <'.$_SESSION['email'].'> messaged you via seatunity';
    $message  = '<p>Hi</p>';
    $message .= '<p>I messaged you</p>';
    
    $headers  = 'MIME-Version: 1.0' . "\r\n";
    $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
//    $headers .= 'From: '.$_SESSION['email']."\r\n";
    $headers .= 'From: messageservice@seatunity.com\r\n';
    mail($to, $subject, $json_obj->message, $headers);
    
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
    
    # ERROR scenario. JSON format validation.
    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
        return;
    }
    
    # ERROR scenario. None logged in.
    if(!is_logged_in($json_obj->PHPSESSID)) {
        error_response('x05', $error_list['x05']);
        return;
    }
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT p1.id ';
        $sql .= 'FROM   ( ';
        $sql .= '   SELECT  * ';
        $sql .= '   FROM    boarding_pass ';
        $sql .= '   WHERE	user_id=:user_id ';
        $sql .= ') p1 ';
        $sql .= 'LEFT JOIN	( ';
        $sql .= '   SELECT  * ';
        $sql .= '   FROM    boarding_pass ';
        $sql .= '   WHERE   user_id=:seatmate_id ';
        $sql .= ') p2 ';
        $sql .= 'ON     p1.carrier=p2.carrier ';
        $sql .= 'AND    p1.flight_no=p2.flight_no ';
        $sql .= 'AND    p1.julian_date=p2.julian_date ';
        $sql .= 'AND    p1.year=p2.year ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => $_SESSION['user_id'],
            ':seatmate_id'  => $mateid
        ));

        $result = $stmt->fetchAll();

        $response = array(
            'success'   => 'true',
            'ids'       => array()
        );
        foreach ($result as $row) {
            $response['ids'][] = $row->id;
        }
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
        return;
    }
});

# ------------------------------------------------------------------------------
# General debug
Flight::route('GET /', function() {
    echo "Hello World!<br />";
    echo time();
    
    $to = 'mustansirmr@gmail.com';
    $subject = 'Password Reset';
    $message  = '<p>Your password has been reset.</p>';
    $message .= '<p>User the following password to login: </p>';
    $message .= '<p>'.substr(sha1(uniqid()), 0, 8).'</p>';
    $message .= "<p>You can use it once within 24 hours from now</p>";
    
    $headers  = 'MIME-Version: 1.0' . "\r\n";
    $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
    $headers .= 'From: noreply@seatunity.com' . "\r\n";
    mail($to, $subject, $message, $headers);
    
    
    echo 'blah blah...'.HTML_LINE_BREAK;
});

Flight::route('GET /debug', function() {
    echo "d..d..debug";
});

//session_setup();
Flight::start();