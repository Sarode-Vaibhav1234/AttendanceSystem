package com.example.student_attendance;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewFullAttendanceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressOverall;
    TextView txtOverall;
    ArrayList<AttendanceModel> list = new ArrayList<>();
    AttendanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_attendance);

        recyclerView = findViewById(R.id.recyclerViewAttendance);
        progressOverall = findViewById(R.id.progressOverall);
        txtOverall = findViewById(R.id.txtOverall);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(list);
        recyclerView.setAdapter(adapter);

        fetchAttendanceData();
    }

    private void fetchAttendanceData() {
        String url = "http:/192.168.146.37/phpProject/get_attendance.php";

        SharedPreferences prefs = getSharedPreferences("student_session", MODE_PRIVATE);
        int studentId = prefs.getInt("student_id", -1);

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                if (json.getString("status").equals("success")) {
                    list.clear();
                    JSONArray attendance = json.getJSONArray("attendance");
                    for (int i = 0; i < attendance.length(); i++) {
                        JSONObject obj = attendance.getJSONObject(i);
                        String subject = obj.getString("subject_name");
                        int percent = obj.getInt("percentage");
                        list.add(new AttendanceModel(subject, percent));
                    }
                    adapter.notifyDataSetChanged();

                    int total = json.getInt("total");
                    int present = json.getInt("present");
                    int overall = total == 0 ? 0 : (present * 100) / total;

                    progressOverall.setProgress(overall);
                    txtOverall.setText("Overall Attendance: " + overall + "%");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Fetch error", Toast.LENGTH_SHORT).show()) {
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
