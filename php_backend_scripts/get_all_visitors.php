<?php
 
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
$link = $db->link;
 
// get all products from products table
$result = mysqli_query($link, "SELECT * FROM visitor") or die(mysqli_error());
 
// check for empty result
if (mysqli_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["visitors"] = array();
 
    while ($row = mysqli_fetch_array($result)) {
        // temp user array
        $visitor= array();
        $visitor["id"] = $row["id"];
        $visitor["firstName"] = $row["first_name"];
        $visitor["lastName"] = $row["last_name"];
        $visitor["phone"] = $row["phone"];
        $visitor["reasonForVisit"] = $row["reason_for_visit"];
	$visitor["contactPerson"] = $row["contact_person"];
	$visitor["licensePlate"] = $row["license_plate"];
        $visitor["badgeNumber"] = $row["badge_number"];
        $visitor["checkinTime"] = $row["checkin_time"];
        $visitor["checkoutTime"] = $row["checkout_time"];
 
        // push single product into final response array
        array_push($response["visitors"], $visitor);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No visitors found";
 
    // echo no users JSON
    echo json_encode($response, JSON_PRETTY_PRINT);
}

