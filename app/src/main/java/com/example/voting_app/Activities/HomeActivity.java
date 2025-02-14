package com.example.voting_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.voting_app.R;
import com.example.voting_app.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    public static final String PREFERENCES = "prefKey";
    SharedPreferences sharedPreferences;
    public static final String isLogin = "isLogin";
    private CircleImageView img;
    private TextView nameTxt, regIdTxt;
    private String uid;
    private FirebaseFirestore firebaseFirestore;
    private Button createBtn, voteBtn, startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean(isLogin,true);
        pref.commit();

//        findViewById(R.id.log_out).setOnClickListener(v->{
//            pref.putBoolean(isLogin,false);
//            pref.commit();
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(this, SplashScreen.class));
//            finish();
//        });
        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        img = findViewById(R.id.circle_image);
        nameTxt = findViewById(R.id.name);
        regIdTxt = findViewById(R.id.reg_id);
        createBtn = findViewById(R.id.admin_btn);
        voteBtn = findViewById(R.id.give_vote);
        startBtn = findViewById(R.id.candidate_create_voting);

        firebaseFirestore.collection("Users").document(uid).get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                String name = task.getResult().getString("name");
                String email = task.getResult().getString("email");
                String regId = task.getResult().getString("registrationId");
                String image = task.getResult().getString("image");
                assert email !=null;
                if(email.equals("pranavverma006@gmail.com")){
                    createBtn.setVisibility(View.VISIBLE);
                    startBtn.setVisibility(View.VISIBLE);
                    voteBtn.setVisibility(View.GONE);
                }
                else{
                    createBtn.setVisibility(View.GONE);
                    voteBtn.setVisibility(View.VISIBLE);
                    startBtn.setVisibility(View.GONE);
                }
                nameTxt.setText(name);
                regIdTxt.setText(regId);
                Glide.with(this).load(image).into(img);
            }
            else{
                Toast.makeText(this, "Internal Error",Toast.LENGTH_SHORT).show();
            }
        });

        createBtn.setOnClickListener(v->{
            this.startActivity(new Intent(this,Create_Candidate_Activity.class));
        });
        voteBtn.setOnClickListener(v->{
            this.startActivity(new Intent(this,AllCandidateActivity.class));
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SharedPreferences.Editor pref = sharedPreferences.edit();

        if (id == R.id.show_result) {
            startActivity(new Intent(HomeActivity.this, ResultActivity.class));
            return true;
        } else if (id == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            pref.putBoolean("isLogin", false);
            pref.apply();  // Use apply() for better performance
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}