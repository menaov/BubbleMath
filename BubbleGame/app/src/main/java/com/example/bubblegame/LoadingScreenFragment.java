package com.example.bubblegame;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.util.Random;

public class LoadingScreenFragment extends Fragment {

    private static final String GAME_FRAGMENT = "GameFragment";
    private TextView hintText;
    private TextView timer;
    private static final int loadingScreenTime = 4000;
    private String mLevel;
    private String mPlayer1;
    private String mPlayer2;

    public static LoadingScreenFragment newInstance(String iLevel, String iPlayer1, String iPlayer2){
        LoadingScreenFragment lf = new LoadingScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Level", iLevel);
        if(!iPlayer1.isEmpty() && !iPlayer2.isEmpty()){
            bundle.putString("Player1", iPlayer1);
            bundle.putString("Player2", iPlayer2);
        }
        lf.setArguments(bundle);
        return lf;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        new CountDownTimer(loadingScreenTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf ((int) millisUntilFinished / 1000));
                setHintText();
            }

            @Override
            public void onFinish() {
                Fragment gameFragment = Game.newInstance(mLevel, mPlayer1, mPlayer2);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.introductionMain, gameFragment, GAME_FRAGMENT).commit();
            }
        }.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_loading_screen, container,false);
        mLevel = getArguments().getString("Level");
        if(mLevel.equals(getString(R.string.two_players)))
            getPlayerNames();
        hintText = rootView.findViewById(R.id.tvHint);
        timer = rootView.findViewById(R.id.tvTime);
        setHintText();
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

    private void getPlayerNames() {
        mPlayer1 = getActivity().getIntent().getStringExtra("Player1");
        mPlayer2 = getActivity().getIntent().getStringExtra("Player2");
    }

    private void setHintText() {
        Random rd = new Random();
        switch (rd.nextInt(4)){
            case 0:
                hintText.setText(getString(R.string.champion_hint));
                break;
            case 1:
                hintText.setText(getString(R.string.here_it_comes_hint));
                break;
            case 2:
                hintText.setText(getString(R.string.ready_for_challenge_hint));
                break;
            case 3:
                hintText.setText(getString(R.string.ready_for_math_hint));
                break;
        }
    }
}
