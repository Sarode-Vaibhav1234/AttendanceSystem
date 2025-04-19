<?php
include 'db.php';

// Validate and fetch POST data
$subject_id = $_POST['subject_id'] ?? '';
$teacher_id = $_POST['teacher_id'] ?? '';
$title = $_POST['title'] ?? '';
$message = $_POST['message'] ?? '';
$start_time = $_POST['start_time'] ?? '';
$end_time = $_POST['end_time'] ?? '';

// Validate required fields
if (
    empty($subject_id) || empty($teacher_id) || empty($title) ||
    empty($start_time) || empty($end_time)
) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
    exit();
}

// Validate subject_id exists
$check_sql = "SELECT * FROM subjects WHERE subject_id = ?";
$stmt_check = $conn->prepare($check_sql);
$stmt_check->bind_param("i", $subject_id);
$stmt_check->execute();
$result_check = $stmt_check->get_result();

if ($result_check->num_rows === 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid subject ID']);
    $stmt_check->close();
    $conn->close();
    exit();
}
$stmt_check->close();

// Insert announcement
$sql = "INSERT INTO announcements 
    (subject_id, teacher_id, title, message, start_time, end_time)
    VALUES (?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);
$stmt->bind_param(
    "iissss",
    $subject_id,
    $teacher_id,
    $title,
    $message,
    $start_time,
    $end_time
);

if ($stmt->execute()) {
    echo json_encode(['status' => 'success']);
} else {
    echo json_encode(['status' => 'error', 'message' => $stmt->error]);
}

$stmt->close();
$conn->close();
?>
