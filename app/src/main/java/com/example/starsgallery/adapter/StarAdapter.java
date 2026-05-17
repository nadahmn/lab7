package com.example.starsgallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
import java.util.Locale;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarViewHolder> implements Filterable {

    private static final String TAG = "StarAdapter";
    private List<Star> stars;
    private List<Star> starsFilter;
    private Context context;
    private StarFilter starFilter;
    private OnStarClickListener clickListener;

    public interface OnStarClickListener {
        void onStarClick(Star star, int position);
        void onStarLongClick(Star star, int position);
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        this.clickListener = listener;
    }

    public StarAdapter(Context context, List<Star> stars) {
        this.context = context;
        this.stars = stars;
        this.starsFilter = new ArrayList<Star>(stars);
        this.starFilter = new StarFilter();
    }

    @Override
    public StarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.star_item, parent, false);
        final StarViewHolder holder = new StarViewHolder(v);

        v.setOnClickListener(view -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onStarClick(starsFilter.get(position), position);
            }
        });

        v.setOnLongClickListener(view -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onStarLongClick(starsFilter.get(position), position);
            }
            return true;
        });

        holder.favoriteIcon.setOnClickListener(v1 -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Star star = starsFilter.get(position);
                star.toggleFavorite();
                notifyItemChanged(position);
                animateFavoriteIcon(holder.favoriteIcon);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(StarViewHolder holder, int position) {
        Star star = starsFilter.get(position);
        
        holder.nameText.setText(star.getName().toUpperCase());
        holder.professionText.setText(star.getProfession());
        holder.ratingBar.setRating(star.getRating());
        holder.idText.setText(String.format("#%03d", star.getId()));
        holder.nationalityText.setText(star.getNationality());
        holder.awardsText.setText(String.format("🏆 %d", star.getAwardsCount()));
        
        holder.favoriteIcon.setImageResource(star.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_empty);
        
        Glide.with(context)
                .asBitmap()
                .load(star.getImg())
                .apply(new RequestOptions()
                        .override(120, 120)
                        .placeholder(R.drawable.star_placeholder)
                        .error(R.drawable.star_error))
                .into(holder.starImage);

        String ratingText = String.format(Locale.getDefault(), "%.1f", star.getRating());
        holder.ratingText.setText(ratingText);
        
        if (star.getRating() >= 4.5f) {
            holder.ratingText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (star.getRating() >= 3.5f) {
            holder.ratingText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            holder.ratingText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        animateItemEntry(holder.itemView, position);
    }

    private void animateItemEntry(View itemView, int position) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(300);
        anim.setStartOffset(position * 50);
        itemView.startAnimation(anim);
    }

    private void animateFavoriteIcon(ImageView favoriteIcon) {
        ScaleAnimation anim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, 
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        favoriteIcon.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return starsFilter.size();
    }

    @Override
    public Filter getFilter() {
        return starFilter;
    }

    public Star getStarAt(int position) {
        return starsFilter.get(position);
    }

    public void updateStar(Star star) {
        for (int i = 0; i < starsFilter.size(); i++) {
            if (starsFilter.get(i).getId() == star.getId()) {
                starsFilter.set(i, star);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeStar(int position) {
        if (position >= 0 && position < starsFilter.size()) {
            Star star = starsFilter.remove(position);
            stars.remove(star);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, starsFilter.size());
        }
    }

    public void addStar(Star star) {
        stars.add(star);
        starsFilter.add(star);
        notifyItemInserted(starsFilter.size() - 1);
    }

    public void refreshData(List<Star> newStars) {
        stars.clear();
        stars.addAll(newStars);
        starsFilter.clear();
        starsFilter.addAll(newStars);
        notifyDataSetChanged();
    }

    static class StarViewHolder extends RecyclerView.ViewHolder {
        ImageView starImage;
        TextView nameText;
        TextView professionText;
        TextView idText;
        TextView nationalityText;
        TextView awardsText;
        TextView ratingText;
        RatingBar ratingBar;
        ImageView favoriteIcon;

        StarViewHolder(View itemView) {
            super(itemView);
            starImage = itemView.findViewById(R.id.img_star);
            nameText = itemView.findViewById(R.id.tv_name);
            professionText = itemView.findViewById(R.id.tv_profession);
            idText = itemView.findViewById(R.id.tv_id);
            nationalityText = itemView.findViewById(R.id.tv_nationality);
            awardsText = itemView.findViewById(R.id.tv_awards);
            ratingText = itemView.findViewById(R.id.tv_rating);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }
    }

    private class StarFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Star> filteredList = new ArrayList<Star>();
            
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(stars);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                
                for (Star star : stars) {
                    boolean matches = star.getName().toLowerCase(Locale.getDefault()).contains(filterPattern) ||
                            star.getProfession().toLowerCase(Locale.getDefault()).contains(filterPattern) ||
                            star.getNationality().toLowerCase(Locale.getDefault()).contains(filterPattern) ||
                            String.valueOf(star.getRating()).contains(filterPattern) ||
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

    public void showRatingDialog(Star star, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.star_edit_item, null);
        
        ImageView dialogImage = dialogView.findViewById(R.id.dialog_img);
        RatingBar dialogRating = dialogView.findViewById(R.id.dialog_rating);
        TextView dialogName = dialogView.findViewById(R.id.dialog_name);
        TextView dialogInfo = dialogView.findViewById(R.id.dialog_info);

        dialogName.setText(star.getName());
        dialogInfo.setText(String.format("%s • %s • Age: %d", 
                star.getProfession(), star.getNationality(), star.getAge()));
        dialogRating.setRating(star.getRating());

        Glide.with(context)
                .load(star.getImg())
                .apply(new RequestOptions().override(150, 150))
                .into(dialogImage);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("⭐ Rate This Star")
                .setMessage(String.format("How would you rate %s?", star.getName()))
                .setView(dialogView)
                .setPositiveButton("UPDATE", (dialogInterface, i) -> {
                    float newRating = dialogRating.getRating();
                    star.setRating(newRating);
                    StarService.getInstance().update(star);
                    notifyItemChanged(position);
                    
                    Toast.makeText(context, 
                            String.format("Rating updated to %.1f", newRating), 
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.show();
    }
}
