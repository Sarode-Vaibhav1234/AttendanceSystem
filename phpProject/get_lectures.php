<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "attendance_system"; // Replace with your database name

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$day = $_GET['day']; // Now fetching day like 'Monday'

$sql = "SELECT * FROM timetable WHERE day = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $day);
$stmt->execute();

$result = $stmt->get_result();
$lectures = array();

while ($row = $result->fetch_assoc()) {
    $lectures[] = $row;
}

echo json_encode($lectures);

$conn->close();
?>
