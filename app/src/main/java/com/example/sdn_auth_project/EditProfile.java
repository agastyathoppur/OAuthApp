package com.example.sdn_auth_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPhone, mDOB, mGender;
    Button mSaveBtn;
    FirebaseAuth fAuth;
    FirebaseUser user;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        final String fullName = data.getStringExtra("fName");
        String email = data.getStringExtra("email");
        String phone = data.getStringExtra("phone");
        String dob = data.getStringExtra("dob");
        String gender = data.getStringExtra("gender");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        mFullName = findViewById(R.id.FullName);
        mEmail = findViewById(R.id.Email);
        mPhone = findViewById(R.id.Phone);
        mDOB = findViewById(R.id.DOB);
        mGender = findViewById(R.id.Gender);

        mSaveBtn = findViewById(R.id.Save);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFullName.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty() || mPhone.getText().toString().isEmpty() || mDOB.getText().toString().isEmpty() || mGender.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email = mEmail.getText().toString();
                user.verifyBeforeUpdateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("email",email);
                        edited.put("fName",mFullName.getText().toString());
                        edited.put("phone",mPhone.getText().toString());
                        edited.put("dob", mDOB.getText().toString());
                        edited.put("gender", mGender.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(EditProfile.this, "Email is changed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        mEmail.setText(email);
        mFullName.setText(fullName);
        mPhone.setText(phone);
        mDOB.setText(dob);
        mGender.setText(gender);
    }
}