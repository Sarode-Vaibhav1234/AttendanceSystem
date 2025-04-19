package com.example.student_attendance;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MarkAttendanceActivity extends AppCompatActivity {

    Spinner spinnerAnnouncements;
    Button btnMarkAttendance;
    ArrayList<String> announcementTitles = new ArrayList<>();
    ArrayList<String> announcementIds = new ArrayList<>();
    ArrayList<String> subjectIds = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int selectedAnnouncementId, studentId, selectedSubjectId;

    private String date;
    FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
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

        btnMarkAttendance.setOnClickListener(v -> authenticateBiometric(this::checkLocationAndMark));
    }

    private void fetchAnnouncements() {
        String url = "http://192.168.35.247/phpProject/get_announcements.php";

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

    private void checkLocationAndMark() {
        // Location checking is disabled. Directly sending attendance request.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
//            return;
//        }
//
//        locationClient.getLastLocation().addOnSuccessListener(location -> {
//            if (location != null) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                double altitude = location.getAltitude();
//
//                // Expected values for location and altitude
//                double expectedLat = 12.9716, expectedLng = 77.5946, expectedAlt = 920;
//                double radius = 50.0;
//
//                float[] results = new float[1];
//                Location.distanceBetween(latitude, longitude, expectedLat, expectedLng, results);
//                boolean withinRange = results[0] <= radius && Math.abs(altitude - expectedAlt) < 10;
//
//                if (withinRange) {
        sendMarkRequest();
//                } else {
//                    Toast.makeText(this, "Not within allowed location or altitude", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void sendMarkRequest() {
        String url = "http://192.168.35.247/phpProject/mark_attendance.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Trim and validate response format
                        String trimmedResponse = response.trim();
                        if (trimmedResponse.startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(trimmedResponse);

                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(MarkAttendanceActivity.this, "Attendance marked!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarkAttendanceActivity.this, "Failed to mark attendance.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("MarkAttendance", "Invalid server response (not JSON):\n" + trimmedResponse);
                            Toast.makeText(MarkAttendanceActivity.this, "Unexpected server response.", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("MarkAttendance", "JSON parsing error: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(MarkAttendanceActivity.this, "Error parsing server response.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("MarkAttendance", "Volley error: " + error.toString());
                    Toast.makeText(MarkAttendanceActivity.this, "Network error. Check your connection.", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("student_id", String.valueOf(studentId)); // Convert to string when sending
                map.put("announcement_id", String.valueOf(selectedAnnouncementId)); // Convert to string
                return map;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
