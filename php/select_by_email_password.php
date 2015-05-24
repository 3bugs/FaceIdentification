<?php

//header('Content-type=application/json; charset=utf-8');

$response = array();

if (isset($_GET['email']) && isset($_GET['password'])) {
	$email = $_GET['email'];
	$password = $_GET['password'];

	require_once __DIR__ . '/db_config.php';
	$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

	if (mysqli_connect_errno()) {
		printf("Connect failed: %s\n", mysqli_connect_error());
		exit();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	if ($result = $db->query("SELECT * FROM users WHERE email='$email' AND password='$password'")) {

		$rowCount = $result->num_rows;

		if ($rowCount > 0) {
			$response["users"] = array();

			while ($row = $result->fetch_assoc()) {
				$users = array();
				$users["_id"] = $row["_id"];
				$users["name"] = iconv("tis-620", "utf-8", $row["name"]);
				$users["lastname"] = iconv("tis-620", "utf-8", $row["lastname"]);
				$users["email"] = $row["email"];
				$users["password"] = $row["password"];
				$users["phone"] = $row["phone"];
				$users["date_added"] = $row["date_added"];
		 
				array_push($response["users"], $users);
			}
			$response["success"] = 1;
			$response["login_success"] = 1;
		} else {
			$response["success"] = 1;
			$response["login_success"] = 0;
		}

		$result->close();
		
	} else {
		$response["success"] = 0;
		$response["message"] = "An error occurred while retrieving data.";
	}

	$db->close();
	/////////////////////////////////////////////////////////////////////////////////////////////

} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing.";
}

sleep(2);
echo json_encode($response);

?>