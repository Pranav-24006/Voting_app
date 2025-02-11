package com.example.voting_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voting_app.R;
import com.example.voting_app.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    private EditText emailEdit;
    private Button reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);
        emailEdit = findViewById(R.id.user_email);
        reset.setOnClickListener(v->{
            String email = emailEdit.getText().toString().trim();
            if(!TextUtils.isEmpty(email)){
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        Toast.makeText(this,"Password reset link sent",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, SplashScreen.class));
                        finish();
                    }else{
                        Toast.makeText(this,"Unable to find user",Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this,"Please enter your registered email id",Toast.LENGTH_SHORT).show();
            }
        });
    }
}