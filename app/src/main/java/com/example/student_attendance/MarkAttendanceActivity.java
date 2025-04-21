package com.example.student_attendance;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarkAttendanceActivity extends AppCompatActivity {

    Spinner spinnerAnnouncements;
    Button btnMarkAttendance;
    ArrayList<String> announcementTitles = new ArrayList<>();
    ArrayList<String> announcementIds = new ArrayList<>();
    ArrayList<String> subjectIds = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int selectedAnnouncementId, studentId, selectedSubjectId;

    FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        spinnerAnnouncements = findViewById(R.id.spinnerAnnouncements);
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);

        // Retrieve student_id from shared preferences as an integer
        SharedPreferences prefs = getSharedPreferences("student_session", MODE_PRIVATE);
        studentId = prefs.getInt("student_id", -1);

        if (studentId == -1) {
            Toast.makeText(this, "Invalid session data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, announcementTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnnouncements.setAdapter(adapter);

        spinnerAnnouncements.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAnnouncementId = Integer.parseInt(announcementIds.get(position));
                selectedSubjectId = Integer.parseInt(subjectIds.get(position)); // Get subjectId from spinner
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fetchAnnouncements();

        btnMarkAttendance.setOnClickListener(v -> authenticateBiometric(this::sendMarkRequest));
    }

    private void fetchAnnouncements() {
        String url = "http://192.168.70.200/phpProject/get_announcements.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getString("status").equals("success")) {
                    JSONArray announcements = json.getJSONArray("announcements");

                    // Clear previous lists
                    announcementTitles.clear();
                    announcementIds.clear();
                    subjectIds.clear();

                    for (int i = 0; i < announcements.length(); i++) {
                        JSONObject obj = announcements.getJSONObject(i);
                        announcementTitles.add(obj.getString("title"));
                        announcementIds.add(String.valueOf(obj.getInt("announcement_id")));
                        subjectIds.add(String.valueOf(obj.getInt("subject_id")));
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "No announcements found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing announcements", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "Error fetching announcements", Toast.LENGTH_SHORT).show();
        });

        Volley.newRequestQueue(this).add(request);
    }

    private void authenticateBiometric(Runnable onSuccess) {
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        onSuccess.run();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(getApplicationContext(), "Biometric authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Confirm fingerprint to mark attendance")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void sendMarkRequest() {
        String url = "http://192.168.70.200/phpProject/mark_attendance.php";

        // Create a request to send student and announcement data to PHP server
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response);

                String status = jsonResponse.getString("status");
                String message = jsonResponse.getString("message");

                // Display the status message
                Toast.makeText(MarkAttendanceActivity.this, message, Toast.LENGTH_SHORT).show();

                // Optionally handle different statuses
                if ("success".equals(status)) {
                    // Do something upon success (e.g., finish activity or update UI)
                } else {
                    // Handle error scenarios
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MarkAttendanceActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            // Handle Volley error (e.g., network issues)
            Toast.makeText(MarkAttendanceActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Send student_id and announcement_id to the server
                Map<String, String> params = new HashMap<>();
                params.put("student_id", String.valueOf(studentId));
                params.put("announcement_id", String.valueOf(selectedAnnouncementId));
                return params;
            }
        };

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(request);
    }

}
