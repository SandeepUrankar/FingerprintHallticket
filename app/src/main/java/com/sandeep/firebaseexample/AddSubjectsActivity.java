package com.sandeep.firebaseexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddSubjectsActivity extends AppCompatActivity {
    TextInputLayout qpcodeTIL, subnameTIL, datetimeTIL, subnoTIL;
    Button addSubjectButton, submitSubjectButton;
    TextView addedsubjectsTV;
    String qpCode, subName, dateTime, regno, noOfSubjects, sem;
    int  numbersadded = 0;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subjects);
        getSupportActionBar().setTitle("Enter subjects");
        regno = getIntent().getStringExtra("regno");
        sem = getIntent().getStringExtra("sem");
        //regno = "202CS18047";
        qpcodeTIL = findViewById(R.id.qpcode_textinputlayout);
        subnameTIL = findViewById(R.id.subname_textinputlayout);
        datetimeTIL = findViewById(R.id.datetime_textinputlayout);
        subnoTIL = findViewById(R.id.subno_textinputlayout);
        addSubjectButton = findViewById(R.id.addsub_button);
        submitSubjectButton = findViewById(R.id.sumitsub_button);
        addedsubjectsTV = findViewById(R.id.addedsubjects_textview);
        addedsubjectsTV.setText("");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("students").child(regno).child("subjects");
        addSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubjects();
            }
        });
        submitSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSubjects();
            }
        });
    }

    private void addSubjects() {
        noOfSubjects = subnoTIL.getEditText().getText().toString().trim();
        qpCode = qpcodeTIL.getEditText().getText().toString().trim();
        subName = subnameTIL.getEditText().getText().toString().trim();
        dateTime = datetimeTIL.getEditText().getText().toString().trim();
        if (noOfSubjects.isEmpty() || qpCode.isEmpty() || subName.isEmpty() || dateTime.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields required", Toast.LENGTH_SHORT).show();
        } else {
            ++numbersadded;
            if (numbersadded > Integer.parseInt(noOfSubjects)) {
                Toast.makeText(getApplicationContext(), "Cannot add more than " + noOfSubjects, Toast.LENGTH_SHORT).show();
            } else {

                databaseReference.child(String.valueOf(numbersadded)).child("subname").setValue(subName);
                databaseReference.child(String.valueOf(numbersadded)).child("qpcode").setValue(qpCode);
                databaseReference.child(String.valueOf(numbersadded)).child("datetime").setValue(dateTime);
                databaseReference.child(String.valueOf(numbersadded)).child("sno").setValue(""+numbersadded);
                databaseReference.child(String.valueOf(numbersadded)).child("regno").setValue(regno);
                databaseReference.child(String.valueOf(numbersadded)).child("sem").setValue(sem);
                databaseReference.child(String.valueOf(numbersadded)).child("status").setValue("Not Attended").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        qpcodeTIL.getEditText().setText("");
                        subnameTIL.getEditText().setText("");
                        datetimeTIL.getEditText().setText("");
                        addedsubjectsTV.append("\n" + subName);
                    }
                });
                if (numbersadded == Integer.parseInt(noOfSubjects)){
                    addSubjectButton.setVisibility(View.GONE);
                }
            }
        }
    }

    private void submitSubjects() {
        if(numbersadded == 0){
            Toast.makeText(getApplicationContext(), "Add Something", Toast.LENGTH_SHORT).show();
        } else if(numbersadded == Integer.parseInt(noOfSubjects)){
            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Add all the subjects!!", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to check String for only Alphabets
    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

}