package com.example.voting_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.voting_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VotingActivity extends AppCompatActivity {
    private CircleImageView circleImg;
    private TextView name, position, branch, regId;
    private Button voteBtn;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voting);

        circleImg = findViewById(R.id.image);
        name = findViewById(R.id.name);
        branch = findViewById(R.id.branch);
        position = findViewById(R.id.position);
        regId = findViewById(R.id.reg_id);
        voteBtn = findViewById(R.id.vote_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Extract data from Intent with null checks
        String url = getIntent().getStringExtra("image");
        String nm = getIntent().getStringExtra("name");
        String pos = getIntent().getStringExtra("position");
        String br = getIntent().getStringExtra("branch");
        String rg = getIntent().getStringExtra("regId");
        String id = getIntent().getStringExtra("id");

        // Load image safely
        if (url != null) Glide.with(this).load(url).into(circleImg);

        // Set text safely
        name.setText(nm != null ? nm : "Unknown");
        branch.setText(br != null ? br : "N/A");
        position.setText(pos != null ? pos : "N/A");
        regId.setText(rg != null ? rg : "N/A");

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String deviceIp = getDeviceIP(); // Avoid multiple calls

        voteBtn.setOnClickListener(v -> {
            if (id == null || pos == null) {
                Toast.makeText(this, "Invalid candidate data", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("finish", "voted");
            userMap.put("deviceIP", deviceIp);
            userMap.put(pos, id);

            firebaseFirestore.collection("Users")
                    .document(uid)
                    .set(userMap, SetOptions.merge())  // Ensure document creation if missing
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show());

            Map<String, Object> candidateMap = new HashMap<>();
            candidateMap.put("deviceIp", deviceIp);
            candidateMap.put("candidatePost", pos);
            candidateMap.put("timestamp", FieldValue.serverTimestamp());

            firebaseFirestore.collection("Candidate/" + id + "/Vote")
                    .document(uid)
                    .set(candidateMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(VotingActivity.this, ResultActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(VotingActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private String getDeviceIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface inf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = inf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            return "Unavailable"; // Avoid returning null
        }
        return "Unavailable";
    }
}
