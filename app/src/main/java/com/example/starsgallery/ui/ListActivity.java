package com.example.starsgallery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.starsgallery.R;
import com.example.starsgallery.adapter.StarAdapter;
import com.example.starsgallery.beans.Star;
import com.example.starsgallery.service.StarService;
import java.util.List;
import java.util.Random;

public class ListActivity extends AppCompatActivity implements StarAdapter.OnStarClickListener {

    private static final String TAG = "ListActivity";
    private RecyclerView recyclerView;
    private StarAdapter starAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyStateText;
    private TextView statsText;
    private View emptyStateContainer;
    private StarService starService;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("⭐ Stars Gallery");
            getSupportActionBar().setSubtitle("Celebrity Database");
        }

        starService = StarService.getInstance();
        random = new Random();

        initializeViews();
        setupRecyclerView();
        setupSwipeRefresh();
        updateStats();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        emptyStateText = findViewById(R.id.empty_state_text);
        emptyStateContainer = findViewById(R.id.empty_state_container);
        statsText = findViewById(R.id.stats_text);
    }

    private void setupRecyclerView() {
        List<Star> stars = starService.findAll();
        starAdapter = new StarAdapter(this, stars);
        starAdapter.setOnStarClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(starAdapter);
        recyclerView.setHasFixedSize(true);
        
        updateEmptyState(stars.isEmpty());
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new android.os.Handler().postDelayed(() -> {
                starService.shuffleRatings();
                starAdapter.refreshData(starService.findAll());
                updateStats();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, "Ratings shuffled! 🎲", Toast.LENGTH_SHORT).show();
            }, 1500);
        });

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updateStats() {
        List<Star> allStars = starService.findAll();
        int totalStars = allStars.size();
        int favorites = starService.getFavorites().size();
        float avgRating = starService.getAverageRating();
        Star topRated = starService.getTopRated();
        
        String stats = String.format("Total: %d | Favorites: %d | Avg: %.1f⭐ | Top: %s", 
                totalStars, favorites, avgRating, 
                topRated != null ? topRated.getName() : "N/A");
        statsText.setText(stats);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        
        searchView.setQueryHint("Search stars by name, profession, nationality...");
        searchView.setIconifiedByDefault(true);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search submitted: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Search text changed: " + newText);
                if (starAdapter != null) {
                    starAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            if (starAdapter != null) {
                starAdapter.getFilter().filter("");
            }
            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.share) {
            shareApp();
            return true;
        } else if (id == R.id.action_add_star) {
            addRandomStar();
            return true;
        } else if (id == R.id.action_sort_by_rating) {
            sortByRating();
            return true;
        } else if (id == R.id.action_show_favorites) {
            showFavorites();
            return true;
        } else if (id == R.id.action_reset_data) {
            resetData();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String shareText = String.format("🌟 Check out Stars Gallery! 🌟\n\n" +
                "Browse through %d amazing celebrities!\n" +
                "Average rating: %.1f⭐\n" +
                "Top rated star: %s\n\n" +
                "Download now and explore the world of stars!",
                starService.count(),
                starService.getAverageRating(),
                starService.getTopRated() != null ? starService.getTopRated().getName() : "N/A");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Stars Gallery - Celebrity Database");
        startActivity(Intent.createChooser(shareIntent, "Share Stars Gallery via..."));
    }

    private void addRandomStar() {
        starService.addRandomStar();
        starAdapter.refreshData(starService.findAll());
        updateStats();
        Toast.makeText(this, "New star added! ✨", Toast.LENGTH_SHORT).show();
    }

    private void sortByRating() {
        List<Star> sortedStars = starService.getByRating(0f);
        starAdapter.refreshData(sortedStars);
        Toast.makeText(this, "Sorted by rating! 📊", Toast.LENGTH_SHORT).show();
    }

    private void showFavorites() {
        List<Star> favorites = starService.getFavorites();
        if (favorites.isEmpty()) {
            Toast.makeText(this, "No favorites yet! ❤️", Toast.LENGTH_SHORT).show();
            starAdapter.refreshData(starService.findAll());
        } else {
            starAdapter.refreshData(favorites);
            Toast.makeText(this, String.format("Showing %d favorites! ❤️", favorites.size()), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetData() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Data")
                .setMessage("Are you sure you want to reset all data? This will restore the original star list.")
                .setPositiveButton("RESET", (dialog, which) -> {
                    starService.clear();
                    starService = StarService.getInstance();
                    starAdapter.refreshData(starService.findAll());
                    updateStats();
                    Toast.makeText(this, "Data reset! 🔄", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    @Override
    public void onStarClick(Star star, int position) {
        starAdapter.showRatingDialog(star, position);
    }

    @Override
    public void onStarLongClick(Star star, int position) {
        new AlertDialog.Builder(this)
                .setTitle(star.getName())
                .setMessage(String.format("🎭 %s\n" +
                                "🏆 Awards: %d\n" +
                                "⭐ Rating: %.1f\n" +
                                "🌍 Nationality: %s\n" +
                                "📅 Age: %d\n" +
                                "❤️ Favorite: %s\n\n" +
                                "What would you like to do?",
                        star.getProfession(),
                        star.getAwardsCount(),
                        star.getRating(),
                        star.getNationality(),
                        star.getAge(),
                        star.isFavorite() ? "Yes" : "No"))
                .setPositiveButton("Toggle Favorite", (dialog, which) -> {
                    star.toggleFavorite();
                    starAdapter.notifyItemChanged(position);
                    updateStats();
                    Toast.makeText(this, 
                            star.isFavorite() ? "Added to favorites! ❤️" : "Removed from favorites 💔", 
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Remove Star", (dialog, which) -> {
                    starAdapter.removeStar(position);
                    starService.delete(star);
                    updateStats();
                    Toast.makeText(this, "Star removed! 🗑️", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }
}
