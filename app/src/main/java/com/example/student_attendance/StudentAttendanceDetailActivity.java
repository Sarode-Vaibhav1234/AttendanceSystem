package com.example.student_attendance;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class StudentAttendanceDetailActivity extends AppCompatActivity {

    private TextView textViewAttendanceDetails;
    private String studentId;
    private String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_detail);

        textViewAttendanceDetails = findViewById(R.id.textViewAttendanceDetails);

        studentId = getIntent().getStringExtra("student_id");
        subjectId = getIntent().getStringExtra("subject_id");

        if (studentId != null && subjectId != null) {
            fetchAttendanceDetails(studentId, subjectId);
        } else {
            Toast.makeText(this, "Missing student or subject ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAttendanceDetails(String studentId, String subjectId) {
        String url = "http://192.168.132.247/phpProject/get_attendance_by_subject.php?student_id=" + studentId + "&subject_id=" + subjectId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equals("success")) {
                            int present = obj.getInt("present");
                            int total = obj.getInt("total");
                            double percent = obj.getDouble("percentage");

                            String display = "Subject ID: " + subjectId + "\n"
                                    + "Attendance: " + present + "/" + total + "\n"
                                    + "Percentage: " + percent + "%";

                            textViewAttendanceDetails.setText(display);
                        } else {
                            Toast.makeText(this, "Error: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
