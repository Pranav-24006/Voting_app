package com.example.voting_app.Activities;

import static java.sql.Types.NULL;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.voting_app.R;
import com.example.voting_app.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {
    Uri mainUri = null;
    private CircleImageView userProfile;
    private EditText userName,userEmail, userPassword, userRegId;
    private FirebaseAuth mAuth;
    public static final String PREFERENCES = "prefKey";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Password = "passwordKey";
    public static final String RegistrationId = "registrationIdKey";
    public static final String Image = "imageKey";
    SharedPreferences sharedPreferences;
    String name, email, password, regId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        findViewById(R.id.have_acc).setOnClickListener(v -> {
            Intent intent  = new Intent(SignupActivity.this, SplashScreen.class);
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
            name = userName.getText().toString().trim();
            email = userEmail.getText().toString().trim();
            password = userPassword.getText().toString().trim();
            regId = userRegId.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(SignupActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignupActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() <= 8) {
                Toast.makeText(SignupActivity.this, "Password must be longer than 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(regId)) {
                Toast.makeText(SignupActivity.this, "Registration ID is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if(mainUri==null){
                Toast.makeText(SignupActivity.this, "Your profile image is required", Toast.LENGTH_SHORT).show();
                return;
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
                        verifyEmail(userName,mAuth.getCurrentUser());
                        Toast.makeText(SignupActivity.this,"User created successfully",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Failed, Try Again",Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(v-> {
                Toast.makeText(SignupActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
        });
    }

    private void verifyEmail(EditText userName,FirebaseUser user){
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(task->{
                if(task.isSuccessful()){
                    SharedPreferences.Editor pref = sharedPreferences.edit();
                    pref.putString(Name, name);
                    pref.putString(Password,password);
                    pref.putString(RegistrationId,regId);
                    pref.putString(Email,email);
                    pref.putString(Image,mainUri.toString());
                    pref.commit();
                    Toast.makeText(SignupActivity.this,"Verification Email Sent",Toast.LENGTH_SHORT).show();

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(SignupActivity.this, "Failed to send verification email. Please try again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
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
                mainUri = croppedImageUri;
                userProfile.setImageURI(croppedImageUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            // Handle cropping error
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(SignupActivity.this, "Error cropping image: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}