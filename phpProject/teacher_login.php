
<?php
// teacher_login.php (Handles teacher login request)
include 'db.php';

// Receive the posted data from the Android app
$email = $_POST['email'];
$password = $_POST['password'];

// Check if the email exists
$sql = "SELECT * FROM teachers WHERE email='$email'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // Teacher found, now verify the password
    $teacher = $result->fetch_assoc();
    if ($password == $teacher['password']) {
        // Password matches, send success response
        echo json_encode(['status' => 'success', 'teacher_id' => $teacher['teacher_id'], 'name' => $teacher['name']]);
    } else {
        // Incorrect password
        echo json_encode(['status' => 'error', 'message' => 'Incorrect password']);
    }
} else {
    // Teacher not found
    echo json_encode(['status' => 'error', 'message' => 'Email not registered']);
}

$conn->close();
?>
