
<?php
include 'db.php';

$student_id = $_POST['student_id'];

$stmt = $conn->prepare("
    SELECT a.attendance_id, s.subject_name, an.title, a.timestamp
    FROM attendance a
    JOIN announcements an ON a.announcement_id = an.announcement_id
    JOIN subjects s ON an.subject_id = s.subject_id
    WHERE a.student_id = ?
    ORDER BY a.timestamp DESC
");
$stmt->bind_param("i", $student_id);
$stmt->execute();
$result = $stmt->get_result();

$response = ["status" => "success", "records" => []];

while ($row = $result->fetch_assoc()) {
    $response["records"][] = [
        "attendance_id" => $row["attendance_id"],
        "subject_name" => $row["subject_name"],
        "title" => $row["title"],
        "timestamp" => $row["timestamp"]
    ];
}

echo json_encode($response);
?>
