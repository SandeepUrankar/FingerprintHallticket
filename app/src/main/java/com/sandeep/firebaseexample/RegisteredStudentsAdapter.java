package com.sandeep.firebaseexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RegisteredStudentsAdapter extends FirebaseRecyclerAdapter<Students, RegisteredStudentsAdapter.myviewholder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RegisteredStudentsAdapter(@NonNull FirebaseRecyclerOptions<Students> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Students model) {
        holder.name.setText(model.getName());
        Glide.with(holder.imageView.getContext()).load(model.getImageurl()).into(holder.imageView);
        holder.regno.setText(model.getRegno());
        holder.sem.setText(model.getSem()+" Sem");
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_detail_single, parent, false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, regno, sem;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_imageview);
            name = itemView.findViewById(R.id.name_textview);
            regno = itemView.findViewById(R.id.regno_textview);
            sem = itemView.findViewById(R.id.sem_textview);
        }
    }
}
