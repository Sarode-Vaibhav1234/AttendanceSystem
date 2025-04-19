<?php
include 'db.php';

// No filtering by teacher here for students
$query = "SELECT announcement_id, title, subject_id, created_at FROM announcements ORDER BY created_at DESC";
$result = $conn->query($query);

$announcements = [];
while ($row = $result->fetch_assoc()) {
    $announcements[] = [
        "announcement_id" => $row['announcement_id'],
        "title" => $row['title'],
        "subject_id" => $row['subject_id'],
        "created_at" => $row['created_at']
    ];
}

echo json_encode(["status" => "success", "announcements" => $announcements]);
$conn->close();
?>
