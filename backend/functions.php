<?php

function session_setup() {
    error_reporting(E_ALL | E_STRICT);
    ini_set('session.use_cookies', 0);
    ini_set('session.use_only_cookies', 0);
    ini_set('session.use_trans_sid', 1);
    ini_set('display_errors', 1);

    unset($_GET['PHPSESSID']);
    if (isset($_SERVER['HTTP_TOKEN'])) {
        $_GET['PHPSESSID'] = $_SERVER['HTTP_TOKEN'];
    }
    session_start();

    if (!isset($_SESSION['ip_check']) or $_SESSION['ip_check'] != $_SERVER['REMOTE_ADDR']) {
        session_regenerate_id();
        session_destroy();
        session_start();
        $_SESSION['ip_check'] = $_SERVER['REMOTE_ADDR'];
    }
}