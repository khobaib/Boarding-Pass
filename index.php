<?php
require_once './flightphp/flight/Flight.php';

Flight::route('GET /', function() {
    echo "Hello World";
});

Flight::start();