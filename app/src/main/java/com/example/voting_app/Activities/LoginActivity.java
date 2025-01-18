package com.example.voting_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voting_app.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        findViewById(R.id.dont_have_acc).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,SignupActivity.class)));
    }
}