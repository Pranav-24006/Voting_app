package com.example.voting_app.Activities;

import static java.sql.Types.NULL;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.voting_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    private CircleImageView userProfile;
    private EditText userName,userEmail, userPassword, userRegId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.have_acc).setOnClickListener(v -> {
            Intent intent  = new Intent(SignupActivity.this,LoginActivity.class);
            startActivity(intent);
            this.finish();});

        userProfile = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        userPassword = findViewById(R.id.user_password);
        userEmail = findViewById(R.id.user_email);
        userRegId = findViewById(R.id.user_reg_id);
        Button signUpBtn = findViewById(R.id.signup_btn);

        mAuth = FirebaseAuth.getInstance();

        userProfile.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                checkAndRequestForPermission();
            }else{
                selectImageFromGallery();
            }
        });
        signUpBtn.setOnClickListener(v->{
            String name = userName.getText().toString().trim();
            String email = userEmail.getText().toString().trim();
            String password = userPassword.getText().toString().trim();
            String regId = userRegId.getText().toString().trim();
            if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(regId)){
                Toast.makeText(SignupActivity.this,"Please fill all fields",Toast.LENGTH_SHORT).show();
            }
            else {
                createUser(email, password);
            }
        });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(SignupActivity.this,"Use created successfully",Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(SignupActivity.this,LoginActivity.class);
                        startActivity(intent);
                        SignupActivity.this.finish();
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Failed, Try Again",Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(v-> {
                Toast.makeText(SignupActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
        });
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //noinspection deprecation
        startActivityForResult(intent, 1);
    }

    private void checkAndRequestForPermission() {
        final int PReqCode = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, PReqCode);
            } else {
                selectImageFromGallery();
            }
        } else {
            // For Android 6.0 to 12 (API 23-32)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            } else {
                selectImageFromGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Step 2: Get the selected image URI
            Uri sourceUri = data.getData();

            if (sourceUri != null) {
                // Step 3: Define a destination URI for the cropped image
                Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

                // Step 4: Start UCrop with the source and destination URIs
                UCrop.of(sourceUri, destinationUri)
                        .withAspectRatio(1, 1) // Set a square crop (profile picture)
                        .withMaxResultSize(512, 512) // Limit the output size
                        .start(this);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            // Step 5: Get the cropped image URI
            Uri croppedImageUri = UCrop.getOutput(data);
            if (croppedImageUri != null) {
                // Step 6: Set the cropped image to the profile ImageView
                userProfile.setImageURI(croppedImageUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            // Handle cropping error
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                cropError.printStackTrace();
            }
        }
    }
}