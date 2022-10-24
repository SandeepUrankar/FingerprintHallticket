package com.sandeep.firebaseexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

import java.util.Arrays;

public class VerifyStudentsActivity extends AppCompatActivity implements MFS100Event {
    TextInputLayout regnoTIL;
    ImageView fingerprintImageView;
    String regnoToVerify, verifyTemplateString;
    Button verifyButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    MFS100 mfs100;
    private Boolean isCaptureRunning = false;
    byte[] fingerprintTemplate, verifyTemplate;
    int timeout = 10000;
    String fingerprintTemplateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_students);
        getSupportActionBar().setTitle("Verify the student");
        mfs100 = new MFS100(this);
        try {
            mfs100 = new MFS100(this);
            mfs100.Init();
            mfs100.SetApplicationContext(VerifyStudentsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        verifyButton = findViewById(R.id.verify_button);
        regnoTIL = findViewById(R.id.regno_verify_textinputlayout);
        fingerprintImageView = findViewById(R.id.fingerprint_verify_imageview);
        firebaseDatabase = FirebaseDatabase.getInstance();
        fingerprintImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCapture();
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });
    }

    private void verify() {

            regnoToVerify = regnoTIL.getEditText().getText().toString();
            if (!regnoToVerify.isEmpty()) {
                fetchFingerprintTemplateFromDB();
            }else{
                Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            }

        //verifying
//        int ret = mfs100.MatchISO(fingerprintTemplate, verifyTemplate);
//        if (ret >= 0) {
//            if (ret >= 96) {
//                Toast.makeText(getApplicationContext(), "Finger matched with score: " + ret, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Finger not matched, score: " + ret + " is too low", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
//        }
        //end

    }

    private void fetchFingerprintTemplateFromDB() {
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("students").child(regnoToVerify).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    verifyTemplateString = task.getResult().child("fingerprinttemplate").getValue().toString();
                    assignBytesFromString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Log.d("firebase", verifyTemplateString);
                    verifyFingerprint();
                }
            }
        });
    }

    private void verifyFingerprint() {
        //Toast.makeText(getApplicationContext(), "finger: "+fingerprintTemplate.toString()+" verify: "+verifyTemplate.toString(), Toast.LENGTH_SHORT).show();
        int ret = mfs100.MatchISO(verifyTemplate, fingerprintTemplate);
        if (ret >= 0) {
            if (ret >= 96) {
                Toast.makeText(getApplicationContext(), "Finger matched with score: " + ret, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("regno", regnoToVerify);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Finger not matched, score: " + ret + " is too low", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
        }
    }

    private void startCapture() {
        Toast.makeText(getApplicationContext(), "Place your finger on scanner", Toast.LENGTH_SHORT).show();
        if (isCaptureRunning == false) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    isCaptureRunning = true;
                    try {
                        FingerData fingerData = new FingerData();
                        int ret = mfs100.AutoCapture(fingerData, timeout, true); //true for finger is correctly placed
                        Log.e("StartSyncCapture.RET", "" + ret);
                        if (ret != 0) {
                            Log.e("this", mfs100.GetErrorMsg(ret));
                        } else {
                            //lastCapFingerData = fingerData;

                            final Bitmap bitmap = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0,
                                    fingerData.FingerImage().length);

                            //fingerprintImageView.setImageBitmap(bitmap);
                            VerifyStudentsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fingerprintImageView.setImageBitmap(bitmap);
                                    fingerprintTemplate = new byte[fingerData.ISOTemplate().length];
                                    System.arraycopy(fingerData.ISOTemplate(), 0, fingerprintTemplate, 0,
                                            fingerData.ISOTemplate().length);
                                    fingerprintTemplateString = Arrays.toString(fingerprintTemplate);
                                    //Toast.makeText(getApplicationContext(), fingerprintTemplateString, Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    } catch (Exception ex) {
                        Log.e("this", ex.toString());
                        //logs.setText(ex.toString());
                    } finally {
                        isCaptureRunning = false;
                    }
                }
            }).start();
        }
    }

    private void assignBytesFromString() {
        String[] byteValues = verifyTemplateString.substring(1, verifyTemplateString.length() - 1).split(",");
        verifyTemplate = new byte[byteValues.length];
        int len = verifyTemplate.length;
        for (int i = 0; i < len; i++) {
            verifyTemplate[i] = Byte.parseByte(byteValues[i].trim());
        }
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret;
        try {
            if (vid == 1204 || vid == 11279) {
                if (pid == 34323) {
                    ret = mfs100.LoadFirmware();
                    if (ret != 0) {
                        Log.v("this", mfs100.GetErrorMsg(ret));
                    } else {
                        Log.v("this", "Load firmware success");
                    }
                } else if (pid == 4101) {
                    String key = "Without Key";
                    ret = mfs100.Init();
                    if (ret == 0) {
                        Log.v("this", key);
                    } else {
                        Log.v("this", mfs100.GetErrorMsg(ret));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnDeviceDetached() {
        Toast.makeText(getApplicationContext(), "Scanner detached", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnHostCheckFailed(String s) {
        Toast.makeText(getApplicationContext(), "Scanner check Failed", Toast.LENGTH_SHORT).show();
    }
}