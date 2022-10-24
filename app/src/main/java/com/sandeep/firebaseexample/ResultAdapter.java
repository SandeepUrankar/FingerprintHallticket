package com.sandeep.firebaseexample;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultAdapter extends FirebaseRecyclerAdapter<Subjects, ResultAdapter.resultviewholder> {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ResultAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull resultviewholder holder, int position, @NonNull Subjects model) {
        holder.semTV.setText(model.getSem());
        holder.snoTV.setText(model.getSno());
        holder.qpcodeTV.setText(model.getQpcode());
        holder.subnameTV.setText(model.getSubname());
        holder.datetimeTV.setText(model.getDatetime());
        holder.statusTV.setText(model.getStatus());
        Log.v("status",model.getStatus());
        if (model.getStatus().startsWith("Attended")){
            holder.markAttendance.setVisibility(View.GONE);
        }else {
            holder.markAttendance.setVisibility(View.VISIBLE);
        }
        holder.markAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("students")
                        .child(model.getRegno())
                        .child("subjects")
                        .child(model.getSno())
                        .child("status");
                databaseReference.setValue("Attended").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.markAttendance.setVisibility(View.GONE);
                            }
                        });
                Toast.makeText(v.getContext(), "Clicked "+model.getSno(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ResultAdapter.resultviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_detail_single, parent, false);
        return new ResultAdapter.resultviewholder(view);
    }

    class resultviewholder extends RecyclerView.ViewHolder {
        TextView semTV, snoTV, qpcodeTV, subnameTV, datetimeTV, statusTV;
        Button markAttendance;
        public resultviewholder(@NonNull View itemView) {
            super(itemView);
            semTV = itemView.findViewById(R.id.sem_tv_sub);
            snoTV = itemView.findViewById(R.id.sno_tv_sub);
            qpcodeTV = itemView.findViewById(R.id.qpcode_tv_sub);
            subnameTV = itemView.findViewById(R.id.subname_tv_sub);
            datetimeTV = itemView.findViewById(R.id.datetime_tv_sub);
            statusTV = itemView.findViewById(R.id.status_tv_sub);
            markAttendance = itemView.findViewById(R.id.attended_button);
        }
    }
}
