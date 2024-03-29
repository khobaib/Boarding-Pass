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
    
    global $error_list;
    
    # do server side validation.
    #   validate json request
    $valid_json_schema = array(
        'email',
        'password',
        'firstname',
        'lastname',
        'gender',
        'live_in',
        'age',
        'profession',
        'seating_pref',
        'language'
    );
    
    json_schema_validation($valid_json_schema, $json_obj);
    
    # initialize database.
    $db = pdo_setup();
    
    # ERROR scenario. email format validation.
    if(!$em = filter_var($json_obj->email, FILTER_VALIDATE_EMAIL)) {
        # Sending error RESPONSE for invalid email
        error_response('x02', $error_list['x02']);
        return;
    }
    
    try {
        # check to see whether the email is already in use in another account.
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
        
        $next_insert_id = get_next_insert_id($db);
        
//        $image_name = save_image_in_filesystem((object)array(
//            'name'      => $json_obj->image_name,
//            'type'      => $json_obj->image_type,
//            'content'   => $json_obj->image_content
//        ), PROFILE_IMG_DIR, $next_insert_id);
        
        $sql  = 'INSERT INTO user ( ';
        $sql .= 'email, password, language, is_reg_confirmed, firstname, lastname, gender, live_in, age, profession, seating_pref ';
        $sql .= ') VALUES ( ';
        $sql .= ':email, :password, :language, :is_reg_confirmed, :firstname, :lastname, :gender, :live_in, :age, :profession, :seating_pref ';
        $sql .= ') ';

        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email'            => $json_obj->email,
            ':password'         => md5($json_obj->password),
            ':language'         => $json_obj->language,
            # Commented out for dev purpose. Must be on production.
//            ':is_reg_confirmed' => $reg_confirm_id,
            ':is_reg_confirmed' => 1,
            ':firstname'        => $json_obj->firstname,
            ':lastname'         => $json_obj->lastname,
            ':gender'           => $json_obj->gender,
            ':live_in'          => $json_obj->live_in,
            ':age'              => $json_obj->age,
            ':profession'       => $json_obj->profession,
            ':seating_pref'     => $json_obj->seating_pref
        ));
        
        # ERROR scenario. Database failure.
        if(!($success && $stmt->rowCount() === 1)) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        # Commented out for development purpose
//        MailHandler::confirm_mail_handler($json_obj->optional->language, $json_obj->required->email, $reg_confirm_id);
        
        
        # send success RESPONSE.
        $response = array(
            "success" => "true"
        );
        
        Flight::json($response);
        
    } catch (PDOException $e) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01'].'. '.$e->getMessage());
    }
});


# Confirm registration
Flight::route('GET /confirm/@id:[0-9a-z]{10}', function($id) {
    
    global $error_list;
    
    $db = pdo_setup();
    
    $sql  = 'SELECT email, id, language ';
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
    
    $result = $stmt->fetch();
    
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
    
    Flight::render($result->language.'/confirm_redirect.php');
});

# Login
Flight::route('POST /login', function() {
    $json_obj = get_the_json();
    
    global $error_list;

    $valid_json_schema = array(
        'email',
        'password'
    );
    
    json_schema_validation($valid_json_schema, $json_obj);
    
    # validate user email and password.
    # initialize database.
    $db = pdo_setup();
    
    try {
        # check against the database whether the email and password combination are valid.
        $sql  = 'SELECT * ';
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
            'success'       => 'true',
            'token'         => $token,
            'language'      => $result[0]->language,
            'firstname'     => $result[0]->firstname,
            'lastname'      => $result[0]->lastname,
            'gender'        => $result[0]->gender,
            'live_in'       => $result[0]->live_in,
            'age'           => $result[0]->age,
            'profession'    => $result[0]->profession,
            'seating_pref'  => $result[0]->seating_pref,
            'some_about_you'=> $result[0]->some_about_you,
            'image_url'     => get_profile_img_url($result[0]->image_name)
        );
        
        if($is_prov > 0) {
            $response['PROVISIONAL'] = 'TRUE';
        }
        
        Flight::json($response);
        
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
    
    json_schema_validation($valid_json_schema, $json_obj, $error_list);
     
    $user_info = is_logged_in($json_obj->token);
    
    delete_token($user_info['email']);
    
    # send SUCCESS response.
    $response = array(
        'success' => 'true'
    );
    
    Flight::json($response);
});

# Update user info
Flight::route('PUT /reg', function() {
    
    global $error_list;
    
    $json_obj = get_the_json();
    
    $valid_json_schema = array(
        'token',
        'password',
        'language',
        'firstname',
        'lastname',
        'gender',
        'live_in',
        'age',
        'profession',
        'seating_pref',
        'some_about_you',
        'status',
        'image_name',
        'image_type',
        'image_content'
    );
    
    # ERROR scenario. JSON format validation.
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
        
        $image_name = save_image_in_filesystem((object)array(
            'name'      => $json_obj->image_name,
            'type'      => $json_obj->image_type,
            'content'   => $json_obj->image_content
        ), PROFILE_IMG_DIR, $user_info['user_id']);
        
        $sql  = 'UPDATE user ';
        $sql .= 'SET ';
        $sql .= 'password=:password, ';
        $sql .= 'language=:language, ';
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
        $sql .= 'image_name=:image_name ';
        $sql .= 'WHERE email=:email ';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':email'                => $user_info['email'],
            ':password'             => md5($json_obj->password),
            ':language'             => $json_obj->language,
            ':is_pass_provisional'  => 0,
            ':firstname'            => $json_obj->firstname,
            ':lastname'             => $json_obj->lastname,
            ':gender'               => $json_obj->gender,
            ':live_in'              => $json_obj->live_in,
            ':age'                  => $json_obj->age,
            ':profession'           => $json_obj->profession,
            ':seating_pref'         => $json_obj->seating_pref,
            ':some_about_you'       => $json_obj->some_about_you,
            ':status'               => $json_obj->status,
            ':image_name'           => $image_name
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
        
        Flight::json($response);
        
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
    
    # ERROR scenario. JSON format validation.
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
        
        Flight::json($response);
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
});

# Reset forgotten password and mail provisional pass
Flight::route('DELETE /passreset/@language', function($language) {
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
        
        MailHandler::pass_reset_mail_handler($language, $json_obj->email, $prov_pass);

        $response = array(
            'success' => 'true'
        );
        
        Flight::json($response);
        
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
            ':carrier'      => $json_obj->carrier,
            ':flight_no'    => $json_obj->flight_no,
            ':julian_date'  => $json_obj->julian_date
        ));
        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }


        if($stmt->rowCount() === 0) {
            # store in the database.
            $sql  = 'INSERT INTO boarding_pass ( ';
            $sql .= 'user_id, stringform, firstname, lastname, PNR, travel_from, travel_to, carrier, flight_no, julian_date, compartment_code, seat, departure, arrival, year ';
            $sql .= ') VALUES ( ';
            $sql .= ':user_id, :stringform, :firstname, :lastname, :PNR, :travel_from, :travel_to, :carrier, :flight_no, :julian_date, :compartment_code, :seat, :departure, :arrival, :year ';
            $sql .= ') ';

            $stmt = $db->prepare($sql);
            $success = $stmt->execute(array(
                ':user_id'          => $user_info['user_id'],
                ':stringform'       => $json_obj->stringform,
                ':firstname'        => $json_obj->firstname,
                ':lastname'         => $json_obj->lastname,
                ':PNR'              => $json_obj->PNR,
                ':travel_from'      => $json_obj->travel_from,
                ':travel_to'        => $json_obj->travel_to,
                ':carrier'          => $json_obj->carrier,
                ':flight_no'        => $json_obj->flight_no,
                ':julian_date'      => $json_obj->julian_date,
                ':compartment_code' => $json_obj->compartment_code,
                ':seat'             => $json_obj->seat,
                ':departure'        => $json_obj->departure,
                ':arrival'          => $json_obj->arrival,
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
        } else if($stmt->rowCount() === 1) {
            $result = $stmt->fetch();
            
            $sql  = 'UPDATE boarding_pass ';
            $sql .= 'SET ';
            $sql .= 'user_id=:user_id, ';
            $sql .= 'stringform=:stringform, ';
            $sql .= 'firstname=:firstname, ';
            $sql .= 'lastname=:lastname, ';
            $sql .= 'PNR=:PNR, ';
            $sql .= 'travel_from=:travel_from, ';
            $sql .= 'travel_to=:travel_to, ';
            $sql .= 'carrier=:carrier, ';
            $sql .= 'flight_no=:flight_no, ';
            $sql .= 'julian_date=:julian_date, ';
            $sql .= 'compartment_code=:compartment_code, ';
            $sql .= 'seat=:seat, ';
            $sql .= 'departure=:departure, ';
            $sql .= 'arrival=:arrival, ';
            $sql .= 'year=:year ';
            $sql .= 'WHERE id=:id ';
            
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
                ':year'             => $json_obj->year,
                ':id'               => $result->id
            ));

            # ERROR scenario. Database failure.
            if(!($success)) {
                # send error RESPONSE for database failure.
                error_response('x01', $error_list['x01']);
            }

            # send SUCCESS response.
            $response = array(
                'success'   => 'true',
                'id'        => $result->id
            );
        } else {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        Flight::json($response);
        
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
        );
        
        Flight::json($response);
        
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
            );
        }
        
        Flight::json($response);
        
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
                            'arrival'       => $result[$i]->arrival
                        );
                    }
                    $result[$i] = NULL;
                    break;
                }
            }
        }
        
        Flight::json($response);
        
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

        Flight::json($response);
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
    
});

# Collect seatmate list
//Flight::route('GET /seatmatelist/@bpid:[1-9][0-9]{0,10}', function($bpid) {
Flight::route('GET /seatmatelist/@carrier/@flight_no/@julian_date', function($carrier, $flight_no, $julian_date) {
    $julian_date = (int)$julian_date;
    
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
        $sql  = 'SELECT u.id, CONCAT_WS(\' \', u.firstname, u.lastname) AS name, u.profession, u.image_name, bp.seat ';
        $sql .= 'FROM user u, boarding_pass bp ';
        $sql .= 'WHERE bp.carrier=:carrier AND bp.flight_no=:flight_no AND bp.julian_date=:julian_date AND u.id=bp.user_id AND bp.user_id<>:user_id';
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':carrier'      => $carrier,
            ':flight_no'    => $flight_no,
            ':julian_date'  => $julian_date,
            ':user_id'      => $user_info['user_id']
        ));
        
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
            
            $image_path = getcwd().'/images/profile_images/'.$row->image_name;
            if(!is_file($image_path)) {
                $image_url = '';
            } else {
                $image_url = Flight::request()->base.'/images/profile_images/'.$row->image_name;
            }
            
            $response['seatmatelist'][] = array(
                'id'            => $row->id,
                'name'          => $row->name,
                'profession'    => $row->profession,
                'seat'          => $row->seat,
                'image_url'     => $image_url
            );
        }
        
        Flight::json($response);
        
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
        
        $stmt = $db->prepare($sql);
        $success = $stmt->execute(array(
            ':id'       => $id
        ));
        
        # ERROR scenario. Database failure.
        if(!$success) {
            # send error RESPONSE for database failure.
            error_response('x01', $error_list['x01']);
        }
        
        if($stmt->rowCount() !== 1) {
            # send error RESPONSE for invalid or unknown seatmate id.
            error_response('x10', $error_list['x10']);
        }
        
        $seatmate_info = $stmt->fetchAll()[0];
        
        $response = array(
            'success' => 'true',
            'id' => $id,
            'name'              => $seatmate_info->name,
            'gender'            => $seatmate_info->gender,
            'age'               => $seatmate_info->age,
            'live_in'           => $seatmate_info->live_in,
            'seating_pref'      => $seatmate_info->seating_pref,
            'some_about_you'    => $seatmate_info->some_about_you,
            'status'            => $seatmate_info->status
        );
        
        Flight::json($response);
        
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
                $user_info['name'] = $row->name;
            }
        }
        
        MailHandler::message_mate_mail_handler($seatmate_email, $json_obj->message, $user_info);

        $response = array(
            "success" => "true"
        );
        
        Flight::json($response);
        
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
        
        Flight::json($response);
        
    } catch (PDOException $exc) {
        error_response('x01', $error_list['x01'].' '.$exc->getMessage());
    }
});

# ------------------------------------------------------------------------------
# General debug
Flight::route('GET /', function() {
    $json_obj = get_the_json();
    
});

Flight::route('GET /debug', function() {
    echo "d..d..debug";
});

//session_setup();
Flight::start();