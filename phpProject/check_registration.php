<?php
include 'db.php';

$device_id = $_POST['device_id'] ?? '';
$fingerprint_hash = $_POST['fingerprint_hash'] ?? '';

// Validate required fields
if (empty($device_id) || empty($fingerprint_hash)) {
    echo json_encode(['status' => 'error', 'message' => 'Device ID or fingerprint hash missing']);
    exit();
}

// Check if the device is already registered
$sql = "SELECT * FROM students WHERE device_id = ? OR fingerprint_hash = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $device_id, $fingerprint_hash);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(['status' => 'error', 'message' => 'Device is already registered']);
} else {
    echo json_encode(['status' => 'success', 'message' => 'Device is not registered']);
}

$stmt->close();
$conn->close();
?>
