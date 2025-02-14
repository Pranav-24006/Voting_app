package com.example.voting_app.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voting_app.Adapter.CandidateAdapter;
import com.example.voting_app.R;
import com.example.voting_app.model.Candidate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllCandidateActivity extends AppCompatActivity {
    private RecyclerView candidateRV;
    private Button startBtn;
    private List<Candidate> list;
    private CandidateAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_candidate);
        candidateRV = findViewById(R.id.candidates_rv);
        startBtn = findViewById(R.id.start);

        firebaseFirestore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        adapter = new CandidateAdapter(this,list);
        candidateRV.setAdapter(adapter);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            firebaseFirestore.collection("Candidates")
                    .get()
                    .addOnCompleteListener(t->{
                       if(t.isSuccessful()){
                            for(DocumentSnapshot doc: t.getResult()){
                                list.add(new Candidate(
                                        doc.getString("image"),
                                        doc.getString("name"),
                                        doc.getString("post"),
                                        doc.getString("regId"),
                                        doc.getString("branch"),
                                        doc.getId() //Storing id so that it can be accessed later
                                ));
                            }
                            adapter.notifyDataSetChanged();
                       }
                       else{
                           Toast.makeText(this,"Couldn't fetch data",Toast.LENGTH_SHORT).show();
                       }
                    });
        }
    }
}