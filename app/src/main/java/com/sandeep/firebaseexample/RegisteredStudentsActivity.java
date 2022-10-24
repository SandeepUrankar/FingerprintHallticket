package com.sandeep.firebaseexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RegisteredStudentsActivity extends AppCompatActivity {

    RecyclerView registeredRecyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Query query;
    RegisteredStudentsAdapter registeredStudentsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_students);
        getSupportActionBar().setTitle("Registered Students");
        registeredRecyclerView = findViewById(R.id.recylerview_rsa);
        registeredRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("students")
                .limitToLast(50);

        FirebaseRecyclerOptions<Students> options =
                new FirebaseRecyclerOptions.Builder<Students>()
                        .setQuery(query, Students.class)
                        .build();

        registeredStudentsAdapter = new RegisteredStudentsAdapter(options);
        registeredRecyclerView.setAdapter(registeredStudentsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registeredStudentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        registeredStudentsAdapter.stopListening();
    }
}