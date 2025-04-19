<?php
include 'db.php';

$teacher_id = $_POST['teacher_id'] ?? '';

if (empty($teacher_id)) {
    echo json_encode(['status' => 'error', 'message' => 'Missing teacher_id']);
    exit();
}

$sql = "SELECT subject_id, subject_name FROM subjects WHERE teacher_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$subjects = [];

while ($row = $result->fetch_assoc()) {
    $subjects[] = $row;
}

echo json_encode(['status' => 'success', 'subjects' => $subjects]);

$stmt->close();
$conn->close();
?>
