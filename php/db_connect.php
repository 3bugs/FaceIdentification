<?php

define('DB_USER', "mydb_admin");
define('DB_PASSWORD', "plv111");
define('DB_DATABASE', "promlert_mydb");
define('DB_SERVER', "localhost");

class DB_CONNECT {
 
    function __construct() {
        $this->connect();
    }
 
    function __destruct() {
        $this->close();
    }
 
    function connect() {
        require_once __DIR__ . '/db_config.php';
 
        $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die(mysql_error());
        $db = mysql_select_db(DB_DATABASE) or die(mysql_error());
 
        return $con;
    }
 
    function close() {
        mysql_close();
    }
}

?>