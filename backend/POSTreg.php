<?php

$baseuri = "http://localhost/Boarding-Pass";
$uriPath = "/reg";

$url = $baseuri.$uriPath;

$method = "POST";

$data = array(
    "required"=>array(
            "email" => "dummyemail@host.com",
            "password" => "mypass"
        ),
    "optional" => array(
            "first_name" => "Jane",
            "last_name" => "Shepherd",
            "gender" => "unspecified",
            "live_in" => "North Korea",
            "age" => 122,
            "profession" => "Skydiving",
            "seating_pref" => "small_talk"
        )
    );

$json = json_encode($data);


$ch = curl_init($url);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(
    "Content-Type: application/json",
    "Content-Length: ".strlen($json)
));

$result = curl_exec($ch);

echo $result;