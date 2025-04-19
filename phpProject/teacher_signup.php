

<?php
include 'db.php';

// Receive the posted data from the Android app
$name = $_POST['name'];
$email = $_POST['email'];
$password = $_POST['password'];  // In production, use hashed password
$created_at = date("Y-m-d H:i:s");  // Current timestamp

// Check if the email already exists
$sql = "SELECT * FROM teachers WHERE email='$email'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // If email exists, return error
    echo json_encode(['status' => 'error', 'message' => 'Email already exists']);
    exit;
}

// Hash the password (for security purposes)
//$password_hash = password_hash($password, PASSWORD_BCRYPT);

// Insert teacher into the database
$sql = "INSERT INTO teachers (name, email, password, created_at) VALUES ('$name', '$email', '$password', '$created_at')";

if ($conn->query($sql) === TRUE) {
    echo json_encode(['status' => 'success', 'message' => 'Teacher registered successfully']);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Error: ' . $conn->error]);
}

$conn->close();
?>
