package com.example.voting_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.voting_app.R;
import com.example.voting_app.model.Candidate;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateResultAdapter extends RecyclerView.Adapter<CandidateResultAdapter.CandidateViewHolder> {

    private Context mContext;
    private List<Candidate> candidateList;
    private FirebaseFirestore db;

    public CandidateResultAdapter(Context context, List<Candidate> candidates) {
        this.mContext = context;
        this.candidateList = candidates;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.candidate_result_layout, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Candidate currentCandidate = candidateList.get(position);

        holder.candidateName.setText(currentCandidate.getName());
        holder.candidateBranch.setText(currentCandidate.getBranch());
        holder.candidateRole.setText(currentCandidate.getPosition());
        holder.candidateRegId.setText(currentCandidate.getRegId());

        Glide.with(mContext).load(currentCandidate.getImage()).into(holder.candidateImage);

        db.collection("Candidate/" + currentCandidate.getId() + "/Vote")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null || snapshots == null || snapshots.isEmpty()) {
                            return;
                        }

                        int voteCount = snapshots.size();
                        currentCandidate.setCount(voteCount);
                        candidateList.set(position, currentCandidate);

                        holder.voteResult.setText("Votes: " + voteCount);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView candidateImage;
        private TextView candidateName, candidateRole, candidateBranch, candidateRegId, voteResult;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            candidateImage = itemView.findViewById(R.id.image);
            candidateName = itemView.findViewById(R.id.name);
            candidateRole = itemView.findViewById(R.id.position);
            candidateBranch = itemView.findViewById(R.id.branch);
            candidateRegId = itemView.findViewById(R.id.reg_id);
            voteResult = itemView.findViewById(R.id.candidate_result);
        }
    }
}
