package com.example.voting_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.voting_app.Activities.VotingActivity;
import com.example.voting_app.R;
import com.example.voting_app.model.Candidate;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {
    private Context context;
    private List<Candidate> list;

    public CandidateAdapter(Context context, List<Candidate> list) {
        this.context = context;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImg;
        private TextView name, position, branch, regId;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImg = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            branch = itemView.findViewById(R.id.branch);
            position = itemView.findViewById(R.id.position);
            regId = itemView.findViewById(R.id.reg_id);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    @NonNull
    @Override
    public CandidateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.candidate_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateAdapter.ViewHolder holder, int index) {
        holder.name.setText(list.get(index).getName());
        holder.position.setText(list.get(index).getPosition());
        holder.regId.setText(list.get(index).getRegId());
        holder.branch.setText(list.get(index).getBranch());

        Glide.with(context).load(list.get(index).getImage()).into(holder.circleImg);

        holder.cardView.setOnClickListener(v->{
            Intent intent = new Intent(context, VotingActivity.class);
            intent.putExtra("name",list.get(index).getName());
            intent.putExtra("position",list.get(index).getPosition());
            intent.putExtra("regId",list.get(index).getRegId());
            intent.putExtra("branch",list.get(index).getBranch());
            intent.putExtra("image",list.get(index).getImage());
            intent.putExtra("id",list.get(index).getId());

            context.startActivity(intent);
            Activity activity = (Activity)context;
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
