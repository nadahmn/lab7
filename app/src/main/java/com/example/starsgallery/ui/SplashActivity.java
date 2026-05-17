package com.example.starsgallery.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.starsgallery.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        handler = new Handler(Looper.getMainLooper());

        // Simple star animation
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(logo, "scaleX", 0.5f, 1.2f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(logo, "scaleY", 0.5f, 1.2f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleUp, scaleUpY, rotate);
        animatorSet.setDuration(2000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();

        // Navigate after 3 seconds
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, ListActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
