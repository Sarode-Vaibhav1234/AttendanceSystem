
<?php
// create_subject.php (Handles subject creation request)
include 'db.php';

// Receive the posted data
$subject_name = $_POST['subject_name'];
$semester = $_POST['semester'];
$teacher_id = $_POST['teacher_id'];

// Insert new subject into the subjects table
$sql = "INSERT INTO subjects (subject_name, teacher_id, semester) 
        VALUES ('$subject_name', '$teacher_id', '$semester')";

if ($conn->query($sql) === TRUE) {
    $subject_id = $conn->insert_id; // ✅ Get the auto-incremented ID
    echo json_encode([
        'status' => 'success',
        'message' => 'Subject created successfully',
        'subject_id' => $subject_id // ✅ Send it in the response
    ]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Error: ' . $conn->error
    ]);
}

$conn->close();
?>

