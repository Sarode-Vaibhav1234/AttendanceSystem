
<?php
// student_login.php
include "db.php"; // Your DB connection

$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $roll_number = $_POST['roll_number'];
    $password = $_POST['password'];

    $stmt = $conn->prepare("SELECT * FROM students WHERE roll_number = ?");
    $stmt->bind_param("s", $roll_number);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 1) {
        $student = $result->fetch_assoc();
        if ($student['password'] === $password) {
            $response['status'] = 'success';
            $response['student_id'] = $student['student_id'];
            $response['name'] = $student['name'];
        } else {
            $response['status'] = 'error';
            $response['message'] = 'Incorrect password';
        }
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Student not found';
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Invalid request method';
}

echo json_encode($response);
?>
