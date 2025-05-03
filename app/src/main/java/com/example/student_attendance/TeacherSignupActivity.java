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
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TeacherSignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    TextView toLogin;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_signup);

        etName = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        ImageView imageView = findViewById(R.id.imageView);
        btnSignup = findViewById(R.id.signup_button);
        toLogin = findViewById(R.id.signUpTextView);
        Drawable drawable = getDrawable(R.drawable.signup);
        if (drawable instanceof AnimatedImageDrawable) {
            ((AnimatedImageDrawable) drawable).start();
        }
        imageView.setImageDrawable(drawable);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                ImageView imageView = findViewById(R.id.imageView);
                // Validate inputs
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(TeacherSignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Make the signup request
                String url = "http://192.168.132.247/phpProject/teacher_signup.php"; // Replace with actual URL

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Handle the response from the PHP API
                                if (response.contains("success")) {
                                    Toast.makeText(TeacherSignupActivity.this, "Teacher registered successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TeacherSignupActivity.this, "Signup failed: " + response, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TeacherSignupActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", name);
                        params.put("email", email);
                        params.put("password", password);
                        return params;
                    }
                };

                // Add the request to the Volley request queue
                Volley.newRequestQueue(TeacherSignupActivity.this).add(stringRequest);
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TeacherLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
