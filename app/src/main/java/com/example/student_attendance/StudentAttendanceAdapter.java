package com.example.student_attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {

    private List<StudentAttendance> studentList;
    private Context context;

    public StudentAttendanceAdapter(Context context, List<StudentAttendance> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentAttendance student = studentList.get(position);
        holder.studentNameTextView.setText(student.getStudentName());

        // Calculate attendance percentage
        float attendancePercentage = student.getAttendancePercentage();

        // Set progress bar
        holder.attendanceProgressBar.setProgress((int) attendancePercentage);

        holder.attendancePercentageTextView.setText(String.format("%.2f%%", attendancePercentage));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        ProgressBar attendanceProgressBar;
        TextView attendancePercentageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.studentNameTextView);
            attendanceProgressBar = itemView.findViewById(R.id.attendanceProgressBar);
            attendancePercentageTextView = itemView.findViewById(R.id.attendancePercentageTextView);
        }
    }
}
