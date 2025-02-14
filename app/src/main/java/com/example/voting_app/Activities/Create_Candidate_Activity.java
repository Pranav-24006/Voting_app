package com.example.voting_app.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voting_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Create_Candidate_Activity extends AppCompatActivity {
    private CircleImageView candidateImg;
    private EditText candidateName,candidateRegID, candidateBranch;
    private Spinner candidateSpinner;
    private final String [] candPost = {"President","Vice-President","General Secretary"};
    private Button submitBtn;
    private Uri mainUri = null;
    StorageReference reference ;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_candidate);
        reference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        candidateImg = findViewById(R.id.candidate_image);
        candidateName = findViewById(R.id.candidate_name);
        candidateRegID = findViewById(R.id.candidate_regId);
        candidateBranch = findViewById(R.id.candidate_branch);
        candidateSpinner = findViewById(R.id.candidate_spinner);
        submitBtn = findViewById(R.id.candidate_submit_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,candPost);
        candidateSpinner.setAdapter(adapter);

        submitBtn.setOnClickListener(v->{
            String name = candidateName.getText().toString().trim();
            String regId = candidateRegID.getText().toString().trim();
            String branch = candidateBranch.getText().toString().trim();
            String post = candidateSpinner.getSelectedItem().toString();
            if(!(TextUtils.isEmpty(name)||TextUtils.isEmpty(regId)||TextUtils.isEmpty(branch)||TextUtils.isEmpty(post))&&mainUri!=null){
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                StorageReference imagePath = reference.child("candidate_img").child(uid + ".jpg");
                imagePath.putFile(mainUri).addOnCompleteListener(task->{
                    if(task.isComplete()){
                        if(task.isSuccessful()){
                            imagePath.getDownloadUrl().addOnSuccessListener(uri->{
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", name);
                                map.put("regId", regId);
                                map.put("branch",branch);
                                map.put("post", post);
                                map.put("image", uri.toString());
                                map.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Candidate").add(map).addOnCompleteListener(t->{
                                    if(t.isSuccessful()){
                                        startActivity(new Intent(Create_Candidate_Activity.this, HomeActivity.class));
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(Create_Candidate_Activity.this, "Data could not be stored", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        }
                    }
                    else {
                        Toast.makeText(Create_Candidate_Activity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(Create_Candidate_Activity.this, "Enter all details", Toast.LENGTH_SHORT).show();
            }
        });

        candidateImg.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                checkAndRequestForPermission();
            }else{
                selectImageFromGallery();
            }
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
                mainUri = croppedImageUri;
                candidateImg.setImageURI(croppedImageUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            // Handle cropping error
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(Create_Candidate_Activity.this, "Error cropping image: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}