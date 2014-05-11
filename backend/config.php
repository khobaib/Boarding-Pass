<?php

$host = "localhost";
$dbname = "seatunity";
$username = "cumhoc";
$password = "ergopropterhoc";


error_reporting(E_ALL | E_STRICT);
ini_set('display_errors', 1);

ini_set('session.use_cookies', 0);
ini_set('session.use_only_cookies', 0);
ini_set('session.use_trans_sid', 1);
ini_set('session.gc_maxlifetime', 360);

define('LAST_ACTIVITY', 30*60);
define('CREATED', 24*60*60);
define('PROVISIONAL_PASS_EXPIRE', 24*60*60);


$error_list = array(
    'x00' => 'invalid request JSON format',
    'x01' => 'database failure',
    'x02' => 'invalid email',
    'x03' => 'email in use',
    'x04' => 'wrong email and password combination',
    'x05' => 'none logged in',
    'x06' => 'email not registered',
    'x07' => 'provisional password expired. Either already logged in once with it or waited longer than 24 hours',
    'x08' => 'must change provisional password',
    'x09' => 'invalid or unknown boarding pass id',
    'x10' => 'invalid or unknown seatmate id',
    'x11' => 'boarding pass already added',
    'x12' => 'invalid or unknown boarding pass or seatmate id'
);


# debug
define('HTML_LINE_BREAK', '<br />');
define('LINE_BREAK', "\n");