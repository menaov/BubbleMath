package com.example.bubblegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class GameOverFragment extends Fragment implements View.OnClickListener {

    private static final String MAIN_FRAGMENT = "MainFragment";
    private static final String LEVEL_FRAGMENT = "LevelFragment";
    private Button yesBtn;
    private Button noBtn;
    private SharedPreferences sharedPreferences;

    public static GameOverFragment newInstance(boolean iPlaySound){
        GameOverFragment gameOverFragment = new GameOverFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("Sound", iPlaySound);
        gameOverFragment.setArguments(bundle);
        return gameOverFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("GamePreferences", MODE_PRIVATE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_over_dialog, container,false);
        yesBtn = rootView.findViewById(R.id.yesBtn);
        noBtn = rootView.findViewById(R.id.noBtn);
        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments().getBoolean("Sound"))
            playGameOverSound();
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
        switch (v.getId()){
            case R.id.yesBtn:
                yesClick();
                break;
            case R.id.noBtn:
                noClick();
                break;
        }
    }

    private void noClick() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
    }

    private void yesClick() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, new MainFragment(), LEVEL_FRAGMENT).commit();
    }

    private void playGameOverSound() {
        if(sharedPreferences.getBoolean("SoundEffects", true)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer gameOver = MediaPlayer.create(GameOverFragment.this.getContext(), R.raw.game_over);
                    gameOver.start();
                }
            }, 50);
        }
    }
}
