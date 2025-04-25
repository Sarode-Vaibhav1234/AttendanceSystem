package com.example.student_attendance;

import android.content.Intent;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class StudentSignupActivity extends AppCompatActivity {

    private EditText etName, etRollNumber, etEmail, etPassword;
    private String name, rollNumber, email, password;
    private String deviceID;
    private String fingerprintHash = "";  // Required for schema
    private Executor executor;

    Button btn;
    // Declaring UI elements
    private EditText etConfirmPassword, etAdmission, etContact;
    private String confirmPassword, admission, contact;
    private Button btnSignUp;
    private TextView signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup);

        // Initializing the EditText fields
        etName = findViewById(R.id.et_name);
        ImageView imageView = findViewById(R.id.imageView);
        etRollNumber = findViewById(R.id.et_prn);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirm_password);
        etAdmission = findViewById(R.id.et_admission);
        etContact = findViewById(R.id.et_contact);

        // Initializing the Button and TextView
        btnSignUp = findViewById(R.id.btn_signup);
        signUpTextView = findViewById(R.id.signUpTextView);

        // Setting the click listener for the Sign-Up button
        executor = ContextCompat.getMainExecutor(this);

        findViewById(R.id.btn_signup).setOnClickListener(view -> checkBiometricSupportAndStart());
        Drawable drawable = getDrawable(R.drawable.signup);
        if (drawable instanceof AnimatedImageDrawable) {
            ((AnimatedImageDrawable) drawable).start();
        }
        imageView.setImageDrawable(drawable);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Redirect to login screen when the "Already have an account? Login" text is clicked
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StudentLoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity (optional)
        });
    }

    private void checkBiometricSupportAndStart() {
        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                startBiometricAuthentication();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No biometric hardware found on this device.", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric hardware is currently unavailable.", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No biometric credentials found. Please add a fingerprint or face unlock in settings.", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "Biometric authentication is not supported.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void startBiometricAuthentication() {
        BiometricPrompt biometricPrompt = new BiometricPrompt(
                this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        fingerprintHash = "verified";  // Dummy value for schema
                        checkIfDeviceRegistered();  // Check if already registered
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(StudentSignupActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(StudentSignupActivity.this, "Authentication failed. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate to complete registration")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void checkIfDeviceRegistered() {
        deviceID = android.os.Build.ID;

        String url = "http://192.168.70.200/phpProject/check_registration.php";  // Check registration endpoint

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Parse response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("error".equals(status)) {
                            Toast.makeText(StudentSignupActivity.this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            registerStudent();
                        }
                    } catch (Exception e) {
                        Toast.makeText(StudentSignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(StudentSignupActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", deviceID);
                params.put("fingerprint_hash", fingerprintHash);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void registerStudent() {
        name = etName.getText().toString().trim();
        rollNumber = etRollNumber.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (fingerprintHash.isEmpty()) {
            Toast.makeText(this, "Please complete biometric authentication first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.214.250/phpProject/student_signup.php";  // Register student endpoint

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(StudentSignupActivity.this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StudentLoginActivity.class);
                    startActivity(intent);
                },
                error -> Toast.makeText(StudentSignupActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("roll_number", rollNumber);
                params.put("email", email);
                params.put("password", password);
                params.put("device_id", deviceID);
                params.put("fingerprint_hash", fingerprintHash);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
