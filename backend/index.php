<?php
require_once './flightphp/flight/Flight.php';
require_once './config.php';
require_once './functions.php';


Flight::route("POST /reg", function() {
    $req = Flight::request();
    $jsonarr = json_decode($req->body);
    var_dump($jsonarr);
});


Flight::route('GET /', function() {
    echo "Hello World";
});

session_setup();
Flight::start();