<?php
	$con = new mysqli('mysql.hostinazo.com', 'u589659455_spect', 'klm,.-12', 'u589659455_spect');
	if ($con->connect_errno) {
        echo 'Error al conectar base de datos: ', $con->connect_error;
		exit();
	}
 
    $sql = 'SELECT valores, nombre FROM espectros ORDER BY fecha DESC';
 
	if (isset($_GET['max'])) {
		$sql .= ' LIMIT '.$_GET['max'];
	}
 
    $cursor = $con->stmt_init();
    if ($cursor->prepare($sql)) {
		$cursor->execute();
        $cursor->bind_result($valores, $nombre);
        while($cursor->fetch()) {
			echo $nombre.' '.$valores."\n";
		}
        echo "\n";
        $cursor->close();
	}
    $con->close();
?>