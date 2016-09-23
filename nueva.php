<?php
	$con = new mysqli('mysql.hostinazo.com', 'u589659455_spect', 'klm,.-12', 'u589659455_spect');
    if ($con->connect_errno) {
        echo 'Error al conectar base de datos: ', $con->connect_error;
        exit();
    }
    $nombre = htmlspecialchars($_GET['nombre']); 
    $valores = htmlspecialchars($_GET['valores']);
    $fecha  = $_GET['fecha']; 
 
    $sql = 'INSERT INTO espectros VALUES ( null, "'.$nombre.'", "'.$valores.'", '.$fecha.')';
    $con->query ($sql);
    echo 'OK'."\n";
    $con->close();
?>