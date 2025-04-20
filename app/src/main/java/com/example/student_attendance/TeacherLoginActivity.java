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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView forgotPasswordTextView, signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        // Initializing the EditText fields and Buttons
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        signUpTextView = findViewById(R.id.signUpTxtView);
        ImageView imageView = findViewById(R.id.imageview);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(getApplicationContext(), TeacherSignupActivity.class);
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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data
                String email = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(TeacherLoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make the login request
                String url = "http://192.168.35.247/phpProject/teacher_login.php"; // Replace with actual URL

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    String status = jsonResponse.getString("status");

                                    if (status.equals("success")) {
                                        // Teacher login successful
                                        String teacherId = jsonResponse.getString("teacher_id");
                                        String name = jsonResponse.getString("name");

                                        SharedPreferences prefs = getSharedPreferences("teacher_session", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("teacher_id", teacherId);
                                        editor.putString("teacher_name", name);
                                        editor.apply();

                                        // Save teacher data (e.g., SharedPreferences or Intent)
                                        Intent intent = new Intent(TeacherLoginActivity.this, TeacherDashboardActivity.class);
                                        intent.putExtra("teacher_id", teacherId);
                                        intent.putExtra("teacher_name", name);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Login failed
                                        Toast.makeText(TeacherLoginActivity.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(TeacherLoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TeacherLoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("password", password);
                        return params;
                    }
                };

                // Add the request to the Volley request queue
                Volley.newRequestQueue(TeacherLoginActivity.this).add(stringRequest);
            }
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
