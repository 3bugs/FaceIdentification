<?php

//header('Content-type=application/json; charset=utf-8');

require_once __DIR__ . '/db_config.php';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}

$response = array();

if ($result = $db->query("SELECT * FROM users ORDER BY _id")) {

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
    } else {
        $response["success"] = 0;
        $response["message"] = "The database is empty.";
    }

    $result->close();
    
} else {
    $response["success"] = 0;
    $response["message"] = "An error occurred while retrieving data.";
}

$db->close();
echo json_encode($response);

?>