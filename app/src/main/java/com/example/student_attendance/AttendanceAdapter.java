package com.example.student_attendance;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private final List<AttendanceModel> list;

    public AttendanceAdapter(List<AttendanceModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSubject, txtPercentage;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtPercentage = itemView.findViewById(R.id.txtPercentage);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceModel model = list.get(position);
        holder.txtSubject.setText(model.getSubjectName());
        holder.txtPercentage.setText(model.getPercentage() + "%");
        holder.progressBar.setProgress(model.getPercentage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
