package com.example.carcontrollingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.Score;

import java.util.List;

public class RankingAdapter extends ScoreAdapter<RankingAdapter.RankingViewHolder> {

    public RankingAdapter(List<Score> scores) {
        super(scores);
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_adapter, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        holder.bind(scores.get(position), position);
    }

    class RankingViewHolder extends RecyclerView.ViewHolder{

        private final TextView positionTextView;
        private final TextView usernameTextView;
        private final TextView scoreTextView;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.positionTextView = itemView.findViewById(R.id.position_text_view);
            this.usernameTextView = itemView.findViewById(R.id.username_text_view);
            this.scoreTextView = itemView.findViewById(R.id.score_text_view);
        }

        public void bind(Score score, int position){
            this.positionTextView.setText((position+1)+"");
            this.usernameTextView.setText(score.getUser());
            this.scoreTextView.setText(score.getScore().toString());
        }
    }

}
