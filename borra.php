<?php
	$con = new mysqli('mysql.hostinazo.com', 'u589659455_spect', 'klm,.-12', 'u589659455_spect');
    if ($con->connect_errno) {
        echo 'Error al conectar base de datos: ', $con->connect_error;
        exit();
    }
	
	$id  = $_GET['id']; 
    
	$sql = 'DELETE FROM espectros WHERE _id = '.$id;
 
	$con->query ($sql);
    echo 'OK'."\n";
    $con->close();
?>