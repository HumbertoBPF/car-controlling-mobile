package com.example.carcontrollingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carcontrollingapp.R;
import com.example.carcontrollingapp.models.Ad;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder>{

    private Context context;
    private List<Ad> ads;

    public AdAdapter(Context context, List<Ad> ads) {
        this.context = context;
        this.ads = ads;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ads_adapter, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        holder.bind(ads.get(position));
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }


    class AdViewHolder extends RecyclerView.ViewHolder{
        private TextView adTitleTextView;
        private ImageView adImageView;
        private TextView adDescriptionTextView;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            this.adTitleTextView = itemView.findViewById(R.id.ad_title_text_view);
            this.adImageView = itemView.findViewById(R.id.ad_image_view);
            this.adDescriptionTextView = itemView.findViewById(R.id.ad_description_text_view);
        }

        public void bind(Ad ad){
            String imageFileName = ad.getPicture().replace("/media/ads/","")
                    .replace(".png","").replace(".jpg","");
            int idImageResource = context.getResources()
                    .getIdentifier(imageFileName,"drawable", context.getPackageName());
            this.adTitleTextView.setText(ad.getTitle());
            this.adImageView.setImageResource(idImageResource);
            this.adDescriptionTextView.setText(ad.getDescription());
        }
    }

}
