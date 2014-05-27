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
//    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
//        return;
//    }
    
    json_schema_validation($valid_json_schema, $json_obj);
    
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
        }
        
        $reg_confirm_id = substr(sha1(uniqid()), 0, 10);
        
        
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
        }
        
        MailHandler::confirm_mail_handler($json_obj->required->email, $reg_confirm_id);
        
//        $to = $json_obj->required->email;
//        $subject = 'SeatUnity: Registration Confirmation';
//        $message  = '<p>Please follow the link below to confirm registration:</p>';
//        $message .= '<a href="'.$confirm_link.'">'.$confirm_link.'</a>';
//
//        $headers  = 'MIME-Version: 1.0' . "\r\n";
//        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
//        $headers .= 'From: noreply@seatunity.com' . "\r\n";
//        mail($to, $subject, $message, $headers);
        
        
        # send success RESPONSE.
        $response = array(
            "success" => "true"
        );
        echo json_encode($response);
        return;
        
    } catch (PDOException $e) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01'].'. '.$e->getMessage());
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
    
//    # ERROR scenario. JSON format validation.
//    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
//        return;
//    }
    json_schema_validation($valid_json_schema, $json_obj);
    # validate user email and password.
    # initialize database.
    $db = pdo_setup();
    
    try {
        # check against the database whether the email and password combination are valid.
        $sql  = 'SELECT id, is_pass_provisional, is_reg_confirmed ';
        $sql .= 'FROM user ';
        $sql .= 'WHERE email=:email and password=:password ';

        $stmt = $db->prepare($sql);
        $suc = $stmt->execute(array(
            ':email' => $json_obj->email,
            ':password' => md5($json_obj->password)
        ));

        # ERROR scenario. Wrong email and password combination.
        if(!($suc && $stmt->rowCount() === 1)) {
            # send error RESPONSE for username password combination is wrong.
            error_response('x04', $error_list['x04']);
        }

        $result = $stmt->fetchAll();
        
        $user_id = (int)$result[0]->id;
        $is_prov = (int)$result[0]->is_pass_provisional;
        $is_confirmed = (int)$result[0]->is_reg_confirmed;
        
        # ERROR scenario. Registration not confirmed.
        if($is_confirmed !== 1) {
            error_response('x13', $error_list['x13']);
        }
        
        if($is_prov !== 0) {
            # ERROR scenario. Provisional password expired.
            if($is_prov === -1 || time() - $is_prov > PROVISIONAL_PASS_EXPIRE) {
                # send error RESPONSE for provisional password expired. (07)
                error_response('x07', $error_list['x07']);
            }
//            $_SESSION['PROVISIONAL'] = $is_prov;

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
            }
        }

        # check to see whether the user was already logged in.
        $sql  = 'SELECT token FROM user ';
        $sql .= 'WHERE email=:email ';
        $stmt = $db->prepare($sql);
        $suc = $stmt->execute(array(
            ':email' => $json_obj->email
        ));
        # if so, the row count should be other than empty string (''). In that case, destroy the session and set its reference in user table to empty string ('').
        $result1 = $stmt->fetchAll();
        if($suc && !empty($result1->token)) {
            $result1 = $stmt->fetchAll();
            # remove reference from database.
            $sql  = 'UPDATE user ';
            $sql .= 'SET ';
            $sql .= 'token=:token ';
            $sql .= 'WHERE email=:email ';
            $stmt = $db->prepare($sql);
            $suc = $stmt->execute(array(
                ':email'        => $json_obj->email,
                ':token'   => ''
            ));
            # ERROR scenario. Database failure.
            if(!$suc) {
                # send error RESPONSE for database failure.
                error_response('x01', $error_list['x01']);
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
        
        $token = generate_token();
        
        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'token=:token ';
        $sql .= 'WHERE email=:email ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email' => $json_obj->email,
            ':token' => $token
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        # send success RESPONSE.
        $response = array(
            'success' => 'true',
            'token' => $token
        );
        if($is_prov > 0) {
            $response['PROVISIONAL'] = 'TRUE';
        }
        echo json_encode($response);
        return;
        
    } catch (PDOException $exc) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }

});

# Logout
Flight::route('POST /logout', function() {
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token'
    );
    
//    # ERROR scenario. JSON format validation.
//    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
//        return;
//    }
    
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
     
    $user_info = is_logged_in($json_obj->token);
    
    delete_token($user_info['email']);
    
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
        'token',
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
//    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
//        return;
//    }
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        # check whether the password is a provisional one.
        $sql  = 'SELECT password, is_pass_provisional ';
        $sql .= 'FROM user ';
        $sql .= 'WHERE email=:email';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email' => $user_info['email']
        ));
        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        $result = $stmt->fetchAll();
        # ERROR scenario. Must change provisional password.
        if((int)$result[0]->is_pass_provisional !== 0 && $result[0]->password === $json_obj->important->password) {
            # send error RESPONSE for provisional password was not changed.
            error_response('x08', $error_list['x08']);
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
            ':email'                => $user_info['email'],
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
        }

        # send SUCCESS response.
        $response = array(
            'success' => 'true'
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Update status
Flight::route('POST /statusupdate', function() {
        
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token',
        'status'
    );
    
//    # ERROR scenario. JSON format validation.
//    if(json_schema_validation($valid_json_schema, $json_obj, $error_list)) {
//        return;
//    }
//    
//    # ERROR scenario. None logged in.
//    if(!is_logged_in($json_obj->session_id)) {
//        error_response('x05', $error_list['x05']);
//    }
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'status=:status ';
        $sql .= 'WHERE email=:email ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':status'   => $json_obj->status,
            ':email'    => $user_info['email']
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }

        # send SUCCESS response.
        $response = array(
            'success' => 'true'
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
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
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
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
        }
        # ERROR scenario. email not registered.
        if(!($stmt->rowCount() == 1)) {
            # send error RESPONSE for the email is not registered.
            error_response('x06', $error_list['x06']);
        }

        $prov_pass = substr(sha1(uniqid()), 0, 8);


        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'password=:password, ';
        $sql .= 'is_pass_provisional=:is_pass_provisional ';
        $sql .= 'WHERE email=:email';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':password'             => md5($prov_pass),
            ':is_pass_provisional'  => (string)time(),
            ':email'                => $json_obj->email
        ));

        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        MailHandler::pass_reset_mail_handler($json_obj->email, $prov_pass);
//        $to = $json_obj->email;
//        $subject = 'Password Reset for SeatUnity';
//        $message  = '<p>Your password has been reset.</p>';
//        $message .= '<p>Use the following password to login: </p>';
//        $message .= '<p>'.$prov_pass.'</p>';
//        $message .= "<p>You can use it once within 24 hours from now</p>";
//
//        $headers  = 'MIME-Version: 1.0' . "\r\n";
//        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
//        $headers .= 'From: noreply@seatunity.com' . "\r\n";
//        mail($to, $subject, $message, $headers);

        $response = array(
            'success' => 'true'
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Send info of newly added boarding pass (BP)
Flight::route('POST /newbp', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'authentication' => array(
            'token'
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
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->authentication->token);
    
    $db = pdo_setup();
    
    try {
        # check to see whether this boarding pass was already added.
        $sql  = 'SELECT id ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE user_id=:user_id AND carrier=:carrier AND flight_no=:flight_no AND julian_date=:julian_date ';
        
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => $user_info['user_id'],
            ':carrier'      => $json_obj->bpdata->carrier,
            ':flight_no'    => $json_obj->bpdata->flight_no,
            ':julian_date'  => $json_obj->bpdata->julian_date
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }


        if(!($stmt->rowCount() === 0)) {
            # send error RESPONSE for the boarding pass is already added.
            error_response('x11', $error_list['x11']);
        }
        
        # store in the database.
        $sql  = 'INSERT INTO boarding_pass ( ';
        $sql .= 'user_id, stringform, firstname, lastname, PNR, travel_from, travel_to, carrier, flight_no, julian_date, compartment_code, seat, departure, arrival, year ';
        $sql .= ') VALUES ( ';
        $sql .= ':user_id, :stringform, :firstname, :lastname, :PNR, :travel_from, :travel_to, :carrier, :flight_no, :julian_date, :compartment_code, :seat, :departure, :arrival, :year ';
        $sql .= ') ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'          => $user_info['user_id'],
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
    }
    
});

# Get details of a single boarding pass (BP)
Flight::route('GET /bpdetail/@id:[1-9][0-9]{0,10}', function($id) {
    
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT * ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE id=:id AND user_id=:user_id ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $id,
            ':user_id'  => $user_info['user_id']
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }

        # ERROR scenario. Invalid or unknown boarding pass id.
        if(!($stmt->rowCount() === 1)) {
            # ERROR scenario. Invalid or unknown boarding pass id.
            error_response('x09', $error_list['x09']);
        }

        $result = $stmt->fetchAll();

        $response = array(
            "success"   => "true",
            "version"   => $result[0]->version,
            "bpdata"    => array(
                'id'                => $result[0]->id,
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
    }
    
});

# Get all boarding pass
Flight::route('GET /bplist', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token',
        'boarding_pass'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT * ';
        $sql .= 'FROM boarding_pass ';
        if($json_obj->boarding_pass === 'upcoming')  {
            $sql .= 'WHERE user_id=:user_id AND (year>=:year OR (year=:year AND julian_date>=:julian_date)) ';
        } else if($json_obj->boarding_pass === 'past') {
            $sql .= 'WHERE user_id=:user_id AND (year<=:year OR (year=:year AND julian_date<:julian_date)) ';
        } else if($json_obj->boarding_pass === 'all') {
        } else {
            # send error RESPONSE for JSON request is in invalid format.
            error_response('x00', $error_list['x00']);
        }
        $sql .= 'ORDER BY year ASC, julian_date DESC';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => $user_info['user_id'],
            ':year'         => (int)date('Y'),
            ':julian_date'  => gregoriantojd(date('n'), date('j'), date('Y')) - gregoriantojd(1, 1, date('Y')) - 1
        ));

        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
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
                    "stringform"        => $result[0]->stringform,
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
    }
    
});

# Check for BP update (is it necessary (meaning in the spec)? if yes, it would 
# still be unnecessary, for it will accomplished by push notification)
Flight::route('GET /bpupdate', function() {
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token',
        'ver_info'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $count = count($json_obj->ver_info);
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT * ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE user_id=:user_id AND id IN ( ';

        foreach ($json_obj->ver_info as $bp) {
            $sql .= $bp->id.', ';
        }

        $sql = substr($sql, 0, strlen($sql)-2).' ) ';
        
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => $user_info['user_id']
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        # ERROR scenario. Invalid or unknown boarding pass id.
        if(!($stmt->rowCount() === $count)) {
            # ERROR scenario. Invalid or unknown boarding pass id.
            error_response('x09', $error_list['x09']);
        }
        
        $result = $stmt->fetchAll();
        
        $response = array(
            'success'               => 'true',
            'boarding_passes'       => array()
        );
        foreach ($json_obj->ver_info as $elem) {
            for ($i = 0; $i < count($result); ++$i) {
                if($result[$i] === NULL) {
                    continue;
                }
                if($elem->id == $result[$i]->id) {
                    if($elem->version < $result[$i]->version) {
                        $response['boarding_passes'][] = array(
                            'id'            => $result[$i]->id,
                            'version'       => $result[$i]->version,
                            'bpdata'        => array(
                                'stringform'    => $result[$i]->stringform,
                                'firstname'     => $result[$i]->firstname,
                                'lastname'      => $result[$i]->lastname,
                                'PNR'           => $result[$i]->PNR,
                                'travel_from'   => $result[$i]->travel_from,
                                'travel_to'     => $result[$i]->travel_to,
                                'carrier'       => $result[$i]->carrier,
                                'flight_no'     => $result[$i]->flight_no,
                                'julian_date'   => $result[$i]->julian_date,
                                'compartment_code'  => $result[$i]->compartment_code,
                                'seat'          => $result[$i]->seat,
                                'departure'     => $result[$i]->departure,
                                'arrival'       => $result[$i]->arrival,
                            )
                        );
                    }
                    $result[$i] = NULL;
                    break;
                }
            }
        }
        
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Delete a boarding pass
Flight::route('DELETE /@bpid:[1-9][0-9]{0,10}', function($bpid) {
    $bpid = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        $sql  = 'DELETE FROM boarding_pass ';
        $sql .= 'WHERE id=:id AND user_id=:user_id';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $bpid,
            ':user_id'  => $user_info['user_id']
        ));


        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }

        # ERROR scenario. invalid or unknown boarding pass id.
        if($stmt->rowCount() !== 1) {
            error_response('x09', $error_list['x09']);
        }

        $response = array(
            'success' => 'true'
        );

        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Collect seatmate list
Flight::route('GET /seatmatelist/@bpid:[1-9][0-9]{0,10}', function($bpid) {
    $bpid = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        # Get flight information for the requested boarding pass.
        $sql  = 'SELECT id, user_id, carrier, flight_no, julian_date, year ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE id=:id AND user_id=:user_id ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $bpid,
            ':user_id'  => $user_info['user_id']
        ));
        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        # ERROR scenario. Invalid or unknown boarding pass id.
        if($stmt->rowCount() !== 1) {
            # send error RESPONSE for database failure.
            error_response('x09', $error_list['x09']);
        }
        $bprow = $stmt->fetchAll()[0];
        
        # Get user_id for all boarding passes other than this boarding pass which correspond to the same flight.
        $sql  = 'SELECT user_id ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE carrier=:carrier AND flight_no=:flight_no AND julian_date=:julian_date AND user_id<>:user_id ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':carrier'      => $bprow->carrier,
            ':flight_no'    => $bprow->flight_no,
            ':julian_date'  => $bprow->julian_date,
            ':user_id'      => $user_info['user_id']
        ));
        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        $result = $stmt->fetchAll();
        
        $sql  = 'SELECT id, CONCAT_WS(\' \', firstname, lastname) AS name, profession, image_name, image_type, image_content ';
        $sql .= 'FROM user ';
        $sql .= 'WHERE id IN ( ';
        
        foreach ($result as $row) {
            $sql .= $row->user_id.', ';
        }
        $sql = substr($sql, 0, strlen($sql)-2).' ) ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute();
        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        $result = $stmt->fetchAll();
        
        $response = array(
            'success'       => 'true',
            'count'         => count($result),
            'seatmatelist'  => array()
        );
        foreach ($result as $row) {
            $response['seatmatelist'][] = array(
                'id'            => $row->id,
                'name'          => $row->name,
                'profession'    => $row->profession,
                'image_name'    => $row->image_name,
                'image_type'    => $row->image_type,
                'image_content' => base64_encode($row->image_content)
            );
        }
        echo json_encode($response);
        return;
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Collect seatmate profile detail
Flight::route('GET /seatmate/@id:[1-9][0-9]{0,10}', function($id) {
    $id     = (int)$id;
//    $bpid   = (int)$bpid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {        
        $sql  = 'SELECT CONCAT_WS(\' \', firstname, lastname) AS name, gender, age, live_in, seating_pref, some_about_you, status ';
        $sql .= 'FROM user ';
        $sql .= 'WHERE id=:id ';
//        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $id
        ));
//        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        $seatmate_info = $stmt->fetchAll()[0];
        
        $response = array(
            'success' => 'true',
            'id' => $id,
            'seatmate_data_additional' => array(
                'name'              => $seatmate_info->name,
                'gender'            => $seatmate_info->gender,
                'age'               => $seatmate_info->age,
                'live_in'           => $seatmate_info->live_in,
                'seating_pref'      => $seatmate_info->seating_pref,
                'some_about_you'    => $seatmate_info->some_about_you,
                'status'            => $seatmate_info->status
            )
        );
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Send message (email) to a seatmate
Flight::route('POST /messagemate/@id:[1-9][0-9]{0,10}', function($id) {
    $id = (int)$id;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token',
        'message'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
        $sql  = 'SELECT email, CONCAT_WS(\' \', firstname, lastname) AS name ';
        $sql .= 'FROM   user ';
        $sql .= 'WHERE  id IN ( :user_id, :seatmate_id ) ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => (int)$user_info['user_id'],
            ':seatmate_id'  => (int)$id
        ));

        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }

        # ERROR scenario. invalid or unknown seatmate id.
        if($stmt->rowCount() !== 2) {
            error_response('x10', $error_list['x10']);
        }
        $result = $stmt->fetchAll();

        foreach ($result as $row) {
            if($row->email !== $user_info['email']) {
                $seatmate_email = $row->email;
                $seatmate_name = $row->name;
            } else {
                $user_name = $row->name;
            }
        }
        MailHandler::message_mate_mail_handler($seatmate_email, $json_obj->message, $user_name);

        $response = array(
            "success" => "true"
        );
        echo json_encode($response);
        return;
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }

});

# Collect info of shared flight with a particular seatmate
Flight::route('GET /sharedflight/@mateid:[1-9][0-9]{0,10}', function($mateid) {
    $mateid = (int)$mateid;
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token'
    );
    
    # ERROR scenario. JSON format validation.
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
    
    # ERROR scenario. None logged in.
    $user_info = is_logged_in($json_obj->token);
    
    $db = pdo_setup();
    
    try {
//        $sql  = 'SELECT p1.id ';
//        $sql .= 'FROM   ( ';
//        $sql .= '   SELECT  * ';
//        $sql .= '   FROM    boarding_pass ';
//        $sql .= '   WHERE	user_id=:user_id ';
//        $sql .= ') p1 ';
//        $sql .= 'JOIN	( ';
//        $sql .= '   SELECT  * ';
//        $sql .= '   FROM    boarding_pass ';
//        $sql .= '   WHERE   user_id=:seatmate_id ';
//        $sql .= ') p2 ';
//        $sql .= 'ON     p1.carrier=p2.carrier ';
//        $sql .= 'AND    p1.flight_no=p2.flight_no ';
//        $sql .= 'AND    p1.julian_date=p2.julian_date ';
//        $sql .= 'AND    p1.year=p2.year ';
        
        $sql  = 'SELECT * ';
        $sql .= 'FROM boarding_pass ';
        $sql .= 'WHERE user_id=:user_id ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':user_id'      => $mateid
        ));

        $result = $stmt->fetchAll();
        
        $response = array(
            'success'   => 'true',
            'boarding_passes'    => array()
        );
        foreach ($result as $row) {
            $response['boarding_passes'][] = array(
                'id'                => $row->id,
                'firstname'         => $row->firstname,
                'lastname'          => $row->lastname,
                'travel_from'       => $row->travel_from,
                'travel_to'         => $row->travel_to,
                'carrier'           => $row->carrier,
                'flight_no'         => $row->flight_no,
                'julian_date'       => $row->julian_date,
                'compartment_code'  => $row->compartment_code,
                'seat'              => $row->seat,
                'departure'         => $row->departure,
                'arrival'           => $row->arrival,
            );
        }
        echo json_encode($response);
        return;
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
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