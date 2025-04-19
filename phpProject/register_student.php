<?php
include 'db_config.php'; // DB connection

header("Content-Type: application/json");

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
    exit();
}

// Collect input data safely
$name = trim($_POST['name'] ?? '');
$roll = trim($_POST['roll_number'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = $_POST['password'] ?? '';
$device_id = $_POST['device_id'] ?? '';
$fingerprint_hash = $_POST['fingerprint_hash'] ?? '';

// Basic validation
if (empty($name) || empty($roll) || empty($email) || empty($password)) {
    echo json_encode(["status" => "error", "message" => "Please fill all required fields."]);
    exit();
}

// Secure password
$hashed_password = password_hash($password, PASSWORD_BCRYPT);

$stmt = $conn->prepare("INSERT INTO students (name, roll_number, email, password, device_id, fingerprint_hash) VALUES (?, ?, ?, ?, ?, ?)");
$stmt->bind_param("ssssss", $name, $roll, $email, $hashed_password, $device_id, $fingerprint_hash);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Registered successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Error: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
