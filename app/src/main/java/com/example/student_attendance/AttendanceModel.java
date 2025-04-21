package com.example.student_attendance;

public class AttendanceModel {
    String subjectName;
    int percentage;

    public AttendanceModel(String subjectName, int percentage) {
        this.subjectName = subjectName;
        this.percentage = percentage;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getPercentage() {
        return percentage;
    }
}
