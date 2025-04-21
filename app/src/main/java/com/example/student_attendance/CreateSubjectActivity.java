package com.example.student_attendance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class CreateSubjectActivity extends AppCompatActivity {

    EditText etSubjectName, etSemester;
    Button btnCreateSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_subject);

        etSubjectName = findViewById(R.id.etSubjectName);
        etSemester = findViewById(R.id.etSemester);
        btnCreateSubject = findViewById(R.id.btnCreateSubject);


        btnCreateSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectName = etSubjectName.getText().toString().trim();
                String semester = etSemester.getText().toString().trim();

                if (subjectName.isEmpty() || semester.isEmpty()) {
                    Toast.makeText(CreateSubjectActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve teacher_id from SharedPreferences
                SharedPreferences prefs = getSharedPreferences("teacher_session", MODE_PRIVATE);
                String teacherId = prefs.getString("teacher_id", null);

                Log.d("DEBUG", "Teacher ID: " + teacherId);
                Toast.makeText(getApplicationContext(), "Teacher ID: " + teacherId, Toast.LENGTH_SHORT).show();

                if (teacherId == null) {
                    Toast.makeText(CreateSubjectActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "http://192.168.70.200/phpProject/create_subject.php"; // Replace with actual URL

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    String status = jsonResponse.getString("status");

                                    if (status.equals("success")) {
                                        Toast.makeText(CreateSubjectActivity.this, "Subject Created Successfully", Toast.LENGTH_SHORT).show();
                                        //String subjectId = jsonResponse.getString("subject_id");

                                        try {
//                                            String subjectId = jsonResponse.getString("subject_id");  // Ensure 'subject_id' is in the response
//
//                                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//                                            SharedPreferences.Editor editor = prefs.edit();
//                                            editor.putString("last_subject_id", subjectId);  // Store the subject_id
//                                            editor.apply();

                                            String subjectId = jsonResponse.getString("subject_id");

                                            // Save subject_id for later use (e.g., creating announcements)
                                            SharedPreferences appPrefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = appPrefs.edit();
                                            editor.putString("last_subject_id", subjectId);
                                            editor.apply();

                                            finish();  // Close the activity
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(CreateSubjectActivity.this, "Error retrieving subject ID", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(CreateSubjectActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(CreateSubjectActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateSubjectActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("subject_name", subjectName);
                        params.put("semester", semester);
                        params.put("teacher_id", teacherId); // Retrieved from session
                        return params;
                    }
                };

                Volley.newRequestQueue(CreateSubjectActivity.this).add(stringRequest);
            }
        });
    }
}

