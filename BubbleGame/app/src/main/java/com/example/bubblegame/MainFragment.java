package com.example.bubblegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String LOADING_FRAGMENT = "LoadingFragment";
    private static final String PLAYERS_FRAGMENT = "PlayersFragment";
    private Button easyBtn;
    private Button mediumBtn;
    private Button hardBtn;
    private Button timerModeBtn;
    private Button versusModeBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container,false);
        easyBtn = rootView.findViewById(R.id.easyBtn);
        easyBtn.setOnClickListener(this);
        mediumBtn = rootView.findViewById(R.id.mediumBtn);
        mediumBtn.setOnClickListener(this);
        hardBtn = rootView.findViewById(R.id.hardBtn);
        hardBtn.setOnClickListener(this);
        timerModeBtn = rootView.findViewById(R.id.timerModeBtn);
        timerModeBtn.setOnClickListener(this);
        versusModeBtn = rootView.findViewById(R.id.versusBtn);
        versusModeBtn.setOnClickListener(this);
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
            case R.id.easyBtn:
                startEasyGame();
                break;
            case R.id.mediumBtn:
                startMediumGame();
                break;
            case R.id.hardBtn:
                startHardGame();
                break;
            case R.id.timerModeBtn:
                startTimerGame();
                break;
            case R.id.versusBtn:
                startVersusGame();
                break;
        }
    }

    private void startVersusGame() {
        Animation animation = AnimationUtils.loadAnimation(versusModeBtn.getContext(), R.anim.fadein);
        versusModeBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, new PlayerNames(), PLAYERS_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 200);
        versusModeBtn.startAnimation(animation);

    }

    private void startTimerGame() {
        Animation animation = AnimationUtils.loadAnimation(timerModeBtn.getContext(), R.anim.fadein);
        timerModeBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment loadingFragment = LoadingScreenFragment.newInstance(getResources().getString(R.string.timerModeTxt), "", "");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, loadingFragment, LOADING_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 200);
        timerModeBtn.startAnimation(animation);
    }

    private void startHardGame() {
        Animation animation = AnimationUtils.loadAnimation(hardBtn.getContext(), R.anim.fadein);
        hardBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment loadingFragment = LoadingScreenFragment.newInstance(getResources().getString(R.string.hardTxt), "", "");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, loadingFragment, LOADING_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 200);
        hardBtn.startAnimation(animation);
    }

    private void startMediumGame() {
        Animation animation = AnimationUtils.loadAnimation(mediumBtn.getContext(), R.anim.fadein);
        mediumBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment loadingFragment = LoadingScreenFragment.newInstance(getResources().getString(R.string.mediumTxt), "", "");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, loadingFragment, LOADING_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 200);
        mediumBtn.startAnimation(animation);
    }

    private void startEasyGame() {
        Animation animation = AnimationUtils.loadAnimation(easyBtn.getContext(), R.anim.fadein);
        easyBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment loadingFragment = LoadingScreenFragment.newInstance(getResources().getString(R.string.easyTxt), "", "");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.introductionMain, loadingFragment, LOADING_FRAGMENT);
                fragmentTransaction.commit();
            }
        }, 200);
        easyBtn.startAnimation(animation);
    }
}
