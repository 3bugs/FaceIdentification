<?php

//header('Content-type=application/json; charset=utf-8');

$response = array();

if (isset($_GET['name']) && isset($_GET['lastname']) && isset($_GET['email']) && isset($_GET['password']) && isset($_GET['phone'])) {
	$name = $_GET['name'];
	$lastname = $_GET['lastname'];
	$email = $_GET['email'];
	$password = $_GET['password'];
	$phone = $_GET['phone'];

	require_once __DIR__ . '/db_config.php';
	$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

	if (mysqli_connect_errno()) {
		printf("Connect failed: %s\n", mysqli_connect_error());
		exit();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	if ($result = $db->query("SELECT * FROM users WHERE email='$email'")) {

		$rowCount = $result->num_rows;
		$result->close();

		if ($rowCount > 0) {
			$response["success"] = 0;
			$response["message"] = "This e-mail already exists.";
		} else {
			if ($result = $db->query("INSERT INTO users (name, lastname, email, password, phone) VALUES ('$name', '$lastname', '$email', '$password', '$phone')")) {
				$response["success"] = 1;
			} else {
				$response["success"] = 0;
				$response["message"] = "An error occurred while saving data.";
			}
		}

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