package com.example.student_attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherAttendanceActivity extends AppCompatActivity {

    private ListView listViewStudents;
    private Spinner subjectSpinner;
    private List<Student> studentList = new ArrayList<>();
    private List<String> subjectNames = new ArrayList<>();
    private List<String> subjectIds = new ArrayList<>();
    private ArrayAdapter<String> subjectAdapter;
    private String selectedSubjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        listViewStudents = findViewById(R.id.listViewStudents);
        subjectSpinner = findViewById(R.id.spinnerSubject);

        SharedPreferences prefs = getSharedPreferences("teacher_session", MODE_PRIVATE);
        String teacherId = prefs.getString("teacher_id", null);
        Log.d("DEBUG", "Teacher ID in Attendance: " + teacherId);

        if (teacherId == null) {
            Toast.makeText(this, "Teacher ID is missing. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectNames);
        subjectSpinner.setAdapter(subjectAdapter);
        setupSubjectSpinner();

        fetchSubjects(teacherId);

        listViewStudents.setOnItemClickListener((parent, view, position, id) -> {
            Student selectedStudent = studentList.get(position);
            Intent intent = new Intent(TeacherAttendanceActivity.this, StudentAttendanceDetailActivity.class);
            intent.putExtra("student_id", selectedStudent.getId());
            intent.putExtra("subject_id", selectedSubjectId);
            startActivity(intent);
        });

    }

    private void setupSubjectSpinner() {
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSubjectId = subjectIds.get(position); // use subject ID
                fetchStudentsForSubject(selectedSubjectId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
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
                        } else {
                            Toast.makeText(this, "No subjects found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
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

    private void fetchStudentsForSubject(String subjectId) {
        String url = "http://192.168.132.247/phpProject/fetch_students.php"; // Modify this if subject_id is needed

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        studentList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String studentId = jsonObject.getString("id");
                            String studentName = jsonObject.getString("name");
                            studentList.add(new Student(studentId, studentName));
                        }

                        ArrayAdapter<Student> studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
                        listViewStudents.setAdapter(studentAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch students", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
