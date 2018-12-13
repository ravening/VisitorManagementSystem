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
$result = mysqli_query($link, "SELECT * FROM employee") or die(mysqli_error());
 
// check for empty result
if (mysqli_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["visitors"] = array();
 
    echo '<table style=\'border: solid 2px black;\'>
	    <tr>
		<th style=\'width:150px;border:2px solid black;\'>First name</th>
		<th style=\'width:150px;border:2px solid black;\'>Last name</th>
		<th style=\'width:150px;border:2px solid black;\'>Phone</th>
		<th style=\'width:150px;border:2px solid black;\'>Department</th>
		<th style=\'width:150px;border:2px solid black;\'>Badge Number</th>
		<th style=\'width:150px;border:2px solid black;\'>Checkin Time</th>
		<th style=\'width:150px;border:2px solid black;\'>Checkout Time</th>
	    </tr>';
    while ($row = mysqli_fetch_array($result)) {
        // temp user array
        $visitor= array();
        $visitor["id"] = $row["id"];
        $visitor["firstName"] = $row["first_name"];
        $visitor["lastName"] = $row["last_name"];
        $visitor["phone"] = $row["phone"];
	$visitor["department"] = $row["department"];
        $visitor["badgeNumber"] = $row["badge_number"];
        $visitor["checkinTime"] = $row["checkin_time"];
        $visitor["checkoutTime"] = $row["checkout_time"];
 
	echo '
	    <tr>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["firstName"].'</td>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["lastName"].'</td>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["phone"].'</td>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["department"].'</td>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["badgeNumber"].'</td>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["checkinTime"].'</td>
		<td style=\'width:150px;border:2px solid black;\'>'.$visitor["checkoutTime"].'</td>
	    </tr>';
        // push single product into final response array
        array_push($response["visitors"], $visitor);
    }
	echo '
		</table>';
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    $out = array_values($response);
    #echo json_encode($out);
    #echo json_encode($response, JSON_PRETTY_PRINT);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No employees found";
 
    // echo no users JSON
    echo json_encode($response, JSON_PRETTY_PRINT);
}

