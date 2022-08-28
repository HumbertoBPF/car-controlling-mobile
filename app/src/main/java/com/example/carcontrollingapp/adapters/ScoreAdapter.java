package com.example.carcontrollingapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.example.carcontrollingapp.models.Score;

import java.util.List;

public abstract class ScoreAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
    protected final List<Score> scores;

    protected ScoreAdapter(List<Score> scores) {
        this.scores = scores;
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public Score getItem(int position){
        return scores.get(position);
    }
}
