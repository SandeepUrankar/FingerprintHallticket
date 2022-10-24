package com.sandeep.firebaseexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ResultActivity extends AppCompatActivity {
    String name, regno, sem, imageurl, fname, department, imagelink;
    ImageView studentImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView snameTV, fnameTV, regnoTV, semTV, departmentTV;
    Query query;
    ResultAdapter resultAdapter;
    RecyclerView resultRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setTitle("Result");
        regno = getIntent().getStringExtra("regno");
        firebaseDatabase = FirebaseDatabase.getInstance();
        snameTV = findViewById(R.id.sname);
        fnameTV = findViewById(R.id.fname);
        regnoTV = findViewById(R.id.regno);
        semTV = findViewById(R.id.sem);
        studentImage = findViewById(R.id.student_result_imageview);
        departmentTV = findViewById(R.id.department);
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("students").child(regno).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    name = task.getResult().child("name").getValue().toString();
                    regno = task.getResult().child("regno").getValue().toString();
                    sem = task.getResult().child("sem").getValue().toString();
                    imageurl = task.getResult().child("imageurl").getValue().toString();
                    fname = task.getResult().child("fname").getValue().toString();
                    department = task.getResult().child("dept").getValue().toString();
                    imagelink = task.getResult().child("imageurl").getValue().toString();
                    snameTV.setText(name);
                    fnameTV.setText(fname);
                    regnoTV.setText(regno);
                    semTV.setText(sem + " sem");
                    departmentTV.setText(department);
                    Glide.with(getApplicationContext()).load(imagelink).into(studentImage);
                }
            }
        });
        getSubjectDetails();

    }

    private void getSubjectDetails() {
        //TODO
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("students")
                .child(regno)
                .child("subjects")
                .limitToLast(50);
        FirebaseRecyclerOptions<Subjects> options =
                new FirebaseRecyclerOptions.Builder<Subjects>()
                        .setQuery(query, Subjects.class)
                        .build();
        resultRecyclerView = findViewById(R.id.subjects_recyclerview);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new ResultAdapter(options);
        resultRecyclerView.setAdapter(resultAdapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        resultAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        resultAdapter.stopListening();
    }
}