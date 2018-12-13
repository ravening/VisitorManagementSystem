<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
$link = $db->link;
 
// check for post data
if (isset($_GET["badgeNumber"]) && isset($_GET["phone"])) {
    $badge = $_GET["badgeNumber"];
    $phone = $_GET["phone"];
 
    // get a product from products table
    $result = mysqli_query($link, "SELECT * FROM employee WHERE badge_number = $badge and phone = $phone");
 
    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
 
        $row = mysqli_fetch_array($result);
 
        $employee = array();
        $employee["id"] = $row["id"];
        $employee["firstName"] = $row["first_name"];
        $employee["lastName"] = $row["last_name"];
        $employee["department"] = $row["department"];
        $employee["badgeNumber"] = $row["badge_number"];
        $employee["phone"] = $row["phone"];
        $employee["checkinTime"] = $row["checkin_time"];
        $employee["checkoutTime"] = $row["checkout_time"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["employee"] = array();
 
            array_push($response["employee"], $employee);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No employee found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No employee found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
