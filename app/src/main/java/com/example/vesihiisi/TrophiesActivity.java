package com.example.vesihiisi;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Activity for displaying trophies that are granted from
 * continuous hydration.
 *
 * @author Adnan Avni
 * @author Nafisul Nazrul
 */
public class TrophiesActivity extends NavigationBarActivity {
    ImageView silverIcon, goldIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophies);

        silverIcon = findViewById(R.id.silverTrophyImageView);
        silverIcon.setVisibility(View.INVISIBLE);

        goldIcon = findViewById(R.id.goldTrophyImageView);
        goldIcon.setVisibility(View.INVISIBLE);
    }

    protected void onResume() {
        super.onResume();

        int overTarget = TrophyCalculator.amountOfCompletedDays(Global.readDayDataList());

        // Show trophies based on how many "completed" days there are.
        if (overTarget >= 1) {
            silverIcon.setVisibility(View.VISIBLE);
        }
        if (overTarget >= 3) {
            goldIcon.setVisibility(View.VISIBLE);
        }
    }
}