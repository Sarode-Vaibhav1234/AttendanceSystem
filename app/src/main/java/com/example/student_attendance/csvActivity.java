package com.example.student_attendance;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class csvActivity extends AppCompatActivity {

    private EditText subjectEditText;
    private Button generateCsvButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.csvactivity);

        subjectEditText = findViewById(R.id.etSubjectName);
        generateCsvButton = findViewById(R.id.btnDownload);

        generateCsvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subjectName = subjectEditText.getText().toString().trim();
                if (!subjectName.isEmpty()) {
                    fetchSubjectIdAndGenerateCsv(subjectName);
                } else {
                    Toast.makeText(csvActivity.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchSubjectIdAndGenerateCsv(final String subjectName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the URL with subject_name parameter
                    String urlString = "http://192.168.132.247/phpProject/generate_csv.php?subject_name=" +
                            URLEncoder.encode(subjectName, "UTF-8");

                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder csvContent = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            csvContent.append(line).append("\n");
                        }
                        reader.close();

                        saveCsvToDownloads(csvContent.toString(), subjectName);
                    } else {
                        runOnUiThread(() -> Toast.makeText(csvActivity.this,
                                "Server returned error: " + responseCode, Toast.LENGTH_SHORT).show());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(csvActivity.this,
                            "Error fetching CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void saveCsvToDownloads(String csvContent, String subjectName) {
        String fileName = subjectName + "_attendance.csv";

        try {
            Uri uri;
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                values.put(MediaStore.Downloads.IS_PENDING, 1);

                Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                uri = getContentResolver().insert(collection, values);

                if (uri == null) {
                    throw new IOException("Failed to create MediaStore entry.");
                }

                outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream == null) {
                    throw new IOException("Failed to open output stream.");
                }

                outputStream.write(csvContent.getBytes());
                outputStream.flush();
                outputStream.close();

                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                getContentResolver().update(uri, values, null, null);
            } else {
                File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File csvFile = new File(downloadsFolder, fileName);
                FileOutputStream fos = new FileOutputStream(csvFile);
                fos.write(csvContent.getBytes());
                fos.close();
            }

            runOnUiThread(() -> Toast.makeText(csvActivity.this, "CSV file saved to Downloads", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(csvActivity.this, "Error saving CSV file", Toast.LENGTH_SHORT).show());
        }
    }
}