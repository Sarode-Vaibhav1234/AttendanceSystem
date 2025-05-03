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

    EditText etTitle, etMessage;
    Spinner spinnerSubjects, spinnerStartTime, spinnerEndTime;
    Button btnCreateAnnouncement;
    TextView btnCreateSubject;

    ArrayList<String> subjectNames = new ArrayList<>();
    ArrayList<String> subjectIds = new ArrayList<>();
    ArrayAdapter<String> subjectAdapter;
    String selectedSubjectId = "";

    String[] timeSlots = {
            "08:00 AM", "09:00 AM", "10:15 AM", "11:15 AM",
            "01:15 PM", "02:15 PM", "03:30 PM", "04:30 PM"
    };

    Map<String, String> startEndMap = new HashMap<>();

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
        spinnerStartTime = findViewById(R.id.spinnerStartTime);
        spinnerEndTime = findViewById(R.id.spinnerEndTime);
        btnCreateAnnouncement = findViewById(R.id.btnCreateAnnouncement);
        btnCreateSubject = findViewById(R.id.btnCreateSubject);

        // Fill time slot map
        startEndMap.put("08:00 AM", "09:00 AM");
        startEndMap.put("09:00 AM", "10:00 AM");
        startEndMap.put("10:15 AM", "11:15 AM");
        startEndMap.put("11:15 AM", "12:15 PM");
        startEndMap.put("01:15 PM", "02:15 PM");
        startEndMap.put("02:15 PM", "03:15 PM");
        startEndMap.put("03:30 PM", "04:30 PM");
        startEndMap.put("04:30 PM", "05:30 PM");

        // Spinner for subjects
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

        // Spinner for start time
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartTime.setAdapter(timeAdapter);

        spinnerStartTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStart = timeSlots[position];
                String correspondingEnd = startEndMap.get(selectedStart);
                int endIndex = Arrays.asList(timeSlots).indexOf(correspondingEnd);
                if (endIndex != -1) {
                    spinnerEndTime.setSelection(endIndex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerEndTime.setAdapter(timeAdapter);

        btnCreateSubject.setOnClickListener(v -> {
            Intent createSubjectIntent = new Intent(getApplicationContext(), CreateSubjectActivity.class);
            startActivity(createSubjectIntent);
        });

        fetchSubjects(teacherId);

        btnCreateAnnouncement.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String message = etMessage.getText().toString();
            String startTime = spinnerStartTime.getSelectedItem().toString();
            String endTime = spinnerEndTime.getSelectedItem().toString();

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Title and Message are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSubjectId.isEmpty()) {
                Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://192.168.132.247/phpProject/create_announcement.php";

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
        String url = "http://192.168.132.247/phpProject/get_subjects_by_teacher.php";

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
