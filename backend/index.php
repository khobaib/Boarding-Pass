<?php
require_once './flightphp/flight/Flight.php';
require_once './config.php';
require_once './functions.php';

# Registration
Flight::route('POST /reg', function() {
    echo 'I do registration';
});

# Login
Flight::route('POST /login', function() {
    echo 'I do login';
});

# Logout
Flight::route('POST /logout', function() {
    echo 'I do logout';
});

# Update user info
Flight::route('PUT /reg', function() {
    echo 'I update profile info';
});

# Update status
Flight::route('POST /statusupdate', function() {
    echo 'I do status update';
});

# Reset forgotten password and mail provisional pass
Flight::route('DELETE /passreset', function() {
    echo 'I do password reset';
});

# Send info of newly added boarding pass (BP)
Flight::route('POST /newbp', function() {
    echo 'I do new bp';
});

# Get details of a single boarding pass (BP)
Flight::route('GET /bpdetail/@id:[1-9][0-9]{0,2}', function($id) {
    echo 'I send details of a single boarding pass: '.$id;
});

# Get all boarding pass
Flight::route('GET /bplist', function() {
    echo 'I send bp list';
});

# Check for BP update (is it necessary (meaning in the spec)? if yes, it would 
# still be unnecessary, for it will accomplished by push notification)
Flight::route('GET /bpupdate', function() {
    echo 'I update bp';
});

# Delete a boarding pass
Flight::route('DELETE /@bpid:[1-9][0-9]{0,2}', function($bpid) {
    echo 'I delete bp';
});

# Collect seatmate list
Flight::route('GET /seatmatelist/@bpid:[1-9][0-9]{0,2}', function($bpid) {
    echo 'I collect seatmate list';
});

# Collect seatmate profile detail
Flight::route('GET /seatmate/@id:[1-9][0-9]{0,2}', function($id) {
    echo 'I collect a seatmate';
});

# Send message (email) to a seatmate
Flight::route('POST /messagemate/@bpid:[1-9][0-9]{0,2}/@mateid:[1-9][0-9]{0,2}', function($bpid, $mateid) {
    echo 'I message a seatmate';
});

# Collect info of shared flight with a particular seatmate
Flight::route('GET /sharedflight/@mateid:[1-9][0-9]{0,10}', function($mateid) {
    echo 'I show shared flight';
});

# ------------------------------------------------------------------------------
# General debug
Flight::route('GET /', function() {
    echo "Hello World!";
});

Flight::route('GET /debug', function() {
    echo "d..d..debug";
});

session_setup();
Flight::start();