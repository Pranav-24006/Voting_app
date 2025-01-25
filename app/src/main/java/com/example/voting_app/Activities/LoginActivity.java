package com.example.voting_app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voting_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText userEmail, userPassword;
    Button loginBtn;
    FirebaseAuth mAuth;
    TextView forgetPassword;

    public static final String PREFERENCES = "prefKey";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    public static final String Password = "passwordKey";
    public static final String RegistrationId = "registrationIdKey";
    public static final String Image = "imageKey";
    SharedPreferences sharedPreferences;

    StorageReference reference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        findViewById(R.id.dont_have_acc).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,SignupActivity.class)));
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        loginBtn = findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        forgetPassword = findViewById(R.id.forget_password);
        sharedPreferences = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        loginBtn.setOnClickListener(v->{
            String mail = userEmail.getText().toString().trim();
            String password = userPassword.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(task->{
                if(task.isSuccessful()){

                    verifyEmail();
                }
                else{
                    Toast.makeText(this, "Check the credentials and try again", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void verifyEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        if (user != null) {
            if (user.isEmailVerified()) {
                String name = sharedPreferences.getString(Name, null);
                String password = sharedPreferences.getString(Password, null);
                String email = sharedPreferences.getString(Email, null);
                String regId = sharedPreferences.getString(RegistrationId, null);
                String image = sharedPreferences.getString(Image, null);

                if (name == null || password == null || email == null || regId == null || image == null) {
                    Toast.makeText(this, "Incomplete data. Please sign up again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uid = user.getUid();

                // Initialize Firebase Storage reference
                if (reference == null) {
                    Toast.makeText(this, "Storage reference not initialized", Toast.LENGTH_SHORT).show();
                    return;
                }

                StorageReference imagePath = reference.child("Profile").child(uid + ".jpg");

                // Show a progress dialog
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading profile...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                imagePath.putFile(Uri.parse(image)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imagePath.getDownloadUrl().addOnSuccessListener(uri -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("email", email);
                            map.put("password", password);
                            map.put("registrationId", regId);
                            map.put("image", uri.toString());
                            map.put("uid", uid);

                            // Write data to Firestore
                            firebaseFirestore.collection("Users")
                                    .document(uid)
                                    .set(map)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Failed to save user data to Firestore: " + task1.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Image upload failed: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                mAuth.signOut();
                Toast.makeText(this, "Please complete the email verification", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid user", Toast.LENGTH_SHORT).show();
        }
    }


}