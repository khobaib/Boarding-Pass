<?php

$db = NULL;

/**
 * Extracts the json fro the flightPHP request, decodes it and returns in a
 * stdClass object form.
 * @return stdClass
 */
function get_the_json() {
    # get the request.
    $req = Flight::request();
    
    # get the request JSON and decode it and return it.
    return json_decode($req->body);
}
# function get_the_json() ends.

/**
 * Sets up PDO,
 * sets PDO error mode to exception,
 * sets default fetch mode to object.
 * return the initialized PDO reference.
 * 
 * @global NULL or \PDO $db
 * @global string $host
 * @global string $dbname
 * @global string $username
 * @global string $password
 * @return \PDO
 */
function pdo_setup() {
    global $db, $host, $dbname, $username, $password;
    if($db == NULL) {
        try {
            $db = new PDO("mysql:host={$host};dbname={$dbname}", $username, $password);
            $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $db->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_OBJ);
        } catch (PDOException $e) {
            error_response('x01', $error_list['x01'].' '.$e->getMessage());
            return;
        }

    }
    return $db;
}
# function pdo_setup() ends.

/**
 * Called by arraywalk function (inside json_schema_validation())to check whether the request json object
 * is in valid formant with all necessary fields. Compares the json_object
 * against the $valid_json_schema.
 * 
 * @param type $value the value of a single array element.
 * @param type $key the key of a single array element.
 * @param type $ar a possible error list.
 */
function validation_callback($value, $key, $ar) {
    $validator = $ar[0];
    $json_obj = $ar[1];

    if(gettype($key) === 'integer') {
        if(!isset($json_obj->$value)) {
            $ar[2] = TRUE;
        }
    } else {
        if(is_array($validator[$key])) {
            if(isset($json_obj->$key)) {
                array_walk($validator[$key], 'validation_callback', array($validator[$key], $json_obj->$key, &$ar[2]));
            } else {
                $ar[2] = TRUE;
            }
        } else {
            if(!isset($json_obj->$key)) {
                $ar[2] = TRUE;
            }
        }
    }
}
# function validation_callback() ends.

/**
 * Validates the json object against the schema.
 * 
 * @param array $valid_json_schema
 * @param stdClass $json_obj
 * @param array $error_list
 * @return boolean TRUE if there is error. FALSE otherwise.
 */
function json_schema_validation($valid_json_schema, $json_obj) {
    global $error_list;
    # validate JSON request by ensuring that all fields are present in right format.
    
    # keep track of schema validation error.
    $is_error = FALSE;
    # recursively check every fields for their existence.
    array_walk($valid_json_schema, 'validation_callback', array($valid_json_schema, $json_obj, &$is_error));
    if($is_error) {
        # send error RESPONSE for JSON request is in invalid format.
        error_response('x00', $error_list['x00']);
        return TRUE;
    }
    return FALSE;
}
# function json_schema_validation() ends.

/**
 * Sends error response.
 * 
 * @param type $code
 * @param type $message
 */
function error_response($code, $message) {
    $response = array(
        'success' => 'false',
        'error' => array(
            $code => $message
        )
    );
    if(isset($_SESSION['user_id'])) {
        $user_id = $_SESSION['user_id'];
    } else {
        $user_id = 'none';
    }
    error_log('IP-'.Flight::request()->ip.' - uri: '.Flight::request()->base.' - method: '.Flight::request()->method.' - user_id: '.$user_id.' - '.$code.' - '.$message );
    echo json_encode($response);
}
# function error_response() ends.

/**
 * Check whether the user is logged in.
 * Logs user out automatically if there is no activity for a certain period of time.
 * Regenerates session and its id if it was created a certain period of time ago.
 * 
 * @param type $sess_id
 * @return boolean
 * @throws Exception
 */
function is_logged_in($sess_id) {
    
    global $error_list;
    
    session_id($sess_id);
    session_start();
    
    # log user out and destroy session if provisional password is expired.
    if(isset($_SESSION['PROVISIONAL'])) {
        if(time() - $_SESSION['PROVISIONAL'] > PROVISIONAL_PASS_EXPIRE) {
            # invalidate session.
            delete_session();
            # RETURN STATEMENT.
            return FALSE;
        }
    }
    
//    # log user out and destroy session if last activity is more than 30 mins (1800 sec) ago. (for debug: 2 mins)
//    # check to see $_SESSION['LAST_ACTIVITY'] is there. If not, there are some bad implementations.
//    if(isset($_SESSION['LAST_ACTIVITY'])) {
//        if(time() - $_SESSION['LAST_ACTIVITY'] > LAST_ACTIVITY) {
//            # invalidate session.
//            delete_session();
//            # RETURN STATEMENT.
//            return FALSE;
//        } else {
//            $_SESSION['LAST_ACTIVITY'] = time();
//        }
//    } else {
//        if(isset($_SESSION['email'])) {
//            throw new Exception("ERROR: LAST_ACTIVITY was not set. BAD implementation");
//        }
//        return FALSE;
//    }
    
//    # regenerate session id if the session is active for more than 1 day. (for debug: 30 mins)
//    # check to see $_SESSION['CREATED'] is there. If not, there are some bad implementations.
//    if(isset($_SESSION['CREATED'])) {
//        if(time() - $_SESSION['CREATED'] > CREATED) {
//            # regenerate new session id and also delete old associated session file. (the true argument)
//            session_regenerate_id(TRUE);
//            
//            # set $_SESSION['CREATED'] to current files.
//            $_SESSION['CREATED'] = time();
//            
//            # store info about the new session id in the database.
//            $db = pdo_setup();
//            $sql  = "UPDATE user ";
//            $sql .= "SET session_id=:sess_id ";
//            $sql .= "WHERE email=:email ";
//            
//            $stmt = $db->prepare($sql);
//            $suc = $stmt->execute(array(
//                ':sess_id'  => session_id(),
//                ':email'    => $_SESSION['email']
//            ));
//            
//            if(!($suc && $stmt->rowCount() === 1)) {
//                error_response('01', $error_list['01']);
//                return FALSE;
//            }
//        }
//    } else {
//        if(isset($_SESSION['email'])) {
//            throw new Exception("ERROR: CREATED was not set. BAD implementation");
//        }
//        return FALSE;
//    }
    
    # RETURN STATEMENT.
    return isset($_SESSION['email']);
}
# function is_logged_in() ends.

/**
 * When a user's session needs to be destroyed, it is called.
 * Destroys the session.
 * Deletes the database reference.
 * 
 * @throws Exception
 */
function delete_session() {
    # replace session record with empty string in the database.
    $db = pdo_setup();
    $sql  = 'UPDATE user ';
    $sql .= 'SET ';
    $sql .= 'session_id=:session_id ';
    $sql .= 'WHERE email=:email ';

    $stmt = $db->prepare($sql);
    $suc = $stmt->execute(array(
        ':email'        => $_SESSION['email'],
        ':session_id'   => ''
    ));
    
    # ERROR scenario. Database failure.
    if(!($suc && $stmt->rowCount() === 1)) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
    # delete the actual sessaion.
    session_unset();
    session_destroy();
}