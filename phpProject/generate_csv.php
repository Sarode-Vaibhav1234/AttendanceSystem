<?php
$host = "localhost";
$username = "root";
$password = "";
$database = "attendance_system";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get subject_name from GET request
$subject_name = isset($_GET['subject_name']) ? $_GET['subject_name'] : null;

if ($subject_name) {
    // Get subject_id from subject_name
    $stmt = $conn->prepare("SELECT subject_id FROM subjects WHERE subject_name = ?");
    $stmt->bind_param("s", $subject_name);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows == 0) {
        echo "Subject not found!";
        exit;
    }

    $subject = $result->fetch_assoc();
    $subject_id = $subject['subject_id'];

    // Fetch all students with roll number and name
    $students = [];
    $student_query = "SELECT student_id, roll_number, name FROM students";
    $student_result = $conn->query($student_query);
    if ($student_result) {
        while ($row = $student_result->fetch_assoc()) {
            $students[$row['student_id']] = [
                'roll_number' => $row['roll_number'],
                'name' => $row['name']
            ];
        }
    }

    // Fetch all lectures (announcements) for the subject
    $lectures = [];
    $stmt = $conn->prepare("SELECT announcement_id FROM announcements WHERE subject_id = ?");
    $stmt->bind_param("i", $subject_id);
    $stmt->execute();
    $lecture_result = $stmt->get_result();
    if ($lecture_result) {
        while ($row = $lecture_result->fetch_assoc()) {
            $lectures[] = $row['announcement_id'];
        }
    }

    // Prepare CSV header (Roll Number, Student Name, Lecture statuses)
    $csv_data = [];
    $header = ['Roll Number', 'Student Name'];
    foreach ($lectures as $index => $announcement_id) {
        $header[] = 'Lecture ' . ($index + 1);
    }
    $csv_data[] = $header;

    // Fill attendance for each student
    foreach ($students as $student_id => $info) {
        $row = [$info['roll_number'], $info['name']];
        foreach ($lectures as $announcement_id) {
            $stmt = $conn->prepare("SELECT attendance_id FROM attendance WHERE student_id = ? AND announcement_id = ?");
            $stmt->bind_param("ii", $student_id, $announcement_id);
            $stmt->execute();
            $att_result = $stmt->get_result();

            // Log for debugging
            error_log("Checking attendance for Roll Number: {$info['roll_number']}, Announcement ID: $announcement_id");

            if ($att_result && $att_result->num_rows > 0) {
                $row[] = 'P';
            } else {
                $row[] = 'A';
            }
        }
        $csv_data[] = $row;
    }

    // Output CSV
    header('Content-Type: text/csv');
    header('Content-Disposition: attachment; filename="' . $subject_name . '_attendance.csv"');
    $output = fopen('php://output', 'w');
    foreach ($csv_data as $row) {
        fputcsv($output, $row);
    }
    fclose($output);

} else {
    echo "No subject_name provided!";
}

$conn->close();
?>
