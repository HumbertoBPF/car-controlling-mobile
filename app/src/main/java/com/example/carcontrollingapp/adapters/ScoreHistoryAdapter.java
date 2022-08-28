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

public class ScoreHistoryAdapter extends ScoreAdapter<ScoreHistoryAdapter.ScoreHistoryViewHolder> {

    public ScoreHistoryAdapter(List<Score> scores) {
        super(scores);
    }

    @NonNull
    @Override
    public ScoreHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score_history_adapter, parent, false);
        return new ScoreHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreHistoryViewHolder holder, int position) {
        holder.bind(scores.get(position));
    }

    class ScoreHistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateTextView;
        private final TextView scoreTextView;

        public ScoreHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dateTextView = itemView.findViewById(R.id.date_text_view);
            this.scoreTextView = itemView.findViewById(R.id.score_text_view);
        }

        public void bind(Score score){
            this.dateTextView.setText(score.getDate());
            this.scoreTextView.setText(score.getScore().toString());
        }

    }

}
