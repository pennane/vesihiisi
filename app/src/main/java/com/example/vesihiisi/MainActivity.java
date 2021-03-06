package com.example.vesihiisi;

import static com.example.vesihiisi.NotificationHandler.NOTIFICATION_TYPE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * Default Activity, the main screen of the application
 *
 * @author Arttu Pennanen
 * @author Adnan Avni
 * @author Nafisul Nazrul
 */
public class MainActivity extends NavigationBarActivity {
    DayData dayData;
    TextView targetConsumption, consumption, message;
    MotivationMessages motivationMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize global preferences
        Global.initialize(getApplicationContext());

        // Schedule notifications
        consumeWaterAlarmSchedule();

        // Redirect the user to settings if the settings are invalid or unset
        if (tryRedirectToSettings()) {
            return;
        }

        // Set a random motivational message
        message = findViewById(R.id.textView4);
        motivationMessages = new MotivationMessages();
        message.setText(motivationMessages.getRandomMessage());

        // Update target consumption on screen
        dayData = Global.readSpecificDayData(new Date());
        targetConsumption = findViewById(R.id.textView2);
        targetConsumption.setText(getString(R.string.dynamic_ml, dayData.getTargetConsumption()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (tryRedirectToSettings()) {
            return;
        }
        message.setText(motivationMessages.getRandomMessage());
        dayData = Global.readSpecificDayData(new Date());
        updateConsumptionValue();
    }

    private void showTargetAchievedToast() {
        Toast.makeText(this, R.string.target_achieved, Toast.LENGTH_SHORT).show();
    }


    /**
     * Display current water consumption amount on the activity
     */
    public void updateConsumptionValue() {
        consumption = findViewById(R.id.textView);
        consumption.setText(getString(R.string.dynamic_ml, dayData.getConsumption()));
        targetConsumption.setText(getString(R.string.dynamic_ml, dayData.getTargetConsumption()));
        Global.writeSpecificDayData(dayData, new Date());
    }

    /**
     * Handle consuming of a glass of water
     *
     * @param view of which the click comes from
     */
    public void onGlassButtonClick(View view) {
        dayData.consume(250);
        if (dayData.getConsumption() >= dayData.getTargetConsumption()) {
            showTargetAchievedToast();
        }
        updateConsumptionValue();
    }

    /**
     * Handle consuming of a bottle of water
     *
     * @param view of which the click comes from
     */
    public void onBottleButtonClick(View view) {
        dayData.consume(500);
        if (dayData.getConsumption() >= dayData.getTargetConsumption()) {
            showTargetAchievedToast();
        }
        updateConsumptionValue();
    }

    /**
     * Handle consuming of a jug of water
     *
     * @param view of which the click comes from
     */
    public void onJugButtonClick(View view) {
        dayData.consume(1000);
        if (dayData.getConsumption() >= dayData.getTargetConsumption()) {
            showTargetAchievedToast();
        }
        updateConsumptionValue();
    }

    /**
     * Redirect the current activity to settings, if saved preferences are invalid
     *
     * @return boolean
     */
    private boolean tryRedirectToSettings() {
        if (!Global.isValidAge(Global.readPreference("age", -1)) || !Global.isValidWeight(Global.readPreference("weight", -1)) || !Global.isValidGender(Global.readPreference("gender", "invalid"))) {
            Intent newActivity = new Intent(this, SettingsActivity.class);
            startActivity(newActivity);
            return true;
        }
        return false;
    }

    /**
     * Schedule alarm for water consumption notifications
     * <p>
     * If the alarm gets triggered between 22:00 and 08:88, it will be dropped by Notification handler.
     * <p>
     * Currently the notification is sent every 3 minutes. This is only for demoing purposes.
     * In reality the notifications would be sent maybe every 4 hours.
     */
    public void consumeWaterAlarmSchedule() {
        Intent intent = new Intent(getApplicationContext(), NotificationHandler.class);
        intent.putExtra(NOTIFICATION_TYPE, "CONSUME");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        // Schedule alarm avery 3 minutes for testing purposes
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                60 * 1000 * 3,
                pendingIntent);
    }
}