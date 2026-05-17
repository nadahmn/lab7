package com.example.starsgallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.starsgallery.R;
import com.example.starsgallery.beans.Star;
import com.example.starsgallery.service.StarService;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StarGridAdapter extends RecyclerView.Adapter<StarGridAdapter.StarViewHolder> {

    private List<Star> stars;
    private List<Star> starsFilter;
    private Context context;
    private StarFilter starFilter;
    private OnStarClickListener clickListener;

    public interface OnStarClickListener {
        void onStarClick(Star star, int position);
    }

    public StarGridAdapter(Context context, List<Star> stars) {
        this.context = context;
        this.stars = stars;
        this.starsFilter = new ArrayList<Star>(stars);
        this.starFilter = new StarFilter();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public StarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.star_grid_item, parent, false);
        return new StarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StarViewHolder holder, int position) {
        Star star = starsFilter.get(position);
        
        holder.nameText.setText(star.getName().toUpperCase());
        holder.ratingBar.setRating(star.getRating());
        
        Glide.with(context)
                .asBitmap()
                .load(star.getImg())
                .apply(new RequestOptions().override(50, 50))
                .into(holder.starImage);

        holder.favoriteIcon.setImageResource(star.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_empty);
        
        // Animate entry
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
        anim.setDuration(300);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return starsFilter.size();
    }

    public Star getStarAt(int position) {
        return starsFilter.get(position);
    }

    public void refreshData(List<Star> newStars) {
        starsFilter.clear();
        starsFilter.addAll(newStars);
        notifyDataSetChanged();
    }

    class StarViewHolder extends RecyclerView.ViewHolder {
        CircleImageView starImage;
        TextView nameText;
        RatingBar ratingBar;
        ImageView favoriteIcon;

        StarViewHolder(View itemView) {
            super(itemView);
            starImage = itemView.findViewById(R.id.img_star);
            nameText = itemView.findViewById(R.id.tv_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onStarClick(starsFilter.get(position), position);
                }
            });
        }
    }

    public void setStars(List<Star> stars) {
        this.stars = stars;
        this.starsFilter = new ArrayList<Star>(stars);
    }

    public Filter getFilter() {
        return starFilter;
    }

    private class StarFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Star> filteredList = new ArrayList<Star>();
            
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(stars);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                
                for (Star star : stars) {
                    if (star.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(star);
                    }
                }
            }
            
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            starsFilter.clear();
            if (results.values != null) {
                starsFilter.addAll((List<Star>) results.values);
            }
            notifyDataSetChanged();
        }
    }
}
