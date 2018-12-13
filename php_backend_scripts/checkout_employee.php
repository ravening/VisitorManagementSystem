<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
$column_id = 1;

// check for required fields
if (isset($_POST['badgeNumber']) && isset($_POST['checkoutTime'])) {
    $badgeNumber = $_POST['badgeNumber'];
    $checkoutTime = $_POST['checkoutTime'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    $link = $db->link;

    // mysql inserting a new row
    $result = mysqli_query($link, "SELECT * FROM employee WHERE badge_number=$badgeNumber AND checkout_time IS NULL ORDER BY id DESC LIMIT 1");

    if (mysqli_num_rows($result) > 0) {
        $response["employees"] = array();
	while($row = mysqli_fetch_array($result)) {
	    $employee = array();	
	    $column_id = $row["id"];
	    $employee["firstName"] = $row["first_name"];
	    $employee["lastName"] = $row["last_name"]; 
	    array_push($response["employees"], $employee);
	}
    } else {
	$response["success"] = 404;
	$response["message"] = "Employee not found";

	echo(json_encode($response));
	exit(1);
    }

    $result = mysqli_query($link, "UPDATE employee SET checkout_time=\"$checkoutTime\" WHERE id=$column_id");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Employee checkedout successfully.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 500;
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
