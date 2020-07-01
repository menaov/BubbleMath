package com.example.bubblegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class Record extends Fragment implements View.OnClickListener {

    private static final String GAMEOVER_FRAGMENT = "GameOverFragment";
    private static final String LEADERS = "Leaders";
    private Button submitBtn;
    private EditText nameInput;
    private KonfettiView viewKonfetti;
    private String mNameResult;
    private int mScore;
    private SharedPreferences sharedPreferences;
    private String mLevel;

    public static Record newInstance(int iScore, String iLevel){
        Record recordFragment = new Record();
        Bundle bundle = new Bundle();
        bundle.putString("Level", iLevel);
        bundle.putInt("Score", iScore);
        recordFragment.setArguments(bundle);
        return recordFragment;
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
        View rootView = inflater.inflate(R.layout.activity_record, container,false);
        mScore = getArguments().getInt("Score");
        mLevel = getArguments().getString("Level");
        nameInput = rootView.findViewById(R.id.nameInput);
        submitBtn = rootView.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);
        viewKonfetti = rootView.findViewById(R.id.viewKonfetti);
        return rootView;
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

    private void playWinEffect() {
        playWinSound();
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12,5f))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }

    private void playWinSound() {
        if(sharedPreferences.getBoolean("SoundEffects", true))
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer yay = MediaPlayer.create(Record.this.getContext(), R.raw.yay);
                    yay.start();
                }
            }, 50);
        }
    }

    @Override
    public void onClick(View v) {
        submit();
    }

    private void submit() {
        if(!nameInput.getText().toString().isEmpty()){
            mNameResult = nameInput.getText().toString();
            submitScore();
        }
        else
            Toast.makeText(Record.this.getContext(), R.string.name_empty_txt, Toast.LENGTH_SHORT).show();
    }

    private void submitScore() {
        Set<String> leaders = getLeaders();
        Set<String> newLeaders = new LinkedHashSet<>();
        String[] words;
        String toAdd;
        int rank = 1;
        boolean inserted = false;

        if(leaders == null) {
            toAdd = String.valueOf(rank) + " " + mNameResult + " " + mScore;
            newLeaders.add(toAdd);
        }
        else {
            Iterator<String> leadersIterator = leaders.iterator();
            while (leadersIterator.hasNext()) {
                words = leadersIterator.next().split(" ");
                if (mScore > Integer.parseInt(words[2]) && !inserted) {
                    inserted = true;
                    toAdd = String.valueOf(rank) + " " + mNameResult + " " + mScore;
                    newLeaders.add(toAdd);
                    rank++;
                    toAdd = String.valueOf(rank) + " " + words[1] + " " + words[2];
                    newLeaders.add(toAdd);
                } else {
                    toAdd = String.valueOf(rank) + " " + words[1] + " " + words[2];
                    newLeaders.add(toAdd);
                }
                rank++;
            }
        }

        String key = getKeyByLevel();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, newLeaders);
        editor.commit();

        Fragment gameOverFragment = GameOverFragment.newInstance(false);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, gameOverFragment, GAMEOVER_FRAGMENT).commit();
    }

    private Set<String> getLeaders() {
        Set<String> result;
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            result = sharedPreferences.getStringSet("easyLeaders", null);
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = sharedPreferences.getStringSet("mediumLeaders", null);
        else if(mLevel.equals(getResources().getString(R.string.hardTxt)))
            result = sharedPreferences.getStringSet("hardLeaders", null);
        else
            result = sharedPreferences.getStringSet("timerLeaders", null);

        return result;
    }

    private String getKeyByLevel(){
        String result;
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            result = "easyLeaders";
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = "mediumLeaders";
        else if(mLevel.equals(getResources().getString(R.string.hardTxt)))
            result = "hardLeaders";
        else
            result = "timerLeaders";

        return result;
    }

    private String getScoreKeyByLevel() {
        String result;
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            result = "easyScores";
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = "mediumScores";
        else if(mLevel.equals(getResources().getString(R.string.hardTxt)))
            result = "hardScores";
        else
            result = "timerScores";

        return result;
    }

    private String getNameKeyByLevel() {
        String result;
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            result = "easyNames";
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = "mediumNames";
        else if(mLevel.equals(getResources().getString(R.string.hardTxt)))
            result = "hardNames";
        else
            result = "timerNames";

        return result;
    }

    private Set<String> getLeadersNameByLevel() {
        Set<String> result;
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            result = sharedPreferences.getStringSet("easyNames", null);
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = sharedPreferences.getStringSet("mediumNames", null);
        else if(mLevel.equals(getResources().getString(R.string.hardTxt)))
            result = sharedPreferences.getStringSet("hardNames", null);
        else
            result = sharedPreferences.getStringSet("timerNames", null);

        return result;
    }

    private Set<String> getScoresByLevel() {
        Set<String> result;
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            result = sharedPreferences.getStringSet("easyScores", null);
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = sharedPreferences.getStringSet("mediumScores", null);
        else if(mLevel.equals(getResources().getString(R.string.hardTxt)))
            result = sharedPreferences.getStringSet("hardScores", null);
        else
            result = sharedPreferences.getStringSet("timerScores", null);

        return result;
    }
}
