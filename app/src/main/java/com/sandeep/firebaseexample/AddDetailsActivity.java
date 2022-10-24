package com.sandeep.firebaseexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

import java.util.Arrays;

public class AddDetailsActivity extends AppCompatActivity implements MFS100Event {

    ImageView profileImageView, fingerprintImageView;
    TextInputLayout nameTIL, regnoTIL, semTIL, fnameTIL, deptTIL;
    Button addDetailsButton;
    Uri imageUri;
    String imageURL;
    String fingerprintTemplateString;
    String name, regno, sem, fname, dept;
    FirebaseStorage storage;
    StorageReference storageRef;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    MFS100 mfs100;
    private Boolean isCaptureRunning = false;
    byte[] Enroll_Template;
    int timeout = 10000;
    //FingerData lastCapFingerData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        getSupportActionBar().setTitle("Enter your details");
        mfs100 = new MFS100(this);
        try {
            mfs100 = new MFS100(this);
            mfs100.Init();
            mfs100.SetApplicationContext(AddDetailsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        database = FirebaseDatabase.getInstance();


        profileImageView = findViewById(R.id.profile_imageview);
        fingerprintImageView = findViewById(R.id.fingerprint_imageview);
        nameTIL = findViewById(R.id.name_textinputlayout);
        regnoTIL = findViewById(R.id.regno_textinputlayout);
        semTIL = findViewById(R.id.sem_textinputlayout);
        fnameTIL = findViewById(R.id.fname_textinputlayout);
        deptTIL = findViewById(R.id.dept_textinputlayout);
        addDetailsButton = findViewById(R.id.add_button);
        requestPermission();


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        fingerprintImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFingerprint();
            }
        });

        addDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                name = nameTIL.getEditText().getText().toString().trim();
                regno = regnoTIL.getEditText().getText().toString().trim();
                sem = semTIL.getEditText().getText().toString().trim();
                dept = deptTIL.getEditText().getText().toString().trim();
                fname = fnameTIL.getEditText().getText().toString().trim();
                if (!name.isEmpty() && !regno.isEmpty() && !sem.isEmpty() && !dept.isEmpty() && !fname.isEmpty() && imageUri!=null && Enroll_Template !=null) {
                    if (isStringOnlyAlphabet(name.replaceAll("\\s",""))) {
                        if (isStringOnlyAlphabet(dept.replaceAll("\\s",""))) {
                            if (isStringOnlyAlphabet(fname.replaceAll("\\s",""))) {
                                addDetailsIntoDB();
                            } else {
                                Toast.makeText(getApplicationContext(), "Father name must be only alphabets", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "The department must be only alphabets", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "The name must be only alphabets", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void requestPermission() {
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Permission Required!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void chooseImage() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(getApplicationContext()).
                    load(imageUri).into(profileImageView);
        }
    }

    private void addDetailsIntoDB() {
        databaseReference = database.getReference("students");
        StorageReference imageAddRef = storageRef.child(regno + ".jpg");
        imageAddRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageAddRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        databaseReference.child(regno).child("name").setValue(name);
                        databaseReference.child(regno).child("regno").setValue(regno);
                        databaseReference.child(regno).child("sem").setValue(sem);
                        databaseReference.child(regno).child("fname").setValue(fname);
                        databaseReference.child(regno).child("dept").setValue(dept);
                        databaseReference.child(regno).child("fingerprinttemplate").setValue(fingerprintTemplateString);
                        databaseReference.child(regno).child("imageurl").setValue(imageURL).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "added to db", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), AddSubjectsActivity.class);
                                intent.putExtra("regno", regno);
                                intent.putExtra("sem", sem);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "failed to add to db", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                Toast.makeText(getApplicationContext(), "Image added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void captureFingerprint() {
        //TODO
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
                            AddDetailsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fingerprintImageView.setImageBitmap(bitmap);
                                    Enroll_Template = new byte[fingerData.ISOTemplate().length];
                                    System.arraycopy(fingerData.ISOTemplate(), 0, Enroll_Template, 0,
                                            fingerData.ISOTemplate().length);
                                    fingerprintTemplateString = Arrays.toString(Enroll_Template);
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

    //mantra
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
    //mantra

    // Function to check String for only Alphabets
    public static boolean isStringOnlyAlphabet(String str) {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }
}