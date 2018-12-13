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
 
$count = 0;
#$query = "set @result = (select curdate()); set @result = concat(@result, \" 00:00:00\"); select * from employee where checkin_time > @result";
$query = "set @result = (select curdate())";
$result = mysqli_query($link, $query) or die(mysqli_error($link));

$output = mysqli_fetch_assoc($result);
echo $output;
	exit(1);
$result = mysqli_query($link, "set @result = (select curdate()); set @result = concat(@result, \" 00:00:00\"); select * from employee where checkin_time > @result") or die(mysqli_error($link));
// get all products from products table
$result = mysqli_query($link, $query) ;
// check for empty result
if (mysqli_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["employees"] = array();
 
    while ($row = mysqli_fetch_array($result)) {
	    $count = $count + 1;
        // temp user array
        $employee= array();
        $employee["id"] = $row["id"];
        $employee["firstName"] = $row["first_name"];
        $employee["lastName"] = $row["last_name"];
        $employee["department"] = $row["department"];
        $employee["badgeNumber"] = $row["badge_number"];
        $employee["phone"] = $row["phone"];
        $employee["checkinTime"] = $row["checkin_time"];
        $employee["checkoutTime"] = $row["checkout_time"];
 
        // push single product into final response array
        array_push($response["employees"], $employee);
    }
    // success
    $response["success"] = 1;
 
    array_push($response["total"], $count);
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No employees found";
 
    // echo no users JSON
    echo json_encode($response);
}

