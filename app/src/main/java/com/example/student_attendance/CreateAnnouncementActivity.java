package com.example.student_attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class CreateAnnouncementActivity extends AppCompatActivity {

    EditText etTitle, etMessage, etStartTime, etEndTime;
    Button btnCreateAnnouncement;
    Spinner spinnerSubjects;
    TextView btnCreateSubject;

    ArrayList<String> subjectNames = new ArrayList<>();
    ArrayList<String> subjectIds = new ArrayList<>();
    ArrayAdapter<String> subjectAdapter;
    String selectedSubjectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);

        SharedPreferences prefs = getSharedPreferences("teacher_session", MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", null);
        Log.d("DEBUG", "Teacher ID in Announcement: " + teacherId);

        if (teacherId == null) {
            Toast.makeText(this, "Teacher ID is missing. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spinnerSubjects = findViewById(R.id.spinnerSubjects);
        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        btnCreateAnnouncement = findViewById(R.id.btnCreateAnnouncement);
        btnCreateSubject=findViewById(R.id.btnCreateSubject);

        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNames);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(subjectAdapter);

        spinnerSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubjectId = subjectIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCreateSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createSubjectIntent = new Intent(getApplicationContext(), CreateSubjectActivity.class);
                startActivity(createSubjectIntent);
            }
        });

        fetchSubjects(teacherId);

        btnCreateAnnouncement.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String message = etMessage.getText().toString();
            String startTime = etStartTime.getText().toString();
            String endTime = etEndTime.getText().toString();

            if (title.isEmpty() || message.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSubjectId.isEmpty()) {
                Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://192.168.35.247/phpProject/create_announcement.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                Toast.makeText(this, "Announcement created", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("teacher_id", teacherId);
                    params.put("subject_id", selectedSubjectId);
                    params.put("title", title);
                    params.put("message", message);
                    params.put("start_time", startTime);
                    params.put("end_time", endTime);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }

    private void fetchSubjects(String teacherId) {
        String url = "http://192.168.35.247/phpProject/get_subjects_by_teacher.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray subjectsArray = json.getJSONArray("subjects");
                            subjectNames.clear();
                            subjectIds.clear();
                            for (int i = 0; i < subjectsArray.length(); i++) {
                                JSONObject subject = subjectsArray.getJSONObject(i);
                                subjectNames.add(subject.getString("subject_name"));
                                subjectIds.add(subject.getString("subject_id"));
                            }
                            subjectAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error fetching subjects", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("teacher_id", teacherId);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
