<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
// check for required fields
if (isset($_POST['firstName']) && isset($_POST['lastName']) && isset($_POST['phoneNumber']) && isset($_POST['checkinTime']) &&
    isset($_POST['reasonForVisit']) && isset($_POST['contactPerson']) && isset($_POST['licensePlate']) && isset($_POST['badgeNumber']) ) {
 
    $firstName = $_POST['firstName'];
    $lastName = $_POST['lastName'];
    $phone = $_POST['phoneNumber'];
    $reasonForVisit = $_POST['reasonForVisit'];
    $contactPerson = $_POST['contactPerson'];
    $licensePlate = $_POST['licensePlate'];
    $badgeNumber = $_POST['badgeNumber'];
    $checkinTime = $_POST['checkinTime'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    $link = $db->link;

    // mysql inserting a new row
    $result = mysqli_query($link, "INSERT INTO visitor(first_name, last_name, phone, reason_for_visit, contact_person, license_plate, badge_number, checkin_time) 
	    VALUES('$firstName', '$lastName', '$phone', '$reasonForVisit', '$contactPerson', '$licensePlate', '$badgeNumber', '$checkinTime')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Visitor checkedin successfully.";

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
