package com.example.starsgallery.ui;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.starsgallery.R;
import com.example.starsgallery.adapter.StarAdapterEnhanced;
import com.example.starsgallery.beans.Star;
import com.example.starsgallery.service.StarService;
import java.util.List;

public class GridListActivity extends AppCompatActivity implements StarAdapterEnhanced.OnStarClickListener {

    private RecyclerView recyclerView;
    private StarAdapterEnhanced starAdapter;
    private TextView statsText;
    private StarService starService;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_grid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Stars Gallery");
            getSupportActionBar().setSubtitle("Recherche par nom");
        }

        starService = StarService.getInstance();
        initializeViews();
        setupRecyclerView();
        updateStats();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view);
        statsText = findViewById(R.id.stats_text);
    }

    private void setupRecyclerView() {
        List<Star> stars = starService.findAll();
        starAdapter = new StarAdapterEnhanced(this, stars);
        starAdapter.setOnStarClickListener(this);
        
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columns
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(starAdapter);
    }

    private void updateStats() {
        List<Star> allStars = starService.findAll();
        int totalStars = allStars.size();
        int favorites = starService.getFavorites().size();
        float avgRating = starService.getAverageRating();
        Star topRated = starService.getTopRated();
        
        String stats = String.format("Total: %d | Favoris: %d | Moyenne: %.1f⭐ | Top: %s", 
                totalStars, favorites, avgRating, 
                topRated != null ? topRated.getName() : "N/A");
        statsText.setText(stats);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        
        searchView.setQueryHint("Rechercher par nom...");
        searchView.setIconifiedByDefault(false);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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

        MenuItem shareItem = menu.findItem(R.id.share);
        shareItem.setOnMenuItemClickListener(item -> {
            shareApp();
            return true;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.share) {
            shareApp();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String shareText = String.format("🌟 Stars Gallery! 🌟\n\n" +
                        "Parcourir %d célébrités!\n" +
                        "Note moyenne: %.1f⭐\n" +
                        "Top star: %s\n\n" +
                        "Télécharger maintenant!",
                starService.count(),
                starService.getAverageRating(),
                starService.getTopRated() != null ? starService.getTopRated().getName() : "N/A");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Stars Gallery - Base de données de célébrités");
        startActivity(Intent.createChooser(shareIntent, "Partager Stars Gallery via..."));
    }

    @Override
    public void onStarClick(Star star, int position) {
        showProfileDialog(star);
    }

    private void showProfileDialog(Star star) {
        new AlertDialog.Builder(this)
                .setTitle(star.getName())
                .setMessage(String.format("🎭 %s\n" +
                                "🏆 Récompenses: %d\n" +
                                "⭐ Note: %.1f\n" +
                                "🌍 Nationalité: %s\n" +
                                "📅 Âge: %d\n" +
                                "❤️ Favori: %s",
                        star.getProfession(),
                        star.getAwardsCount(),
                        star.getRating(),
                        star.getNationality(),
                        star.getAge(),
                        star.isFavorite() ? "Oui" : "Non"))
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }
}
