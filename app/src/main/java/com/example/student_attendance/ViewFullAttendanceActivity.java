package com.example.student_attendance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewFullAttendanceActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> attendanceList = new ArrayList<>();
    int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_attendance);

        listView = findViewById(R.id.listViewAttendance);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendanceList);
        listView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("student_session", MODE_PRIVATE);
        studentId = prefs.getInt("student_id", -1);

        if (studentId == -1) {
            Toast.makeText(this, "Invalid student session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchAttendance();
    }

    private void fetchAttendance() {
        String url = "http://192.168.35.247/phpProject/get_attendance.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getString("status").equals("success")) {
                    JSONArray records = json.getJSONArray("records");

                    attendanceList.clear();
                    for (int i = 0; i < records.length(); i++) {
                        JSONObject record = records.getJSONObject(i);
                        String subject = record.getString("subject_name");
                        String title = record.getString("title");
                        String time = record.getString("timestamp");

                        attendanceList.add(subject + "\n" + title + "\n" + time);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("student_id", String.valueOf(studentId));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
