package com.example.bubblegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class WinnerFragment extends Fragment implements View.OnClickListener {
    private static final String MAIN_FRAGMENT = "MainFragment";
    private static final String LEVEL_FRAGMENT = "LevelFragment";
    private TextView player1name;
    private TextView player2name;
    private TextView player1score;
    private TextView player2score;
    private TextView winnerTopic;
    private Button yesBtn;
    private Button noBtn;
    private KonfettiView konffeti;
    private SharedPreferences sharedPreferences;

    public static WinnerFragment newInstance(String iPlayer1Name, String iPlayer2Name, int iScore, int iScore2, boolean iIsTied){
        Bundle bundle = new Bundle();
        WinnerFragment winnerFragment = new WinnerFragment();
        bundle.putString("player1", iPlayer1Name);
        bundle.putString("player2", iPlayer2Name);
        bundle.putInt("player1score", iScore);
        bundle.putInt("player2score", iScore2);
        bundle.putBoolean("isTied", iIsTied);
        winnerFragment.setArguments(bundle);

        return winnerFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        sharedPreferences = getActivity().getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.winner_fragment, container,false);
        player1name = rootView.findViewById(R.id.player1Name);
        player2name = rootView.findViewById(R.id.player2name);
        player1score = rootView.findViewById(R.id.player1Score);
        player2score = rootView.findViewById(R.id.player2score);
        winnerTopic = rootView.findViewById(R.id.winnerTopic);
        yesBtn = rootView.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(this);
        noBtn = rootView.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(this);
        konffeti = rootView.findViewById(R.id.winnerKonffetti);
        setTexts();
        return rootView;
    }

    private void setTexts() {
        String p1name = getArguments().getString("player1");
        String p2name = getArguments().getString("player2");
        int p1Score = getArguments().getInt("player1score");
        int p2Score = getArguments().getInt("player2score");
        player1name.setText(p1name);
        player2name.setText(p2name);
        player1score.setText(String.valueOf(p1Score));
        player2score.setText(String.valueOf(p2Score));
        if(getArguments().getBoolean("isTied"))
            winnerTopic.setText(getString(R.string.tied_txt));
        else{
            if(p1Score > p2Score)
                winnerTopic.setText(getString(R.string.the_winner_is_txt) + " " + p1name);
            else
                winnerTopic.setText(getString(R.string.the_winner_is_txt) + " " + p2name);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        playWinEffect();
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
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();
    }

    private void yesClick() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, new MainFragment(), LEVEL_FRAGMENT).commit();
    }

    private void playWinEffect() {
        playWinSound();
        konffeti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12,5f))
                .setPosition(-50f, konffeti.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }

    private void playWinSound() {
        if(sharedPreferences.getBoolean("SoundEffects", true)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer yay = MediaPlayer.create(WinnerFragment.this.getContext(), R.raw.yay);
                    yay.start();
                }
            }, 50);
        }
    }
}
