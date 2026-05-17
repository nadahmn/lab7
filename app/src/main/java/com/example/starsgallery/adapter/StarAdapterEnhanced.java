package com.example.starsgallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.starsgallery.R;
import com.example.starsgallery.beans.Star;
import com.example.starsgallery.service.StarService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StarAdapterEnhanced extends RecyclerView.Adapter<StarAdapterEnhanced.StarViewHolder> {

    private List<Star> stars;
    private List<Star> starsFilter;
    private final Context context;
    private final StarFilter starFilter;
    private OnStarClickListener clickListener;

    public interface OnStarClickListener {
        void onStarClick(Star star, int position);
    }

    public StarAdapterEnhanced(Context context, List<Star> stars) {
        this.context = context;
        this.stars = stars;
        this.starsFilter = new ArrayList<Star>(stars);
        this.starFilter = new StarFilter();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.star_item, parent, false);
        return new StarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StarViewHolder holder, int position) {
        Star star = starsFilter.get(position);
        
        // Set star information
        holder.nameText.setText(star.getName().toUpperCase());
        holder.idText.setText(String.format(Locale.US, "#%03d", star.getId()));
        holder.professionText.setText(star.getProfession());
        holder.nationalityText.setText("🌍 " + star.getNationality());
        holder.awardsText.setText("🏆 " + star.getAwardsCount());
        holder.ratingBar.setRating(star.getRating());
        holder.ratingText.setText(String.format(Locale.US, "%.1f", star.getRating()));
        
        // Load image with Glide
        Glide.with(context)
                .asBitmap()
                .load(star.getImg())
                .apply(new RequestOptions().override(120, 120))
                .into(holder.starImage);

        // Set favorite icon
        holder.favoriteIcon.setImageResource(star.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_empty);
        holder.favoriteBadge.setVisibility(star.isFavorite() ? View.VISIBLE : View.GONE);
        
        // Color code rating text
        if (star.getRating() >= 4.5f) {
            holder.ratingText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark, null));
        } else if (star.getRating() >= 3.5f) {
            holder.ratingText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark, null));
        } else {
            holder.ratingText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark, null));
        }
        
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
        final de.hdodenhof.circleimageview.CircleImageView starImage;
        final TextView nameText;
        final TextView idText;
        final TextView professionText;
        final TextView nationalityText;
        final TextView awardsText;
        final TextView ratingText;
        final RatingBar ratingBar;
        final ImageView favoriteIcon;
        final View favoriteBadge;

        StarViewHolder(View itemView) {
            super(itemView);
            starImage = itemView.findViewById(R.id.img_star);
            nameText = itemView.findViewById(R.id.tv_name);
            idText = itemView.findViewById(R.id.tv_id);
            professionText = itemView.findViewById(R.id.tv_profession);
            nationalityText = itemView.findViewById(R.id.tv_nationality);
            awardsText = itemView.findViewById(R.id.tv_awards);
            ratingText = itemView.findViewById(R.id.tv_rating);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            favoriteBadge = itemView.findViewById(R.id.favorite_badge);
            
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
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
                    boolean matches = star.getName().toLowerCase().contains(filterPattern) ||
                            star.getProfession().toLowerCase().contains(filterPattern) ||
                            star.getNationality().toLowerCase().contains(filterPattern) ||
                            String.valueOf(star.getId()).contains(filterPattern);
                    if (matches) {
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
