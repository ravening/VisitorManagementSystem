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
if (isset($_POST['all'])) {
    $result = mysqli_query($link, "DELETE FROM supplier");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "All suppliers deleted successfully.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
	$response["message"] = mysqli_error($link);

        // echoing JSON response
        echo json_encode($response);
    }
} else if (isset($_POST['threeMonths'])) {
    $result = mysqli_query($link, "SELECT * FROM supplier WHERE checkin_time < NOW() - INTERVAL 3 MONTH");

    if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_array($result)) {
            $id = $row["id"];
	    mysqli_query($link, "DELETE FROM supplier WHERE id=$id");
	}
    }

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Suppliers older than 3 months deleted successfully.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
	$response["message"] = mysqli_error($link);

        // echoing JSON response
        echo json_encode($response);
    }
} else if (isset($_POST['oneWeek'])) {
    $result = mysqli_query($link, "SELECT * FROM supplier WHERE checkin_time < NOW() - INTERVAL 1 WEEK");

    if (mysqli_num_rows($result) > 0) {
        while($row = mysqli_fetch_array($result)) {
            $id = $row["id"];
	    mysqli_query($link, "DELETE FROM supplier WHERE id=$id");
	}
    }

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Suppliers older than 1 week deleted successfully.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
	$response["message"] = mysqli_error($link);

        // echoing JSON response
        echo json_encode($response);
    }
} else if (isset($_POST['range'])) {
    if (isset($_POST['from']) && isset($_POST['to'])) {
        $from = $_POST['from'];
	$to = $_POST['to'];	
	$result = mysqli_query($link, "SELECT * FROM supplier WHERE checkin_time > \"$from\" AND checkin_time < \"$to\"");

        if (mysqli_num_rows($result) > 0) {
            while($row = mysqli_fetch_array($result)) {
                $id = $row["id"];
	        mysqli_query($link, "DELETE FROM supplier WHERE id=$id");
	    }
        }

        if ($result) {
        // successfully inserted into database
            $response["success"] = 1;
            $response["message"] = "Suppliers deleted successfully.";

            // echoing JSON response
            echo json_encode($response);
        } else {
            $response["success"] = 0;
	    $response["message"] = mysqli_error($link);

            // echoing JSON response
            echo json_encode($response);
        }
    } else {
        $response["success"] = 0;
        $response["message"] = "Required field(s) are missing";
 
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
