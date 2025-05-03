package com.example.student_attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    TextView tvTeacherName;
    LinearLayout btnCreateAnnouncement, btnViewAttendance, examManagement, timetable;
    Button csv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        tvTeacherName = findViewById(R.id.tvTeacherName);
        btnCreateAnnouncement = findViewById(R.id.takeAttendance);
        btnViewAttendance = findViewById(R.id.studentReports);
        csv=findViewById(R.id.csv);
        examManagement=findViewById(R.id.examManagement);
        timetable=findViewById(R.id.manageTimetable);

        // Retrieve teacher's name from Intent
        Intent intent = getIntent();
        String teacherName = intent.getStringExtra("teacher_name");
        tvTeacherName.setText("Welcome, " + teacherName);

        // Route to Create Subject

        // Route to Create Announcement
        btnCreateAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAnnouncementIntent = new Intent(TeacherDashboardActivity.this, CreateAnnouncementActivity.class);
                startActivity(createAnnouncementIntent);
            }
        });

        // Route to View Attendance
        btnViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAttendanceIntent = new Intent(TeacherDashboardActivity.this, TeacherAttendanceActivity.class);
                startActivity(viewAttendanceIntent);
            }
        });

        csv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Exam Schedule activity or show a Toast
                Intent intent = new Intent(getApplicationContext(), csvActivity.class);
                startActivity(intent);
            }
        });
        examManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Exam Schedule activity or show a Toast
                Intent intent=new Intent(getApplicationContext(), ExamSchedule.class);
                startActivity(intent);
            }
        });
        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Timetable activity or show a Toast
                Intent intent = new Intent(getApplicationContext(), studenttimeActivity.class);
                startActivity(intent);
            }
        });
    }
}
