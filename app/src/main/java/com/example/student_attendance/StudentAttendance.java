package com.example.student_attendance;

public class StudentAttendance {
    private String studentName;
    private int presentCount;
    private int totalCount;
    private float attendancePercentage;

    public StudentAttendance(String studentName, int presentCount, int totalCount, float attendancePercentage) {
        this.studentName = studentName;
        this.presentCount = presentCount;
        this.totalCount = totalCount;
        this.attendancePercentage = attendancePercentage;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public float getAttendancePercentage() {
        return attendancePercentage;
    }
}



