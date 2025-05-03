package com.example.student_attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    private LinearLayout academicCalendar, timetable, examSchedule;
    private LinearLayout viewAttendance, checkInOut, checkInOutSummary;
    private TextView tvUserName;
    private int studentId;
    private String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        SharedPreferences prefs = getSharedPreferences("student_session", MODE_PRIVATE);
        studentId = prefs.getInt("student_id", -1);
        studentName = prefs.getString("name", "");

        // Initialize the layouts
        academicCalendar = findViewById(R.id.academicCalendar);
        timetable = findViewById(R.id.timetable);
        examSchedule = findViewById(R.id.examSchedule);
        viewAttendance = findViewById(R.id.viewAttendance);
        checkInOut = findViewById(R.id.markAttendance);
        checkInOutSummary = findViewById(R.id.seeAttendance);
        tvUserName=findViewById(R.id.tvUserName);

        tvUserName.setText("Welcome, " + studentName);

        // Set onClick listeners for each layout

        // View Today's Attendance
        viewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start View Attendance activity or show a Toast
//                Intent intent = new Intent(getApplicationContext(), ViewAttendanceActivity.class);
//                startActivity(intent);
            }
        });

        // Academic Calendar
        academicCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Academic Calendar activity or show a Toast
                Intent intent = new Intent(getApplicationContext(), PdfViewerActivity.class);
                startActivity(intent);
            }
        });

        // Check-In/Out (Mark Attendance)
        checkInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Check-In/Out activity or show a Toast
                Intent intent = new Intent(StudentDashboardActivity.this, MarkAttendanceActivity.class);
                startActivity(intent);
            }
        });

        // Check-In/Out Summary
        checkInOutSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Check-In/Out Summary activity or show a Toast
                Intent intent = new Intent(StudentDashboardActivity.this, ViewFullAttendanceActivity.class);
                startActivity(intent);
            }
        });

        // Timetable
        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Timetable activity or show a Toast
                Intent intent = new Intent(getApplicationContext(), studenttimeActivity.class);
                startActivity(intent);
            }
        });

        // Exam Schedule
        examSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Exam Schedule activity or show a Toast
                Intent intent=new Intent(getApplicationContext(), ExamSchedule.class);
                startActivity(intent);
            }
        });

    }
}
