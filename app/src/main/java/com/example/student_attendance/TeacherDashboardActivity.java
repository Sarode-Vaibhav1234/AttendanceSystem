package com.example.student_attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherDashboardActivity extends AppCompatActivity {

    TextView tvTeacherName;
    LinearLayout btnCreateAnnouncement, btnViewAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        tvTeacherName = findViewById(R.id.tvTeacherName);
        btnCreateAnnouncement = findViewById(R.id.takeAttendance);
        btnViewAttendance = findViewById(R.id.studentReports);

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
    }
}
