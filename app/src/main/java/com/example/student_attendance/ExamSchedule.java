package com.example.student_attendance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

public class ExamSchedule extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdfView);

        // Load PDF from assets folder
        pdfView.fromAsset("ESE TimeTable.pdf")
                .enableSwipe(true) // allows scrolling
                .swipeHorizontal(false) // vertical scrolling
                .enableDoubletap(true) // zoom with double tap
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .spacing(10) // space between pages
                .load();
    }
}

