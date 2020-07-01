package com.example.bubblegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import static android.content.Context.MODE_PRIVATE;

public class IntroductionMainFragment extends Fragment implements View.OnClickListener {

    private static final String SETTINGS_FRAGMENT = "SettingsFragment";
    private static final String LEADERBOARDS_FRAGMENT = "LeaderboardsFragment";
    private static final String LEVEL_FRAGMENT = "LevelFragment";
    private SharedPreferences sharedpreferences;
    private Button startBtn;
    private Button settingsBtn;
    private Button leaderboardsBtn;
    private LinearLayout mainContentLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = this.getActivity().getSharedPreferences("GamePreferences", MODE_PRIVATE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.introduction_main_fragment, container,false);
        mainContentLayout = rootView.findViewById(R.id.introductionMainContent);
        startBtn = rootView.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);
        settingsBtn = rootView.findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(this);
        leaderboardsBtn = rootView.findViewById(R.id.leaderboardsBtn);
        leaderboardsBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startBtn:
                startClick();
                break;
            case R.id.settingsBtn:
                settingsClick();
                break;
            case R.id.leaderboardsBtn:
                leaderboardsClick();
                break;
        }
    }

    private void leaderboardsClick() {
        Animation animation = AnimationUtils.loadAnimation(leaderboardsBtn.getContext(), R.anim.blink_anim);
        leaderboardsBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, new LeaderboardsFragment(), LEADERBOARDS_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 700);
        leaderboardsBtn.startAnimation(animation);
    }

    private void settingsClick() {
        Animation animation = AnimationUtils.loadAnimation(settingsBtn.getContext(), R.anim.blink_anim);
        settingsBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                showSettings();
            }
        }, 700);
        settingsBtn.startAnimation(animation);
    }

    private void startClick() {
        Animation animation = AnimationUtils.loadAnimation(startBtn.getContext(), R.anim.blink_anim);
        startBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, new MainFragment(), LEVEL_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 700);
        startBtn.startAnimation(animation);
    }

    private void showSettings() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.introductionMain, new SettingsFragment(), SETTINGS_FRAGMENT);
        fragmentTransaction.commit();
    }
}
