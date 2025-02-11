package com.example.voting_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voting_app.Activities.HomeActivity;
import com.example.voting_app.Activities.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    public static final String PREFERENCES = "prefKey";
    SharedPreferences sharedPreferences;
    public static final String isLogin = "isLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        boolean b = sharedPreferences.getBoolean(isLogin,false);
        new Handler().postDelayed(()->{
            if(b){
                startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                finish();
            }
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        },3000);
    }
}