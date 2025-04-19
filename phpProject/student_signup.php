<?php
include 'db.php';

$name = $_POST['name'] ?? '';
$roll_number = $_POST['roll_number'] ?? '';
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';
$device_id = $_POST['device_id'] ?? '';
$fingerprint_hash = $_POST['fingerprint_hash'] ?? '';

// Validate required fields
if (empty($name) || empty($roll_number) || empty($email) || empty($password)) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
    exit();
}

// Hash the password for security
//$hashed_password = password_hash($password, PASSWORD_DEFAULT);

// Insert into students table
$sql = "INSERT INTO students (name, roll_number, email, password, device_id, fingerprint_hash) 
        VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssssss", $name, $roll_number, $email, $password, $device_id, $fingerprint_hash);

if ($stmt->execute()) {
    echo json_encode(['status' => 'success', 'message' => 'Student registered successfully']);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Error: ' . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
