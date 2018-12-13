<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
$link = $db->link;

// check for required fields
if (isset($_POST['employee'])) {
    $lastName= $_POST['lastName'];
    $phone = $_POST['phone'];
 
    $result = mysqli_query($link, "SELECT * FROM employee WHERE last_name=\"$lastName\" and phone=\"$phone\"");
    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_array($result)) {
	    $id = $row["id"];
	    $result1 = mysqli_query($link, "DELETE FROM employee WHERE id=$id");
	}
	$response["success"] = 1;
	$response["message"] = "Employee deleted successfully.";

	// echoing JSON response
	echo json_encode($response);
    } else {
        $response["success"] = 404;
        $response["message"] = "Employee not found";
	echo json_encode($response);
    }
} else if (isset($_POST['visitor'])) {
    $lastName= $_POST['lastName'];
    $phone = $_POST['phone'];
 
    $result = mysqli_query($link, "SELECT * FROM visitor WHERE last_name=\"$lastName\" and phone=\"$phone\"");
    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_array($result)) {
	    $id = $row["id"];
	    $result1 = mysqli_query($link, "DELETE FROM visitor WHERE id=$id");
	}
	$response["success"] = 1;
	$response["message"] = "Visitor deleted successfully.";

	// echoing JSON response
	echo json_encode($response);
    } else {
        $response["success"] = 404;
        $response["message"] = "Visitor not found";
	echo json_encode($response);
    }
} else if (isset($_POST['supplier'])) {
    $lastName= $_POST['lastName'];
    $phone = $_POST['phone'];
 
    $result = mysqli_query($link, "SELECT * FROM supplier WHERE last_name=\"$lastName\" and phone=\"$phone\"");
    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_array($result)) {
	    $id = $row["id"];
	    $result1 = mysqli_query($link, "DELETE FROM supplier WHERE id=$id");
	}
	$response["success"] = 1;
	$response["message"] = "Supplier deleted successfully.";

	// echoing JSON response
	echo json_encode($response);
    } else {
        $response["success"] = 404;
        $response["message"] = "Supplier not found";
	echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) are missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
