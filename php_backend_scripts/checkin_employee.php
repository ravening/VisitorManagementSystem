<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
$file = "/tmp/temp"; 
// check for required fields
if (isset($_POST['firstName']) && isset($_POST['lastName']) && isset($_POST['department']) && isset($_POST['phoneNumber']) &&
    isset($_POST['badgeNumber']) && isset($_POST['checkinTime'])) {
 
    $firstName = $_POST['firstName'];
    $lastName = $_POST['lastName'];
    $department = $_POST['department'];
    $phone = $_POST['phoneNumber'];
    $badgeNumber = $_POST['badgeNumber'];
    $checkinTime = $_POST['checkinTime'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    $link = $db->link;

    // mysql inserting a new row
    $result = mysqli_query($link, "INSERT INTO employee(first_name, last_name, department, phone, badge_number, checkin_time) 
	    VALUES('$firstName', '$lastName', '$department', '$phone', '$badgeNumber', '$checkinTime')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Employee checkedin successfully.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
	$response["message"] = mysqli_error($link);

        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) are missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
