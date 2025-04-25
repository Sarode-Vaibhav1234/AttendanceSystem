package com.example.student_attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText etRollNumber, etPassword;
    private Button btnLogin;
    private TextView forgotPasswordTextView, signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        ImageView imageView = findViewById(R.id.imageView);

        // Initializing the EditText fields and Buttons
        etRollNumber = findViewById(R.id.prnEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        signUpTextView = findViewById(R.id.signUpTextView);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getApplicationContext(), StudentSignupActivity.class);
                startActivity(i1);
            }
        });
        Drawable drawable = getDrawable(R.drawable.loginimg);
        if (drawable instanceof AnimatedImageDrawable) {
            ((AnimatedImageDrawable) drawable).start();
        }
        imageView.setImageDrawable(drawable);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnLogin.setOnClickListener(v -> {
            String roll = etRollNumber.getText().toString();
            String pass = etPassword.getText().toString();

            if (roll.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                return;
            }


            String url = "http://192.168.70.200/phpProject/student_login.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                int studentId = json.getInt("student_id");
                                String name = json.getString("name");

                                // Save to SharedPreferences
                                SharedPreferences prefs = getSharedPreferences("student_session", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("student_id", studentId);
                                editor.putString("name", name);
                                editor.apply();



                                Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();

                                // Navigate to Student Dashboard
                                Intent intent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
                                intent.putExtra("student_id", studentId);  // Pass student ID if needed
                                intent.putExtra("student_name", name);  // Pass student name if needed
                                startActivity(intent);
                                finish();  // Close the login activity
                            } else {
                                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("roll_number", roll);
                    params.put("password", pass);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
