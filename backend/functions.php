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
    global $db, $host, $dbname, $username, $password, $error_list;
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
    exit;
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
function is_logged_in($token) {
    
    global $error_list;
    
    $db = pdo_setup();
//    session_id($sess_id);
//    session_start();
    
    $sql  = 'SELECT id, email, is_pass_provisional ';
    $sql .= 'FROM user ';
    $sql .= 'WHERE token=:token ';
    
    $stmt = $db->prepare($sql);
    $suc = $stmt->execute(array(
        ':token'    => $token
    ));
    # ERROR scenario. Database failure.
    if(!$suc) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
    }
    if($stmt->rowCount() !== 1) {
        error_response('x05', $error_list['x05']);
    }
    
    $result = $stmt->fetchAll();
    $is_prov = (int)$result[0]->is_pass_provisional;
    # log user out and destroy session if provisional password is expired.
    if($is_prov !== -1 && $is_prov !== 0) {
        if(time() - $is_prov > PROVISIONAL_PASS_EXPIRE) {
            # invalidate session.
            delete_token($result[0]->email);
            # RETURN STATEMENT.
            return FALSE;
        }
    }
    
    # RETURN STATEMENT.
    return array(
        'user_id'   => $result[0]->id,
        'email'     => $result[0]->email
    );
}
# function is_logged_in() ends.

/**
 * When a user's session needs to be destroyed, it is called.
 * Destroys the session.
 * Deletes the database reference.
 * 
 * @throws Exception
 */
function delete_token($email) {
    # replace session record with empty string in the database.
    $db = pdo_setup();
    $sql  = 'UPDATE user ';
    $sql .= 'SET ';
    $sql .= 'token=:token ';
    $sql .= 'WHERE email=:email ';

    $stmt = $db->prepare($sql);
    $suc = $stmt->execute(array(
        ':email'        => $email,
        ':token'   => ''
    ));
    
    # ERROR scenario. Database failure.
    if(!($suc && $stmt->rowCount() === 1)) {
        # send error RESPONSE for database failure.
        error_response('x01', $error_list['x01']);
        return;
    }
//    # delete the actual sessaion.
//    session_unset();
//    session_destroy();
}

Class MailHandler {
    public static function confirm_mail_handler($email_to, $confirmation_id) {
        $confirm_link = $_SERVER['HTTP_HOST'].'/confirm/'.$confirmation_id;
        
        $from = 'noreply@seatunity.com';
        
        $headers  = 'MIME-Version: 1.0' . "\r\n";
        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
        $headers .= 'From: '.$from."\r\n";
        
        # get these from database?
        $subject = 'SeatUnity: Registration Confirmation';
        $description = 'Please follow the link below to confirm registration:';
        
        ob_start();
        include './mail_template/confirmation_tpl.php';
        $body = ob_get_contents();
        ob_end_clean();
        
        return mail($email_to, $subject, $body, $headers);
    }
    
    public static function pass_reset_mail_handler($email_to, $prov_pass) {
        $from = 'noreply@seatunity.com';        
        
        $headers  = 'MIME-Version: 1.0' . "\r\n";
        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
        $headers .= 'From: '.$from."\r\n";
        
        # get these from database?
        $subject = 'Password Reset for SeatUnity';
        
        $info = 'Your password has been reset.';
        $instruction = 'Use the following password to login: ';
        $rule = 'You can use it once within 24 hours from now';
        
        ob_start();
        include'./mail_template/pass_reset_tpl.php';
        $body = ob_get_contents();
        ob_end_clean();
        
        return mail($email_to, $subject, $body, $headers);
    }
    
    public static function message_mate_mail_handler($to, $message, $user_name) {
        $headers  = 'MIME-Version: 1.0' . "\r\n";
        $headers .= 'Content-type: text/html; charset=iso-utf-8' . "\r\n";
        $headers .= 'From: messageservice@seatunity.com\r\n';
        
        $subject = $user_name.' <'.$_SESSION['email'].'> messaged you via seatunity';
        
        return mail($to, $subject, $message, $headers);
    }
}

function generate_token() {
//    return sha1(uniqid());
    return uniqid();
}