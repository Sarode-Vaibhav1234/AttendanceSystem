package com.example.student_attendance;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ViewAttendanceActivity extends AppCompatActivity {

    LinearLayout studentListLayout;
    String url = "http://192.168.214.250/phpProject/get_students.php";
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_record);
        studentListLayout = findViewById(R.id.studentListLayout);
        studentListLayout = findViewById(R.id.studentListLayout);
        fetchStudents();
    }

    private void fetchStudents() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject student = response.getJSONObject(i);
                            String name = student.getString("name");

                            TextView textView = new TextView(this);
                            textView.setText(name);
                            textView.setTextSize(18f);
                            textView.setPadding(0, 10, 0, 10);

                            studentListLayout.addView(textView);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
        );

        queue.add(jsonArrayRequest);
    }
}