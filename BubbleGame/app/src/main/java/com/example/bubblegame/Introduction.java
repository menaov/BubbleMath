package com.example.bubblegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Introduction extends AppCompatActivity{
    private static final String SETTINGS_FRAGMENT = "SettingsFragment";
    private static final String MAIN_FRAGMENT = "MainFragment";
    private static final String GAME_FRAGMENT = "GameFragment";
    private static final String LEADERBOARDS_FRAGMENT = "LeaderboardsFragment";
    private static final String LEVEL_FRAGMENT = "LevelFragment";
    private static final String PLAYER_FRAGMENT = "PlayersFragment";
    private static final String RECORD_FRAGMENT = "RecordFragment";
    private static final String WINNER_FRAGMENT = "WinnerFragment";
    private static final String HOW_FRAGMENT = "HowToFragment";
    private static final String ABOUT_FRAGMENT = "AboutFragment";
    private SharedPreferences sharedpreferences;
    private SensorEventListener mSensorEventListener;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        setTheme(R.style.AppTheme);
        sharedpreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(!(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        SensorManager sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sm.registerListener(mSensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        showMainFragment();
    }

    private void showMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.actionHowToPlay:
                showHowToPlay();
                break;
            case R.id.actionSettings:
                showSettings();
                break;
            case R.id.actionAbout:
                showAbout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHowToPlay() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.introductionMain, new HowToFragment(), HOW_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void showSettings() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.introductionMain, new SettingsFragment(), SETTINGS_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void showAbout() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.introductionMain, new AboutFragment(), ABOUT_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        openOptionsMenu();
        /////////////////////////////////////////////////////////////
        ////Check current Fragment and decide behaviour/////////////
        ///////////////////////////////////////////////////////////
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(MAIN_FRAGMENT))
            super.onBackPressed();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(SETTINGS_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(LEADERBOARDS_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(GAME_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new MainFragment(), LEVEL_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(LEVEL_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(PLAYER_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new MainFragment(), LEVEL_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(RECORD_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(WINNER_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(ABOUT_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
        if(this.getSupportFragmentManager().getFragments().get(0).getTag().equals(HOW_FRAGMENT))
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
    }
}
