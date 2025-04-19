<?php
include 'db.php';

header('Content-Type: application/json'); // Always return JSON

if (!isset($_POST['student_id'], $_POST['announcement_id'])) {
    echo json_encode(["status" => "error", "message" => "Missing parameters."]);
    exit;
}

$student_id = (int)$_POST['student_id'];
$announcement_id = (int)$_POST['announcement_id'];

// Step 1: Fetch announcement time and subject_id
$query = "SELECT created_at, subject_id FROM announcements WHERE announcement_id = ?";
$stmt = $conn->prepare($query);

if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("i", $announcement_id);
$stmt->execute();
$stmt->bind_result($created_at, $subject_id);

if (!$stmt->fetch()) {
    echo json_encode(["status" => "error", "message" => "Invalid announcement ID."]);
    $stmt->close();
    $conn->close();
    exit;
}
$stmt->close();

// Step 2: Check time limit
$current_time = new DateTime();
$announcement_time = new DateTime($created_at);
$interval = $current_time->getTimestamp() - $announcement_time->getTimestamp();

if ($interval > 60) {
    echo json_encode(["status" => "error", "message" => "Time limit exceeded."]);
    exit;
}

// Step 3: Check if attendance already marked
$checkStmt = $conn->prepare("SELECT id FROM attendance WHERE student_id = ? AND announcement_id = ?");
if (!$checkStmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}
$checkStmt->bind_param("ii", $student_id, $announcement_id);
$checkStmt->execute();
$checkStmt->store_result();

if ($checkStmt->num_rows > 0) {
    echo json_encode(["status" => "error", "message" => "Attendance already marked."]);
    $checkStmt->close();
    $conn->close();
    exit;
}
$checkStmt->close();

// Step 4: Insert attendance
$insertStmt = $conn->prepare("INSERT INTO attendance (student_id, subject_id, announcement_id) VALUES (?, ?, ?)");
if (!$insertStmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}

$insertStmt->bind_param("iii", $student_id, $subject_id, $announcement_id);

if ($insertStmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Attendance marked successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed to mark attendance: " . $insertStmt->error]);
}

$insertStmt->close();
$conn->close();
?>
