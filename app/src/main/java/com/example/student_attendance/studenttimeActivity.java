package com.example.student_attendance;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class studenttimeActivity extends AppCompatActivity {

    CalendarView calendarView;
    TextView textViewDate;
    ListView lectureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studenttime);

        calendarView = findViewById(R.id.calendarView);
        textViewDate = findViewById(R.id.textViewDate);
        lectureList = findViewById(R.id.lectureList);

        // Get today's date and fetch lectures
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        fetchLectures(today);

        // Listener for calendar date change
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            fetchLectures(selectedDate);
        });
    }

    // Get day name like Monday, Tuesday from yyyy-MM-dd
    private String getDayName(String dateStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = format.parse(dateStr);
            return new SimpleDateFormat("EEEE", Locale.getDefault()).format(date); // EEEE gives full day name
        } catch (Exception e) {
            e.printStackTrace();
            return "Monday"; // Fallback
        }
    }

    private void fetchLectures(String date) {
        String dayName = getDayName(date); // Convert to day name
        textViewDate.setText("Lectures for: " + dayName);
        new FetchLecturesTask().execute(dayName);
    }

    private class FetchLecturesTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            List<String> lectures = new ArrayList<>();
            try {
                String day = params[0]; // e.g., "Monday"
                URL url = new URL("http://192.168.132.247/phpProject/get_lectures.php?day=" + URLEncoder.encode(day, "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    result.append(line);

                JSONArray jsonArray = new JSONArray(result.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String lecture = obj.getString("time_from") + " to " + obj.getString("time_to")
                            + " - " + obj.getString("subject");
                    lectures.add(lecture);
                }

            } catch (Exception e) {
                e.printStackTrace(); // Debug log
                lectures.clear();
                lectures.add("Error fetching data");
            }
            return lectures;
        }

        @Override
        protected void onPostExecute(List<String> lectures) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(studenttimeActivity.this, android.R.layout.simple_list_item_1, lectures);
            lectureList.setAdapter(adapter);
        }
    }
}

