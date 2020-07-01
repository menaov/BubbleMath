package com.example.bubblegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillValue;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.app.progresviews.ProgressWheel;
import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.content.Context.MODE_PRIVATE;

public class Game extends Fragment implements View.OnClickListener {
    private static final String SHOWCASE_ID = "case";
    private static final int REQUEST_CODE = 1;
    private static final String MAIN_FRAGMENT = "MainFragment";
    private static final String RECORD_FRAGMENT = "RecordFragment";
    private static final String GAMEOVER_FRAGMENT = "GameOverFragment";
    private static final String WINNER_FRAGMENT = "WinnerFragment";
    private BubblePicker picker;
    private String mQuestion;
    private TextView mQuestionText;
    private TextView mScoreText;
    private TextView mPlayerNameText;
    private ProgressWheel timerProgress;
    private CountDownTimer countDownTimer;
    private long timeLeft;
    private boolean timerRunning;
    private int mHardAnswer;
    private int mEasyAnswer;
    private int mMediumAnswer;
    private int mScore;
    private int mScore2;
    private LinearLayout pickerLayout;
    private LinearLayout mainLayout;
    private int NUM_OF_ANSWERS;
    private int mTimerModeAnswer;
    private MediaPlayer backgroundMusic;
    private ImageButton hintBtn;
    private String mLevel;
    private String mPlayer1Name;
    private String mPlayer2Name;
    private RatingBar mLivesBar;
    private boolean hintClicked = false;
    private SharedPreferences sharedPreferences;
    private String mNameResult;
    private String mCurrentPlayer;
    private int numOfMistakes = 0;
    private boolean inspected = false;
    private boolean isFinished = false;

    public static Game newInstance(String iLevel, String iPlayer1, String iPlayer2){
        Game game = new Game();
        Bundle bundle = new Bundle();
        bundle.putString("Level", iLevel);
        if(iPlayer1 != null && iPlayer2 != null){
            if(!iPlayer1.isEmpty() && !iPlayer2.isEmpty()){
                bundle.putString("Player1", iPlayer1);
                bundle.putString("Player2", iPlayer2);
            }
        }
        game.setArguments(bundle);
        return game;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void checkIfTwoPlayers() {
        if(mLevel.equals(getString(R.string.two_players))){
            mPlayer1Name = getArguments().getString("Player1");
            mPlayer2Name = getArguments().getString("Player2");
            mCurrentPlayer = mPlayer1Name;
            mPlayerNameText.setText(mCurrentPlayer);
            mPlayerNameText.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_game, container,false);
        sharedPreferences = getActivity().getSharedPreferences("GamePreferences", MODE_PRIVATE);
        mLevel = getArguments().getString("Level");
        mLivesBar = rootView.findViewById(R.id.lives);
        mPlayerNameText = rootView.findViewById(R.id.PlayerName);
        setLivesVisibility();
        checkIfTwoPlayers();
        setNumOfAnswers();
        mainLayout = rootView.findViewById(R.id.mainLayout);
        pickerLayout = rootView.findViewById(R.id.pickerLayout);
        picker = rootView.findViewById(R.id.bubblePicker);
        picker.setCenterImmediately(true);
        picker.setClipBounds(pickerLayout.getClipBounds());
        picker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem item) {
                String timerMode = getResources().getString(R.string.timerModeTxt);
                checkAnswer(item, mLevel);
                item.setSelected(false);
                picker.setSelected(false);
                picker.requestLayout();
                picker.invalidate();
                pickerLayout.removeAllViews();
                pickerLayout.addView(picker);
                if(!mLevel.equals(getResources().getString(R.string.timerModeTxt)))
                    setTimeByLevel(mLevel);
                if(!mLevel.equals(timerMode))
                    startStopTimer();
            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem item) {
            }
        });
        picker.setBubbleSize(40);

        timerProgress = rootView.findViewById(R.id.timerProgress);
        mScore = 0;
        mScore2 = 0;
        mQuestionText = rootView.findViewById(R.id.questionText);
        mScoreText = rootView.findViewById(R.id.scoreText);
        getQuestionsByLevels(mLevel);
        setAnswersByLevel(mLevel, false);
        startBackgroundMusic();
        hintBtn = rootView.findViewById(R.id.hintBtn);
        hintBtn.setOnClickListener(this);
        setTimeByLevel(mLevel);
        showGuide();
        startStopTimer();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        picker.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBackgroundMusic();
        picker.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setTimeByLevel(String mLevel) {
        if(mLevel.equals(getString(R.string.easyTxt))){
            timeLeft = 31000;
        }
        else if (mLevel.equals(getString(R.string.mediumTxt))){
            timeLeft = 21000;
        }
        else if (mLevel.equals(getString(R.string.hardTxt))){
            timeLeft = 11000;
        }
        else if (mLevel.equals(getString(R.string.timerModeTxt))){
            timeLeft = 61000;
        }
        else if (mLevel.equals(getString(R.string.two_players))){
            timeLeft = 16000;
        }
    }

    private void setNumOfAnswers() {
        if(mLevel.equals(getResources().getString(R.string.easyTxt)))
            NUM_OF_ANSWERS = 7;
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt)))
            NUM_OF_ANSWERS = 10;
        else
            NUM_OF_ANSWERS = 12;
    }

    private void setLivesVisibility() {
        if(mLevel.equals(getResources().getString(R.string.timerModeTxt)))
            mLivesBar.setVisibility(View.INVISIBLE);
        if(mLevel.equals(getResources().getString(R.string.two_players)))
            mLivesBar.setVisibility(View.GONE);
    }

    private void startBackgroundMusic() {
        if(sharedPreferences.getBoolean("BackgroundMusic", true) && Game.this.getContext() != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backgroundMusic = MediaPlayer.create(Game.this.getContext(), R.raw.thinking);
                    backgroundMusic.setLooping(true);
                    backgroundMusic.start();
                }
            }, 500);
        }
    }

    private void stopBackgroundMusic(){
        if(backgroundMusic != null)
            backgroundMusic.stop();
    }

    private void showGuide() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(Game.this.getActivity(), SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(mScoreText,
                getString(R.string.this_is_score), getString(R.string.gotit_txt));
        sequence.addSequenceItem(mLivesBar,
                getString(R.string.this_lives), getString(R.string.gotit_txt));
        sequence.addSequenceItem(timerProgress,
                getString(R.string.this_is_timer), getString(R.string.gotit_txt));
        sequence.addSequenceItem(mQuestionText,
                getString(R.string.this_is_question_txt), getString(R.string.gotit_txt));
        sequence.addSequenceItem(hintBtn,
                getString(R.string.this_hint_txt), getString(R.string.gotit_txt));
        sequence.addSequenceItem(picker,
                getString(R.string.this_answers_txt), getString(R.string.gotit_txt));
        sequence.start();
    }

    private void startStopTimer(){
        if(timerRunning)
            stopTimer();
        else
            startTimer();
    }
    
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                if(mLevel.equals(getString(R.string.two_players)))
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showWinner(false);
                        }
                    }, 50);
                }
                else if(mLevel.equals(getString(R.string.timerModeTxt)))
                    gameOver();
                else
                    reduceLives();
            }
        }.start();
        timerRunning = true;
    }
    
    public void stopTimer(){
        countDownTimer.cancel();
        timerRunning = false;
    }
    
    private void updateTimer() {
        int fullProgress = getLevelTime(mLevel);
        timerProgress.setPercentage((int)((timeLeft * 360) / fullProgress));
        timerProgress.invalidate();
        timerProgress.setStepCountText(String.valueOf((int)timeLeft % 60000 / 1000));
    }

    private int getLevelTime(String mLevel) {
        int result = 0;
        if(mLevel.equals(getString(R.string.easyTxt)))
            result = 31000;
        else if (mLevel.equals(getString(R.string.mediumTxt)))
            result = 21000;
        else if (mLevel.equals(getString(R.string.hardTxt)))
            result = 11000;
        else if (mLevel.equals(getString(R.string.timerModeTxt)))
            result = 61000;
        else if (mLevel.equals(getString(R.string.two_players)))
            result = 16000;

        return result;

    }

    private void checkAnswer(PickerItem iItem, String iLevel) {
        String easy = getResources().getString(R.string.easyTxt);
        String medium = getResources().getString(R.string.mediumTxt);
        String hard = getResources().getString(R.string.hardTxt);
        String timerMode = getResources().getString(R.string.timerModeTxt);
        String versusMode = getResources().getString(R.string.two_players);
        if ((iItem.getTitle().equals(String.valueOf(mEasyAnswer))) ||
                (iItem.getTitle().equals(String.valueOf(mMediumAnswer))) ||
                    (iItem.getTitle().equals(String.valueOf(mHardAnswer))) ||
                        (iItem.getTitle().equals(String.valueOf(mTimerModeAnswer)))) {
            if (inspected) {
                updateScores();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showWinner(false);
                    }
                }, 50);
            }
            else
                nextQuestion(iLevel, false);
        }
        else
            if(!iLevel.equals(timerMode) && !iLevel.equals(versusMode))
                reduceLives();
            else {
                playIncorrectSound();
                if(iLevel.equals(timerMode)) {
                    getQuestionsByLevels(iLevel);
                    setAnswersByLevel(iLevel,false);
                }
                else{
                    numOfMistakes++;
                    if(numOfMistakes > 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showWinner(true);
                            }
                        }, 50);
                    }
                    else if(numOfMistakes == 1) {
                        if((mScore > mScore2) || (mScore2 > mScore)) {
                            changeTurn();
                            setScreenOrientation();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showWinner(false);
                                }
                            }, 50);
                        }
                        else{
                            inspected = true;
                            nextQuestion(iLevel, false);
                        }
                    }
                }
            }
    }

    private void showWinner(boolean isTied) {
        setScreenOrientation();
        Fragment winnerFragment = WinnerFragment.newInstance(mPlayer1Name, mPlayer2Name, mScore, mScore2, isTied);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, winnerFragment, WINNER_FRAGMENT).commit();
    }

    private void reduceLives() {
        mLivesBar.setRating(mLivesBar.getRating() - 1);
        if(mLivesBar.getRating() == 0)
            gameOver();
        else {
            playIncorrectSound();
            nextQuestion(mLevel, true);
            setTimeByLevel(mLevel);
            startStopTimer();
        }
    }

    private void nextQuestion(String iLevel, boolean isReducing) {
        String timerMode = getResources().getString(R.string.timerModeTxt);
        String versusMode = getResources().getString(R.string.two_players);

        if(iLevel.equals(versusMode)){
            if(!inspected){
                playCorrectSound();
                updateScores();
            }
            else
                playIncorrectSound();
            changeTurn();
            mPlayerNameText.setText(mCurrentPlayer);
            setScreenOrientation();
            startStopTimer();
            getQuestionsByLevels(versusMode);
            setAnswersByLevel(versusMode, false);
            setScoreText();
        }
        else{
            if(!isReducing)
            {
                playCorrectSound();
                setScore(false);
            }
            if(!iLevel.equals(timerMode))
                startStopTimer();
            getQuestionsByLevels(iLevel);
            setAnswersByLevel(iLevel, false);
        }
    }

    private void setScoreText() {
        if(mCurrentPlayer.equals(mPlayer1Name))
            mScoreText.setText(String.valueOf(mScore) + " " + getString(R.string.points));
        else
            mScoreText.setText(String.valueOf(mScore2) + " " + getString(R.string.points));
    }

    private void updateScores() {
        if(mCurrentPlayer.equals(mPlayer1Name)){
            mScore += 100;
        }
        else{
            mScore2 += 100;
        }
    }

    private void setScreenOrientation() {
        if(mCurrentPlayer.equals(mPlayer1Name)){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
    }

    private void changeTurn() {
        if(mCurrentPlayer.equals(mPlayer1Name))
            mCurrentPlayer = mPlayer2Name;
        else
            mCurrentPlayer = mPlayer1Name;
    }

    private void setScore(boolean isHint) {
        if(mLevel.equals(getString(R.string.two_players))){
            if(mCurrentPlayer.equals(mPlayer1Name)){
                mScore -= 50;
                mScoreText.setText(String.valueOf(mScore) + " " + getString(R.string.points));
            }
            else{
                mScore2 -= 50;
                mScoreText.setText(String.valueOf(mScore2) + " " + getString(R.string.points));
            }
        }
        else{
            if(isHint)
                mScore -= 50;
            else
                mScore += 100;
            mScoreText.setText(String.valueOf(mScore) + " " + getString(R.string.points));
        }
    }

    private void playCorrectSound() {
        if(sharedPreferences.getBoolean("SoundEffects", true)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer correct = MediaPlayer.create(Game.this.getContext(), R.raw.correct);
                    correct.start();
                }
            }, 50);
        }
        hintClicked = false;
    }

    private void playIncorrectSound() {
        if(sharedPreferences.getBoolean("vibration", true)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Vibrator v = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v.vibrate(1000);
                    }
                }
            });

        }
        if (sharedPreferences.getBoolean("SoundEffects", true)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer incorrect = MediaPlayer.create(Game.this.getContext(), R.raw.wrong_answer);
                    incorrect.start();
                }
            }, 50);
        }
    }

    private void gameOver(){
        stopBackgroundMusic();
        if(checkIfRecordBraked())
            saveScore();
        else
            showGameOver();
    }

    private boolean checkIfRecordBraked() {
        Set<String> leaders = getLeaders();
        boolean result = false;
        if(leaders == null)
            result = true;
        if (leaders != null) {
            Iterator<String> leader = leaders.iterator();
            while(leader.hasNext()) {
                String [] words = leader.next().split(" ");
                if (Integer.parseInt(words[2]) < mScore) {
                    result = true;
                    break;
                }
            }
        }

        return result;
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

    private void saveScore() {
        if(mScore != 0)
            getPlayerName();
        else
            showGameOver();
    }

    private void getPlayerName() {
        Fragment recordFragment = Record.newInstance(mScore, mLevel);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, recordFragment, RECORD_FRAGMENT).commit();
    }

    private void showGameOver() {
        Fragment gameOverFragment = GameOverFragment.newInstance(true);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, gameOverFragment, GAMEOVER_FRAGMENT).commit();
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

    private void getQuestionsByLevels(String iLevel){
        String easy = getResources().getString(R.string.easyTxt);
        String medium = getResources().getString(R.string.mediumTxt);
        String hard = getResources().getString(R.string.hardTxt);
        String timerMode = getResources().getString(R.string.timerModeTxt);
        String versusMode = getResources().getString(R.string.two_players);
        if(iLevel.equals(easy))
            mQuestion = getEasyQuestion();
        else if(iLevel.equals(medium))
            mQuestion = getMediumQuestion();
        else if(iLevel.equals(hard))
            mQuestion = getHardQuestion();
        else if(iLevel.equals(timerMode))
            mQuestion = getTimerModeQuestion();
        else if(iLevel.equals(versusMode))
            mQuestion = getTimerModeQuestion();
        mQuestionText.setText(mQuestion);
    }

    private String getTimerModeQuestion() {
        Random rnd = new Random();
        String question;
        int type = rnd.nextInt(3);
        if(type == 0) {
            question = getEasyQuestion();
            mTimerModeAnswer = mEasyAnswer;
        }
        else if(type == 1){
            question = getMediumQuestion();
            mTimerModeAnswer = mMediumAnswer;
        }
        else {
            question = getHardQuestion();
            mTimerModeAnswer = mHardAnswer;
        }

        return question;
    }

    private String getHardQuestion() {
        Random rnd = new Random();
        String question;
        int first = rnd.nextInt(100);
        int second = rnd.nextInt(100);
        int third = rnd.nextInt(20);
        String questionPlus = String.valueOf(first) + ' ' + '+' + ' ' + second + ' ' + "*" + " " + third + " " + "=" + " " + "?";
        String questionMinus = String.valueOf(third) + ' ' + '*' + ' ' + second + ' ' + "-" + " " + first + " " + "=" + " " + "?";
        int type = rnd.nextInt(2);
        if(type == 1){
            mHardAnswer = (second * third) + first;
            question = questionPlus;
        }
        else {
            mHardAnswer = (third * second) - first;
            question = questionMinus;
        }

        return question;
    }

    private String getMediumQuestion() {
        Random rnd = new Random();
        int first = rnd.nextInt(20);
        int second = rnd.nextInt(20);
        String question = String.valueOf(first) + ' ' + '*' + ' ' + second + " " + "=" + " " + "?";
        mMediumAnswer = first * second;
        return question;
    }

    private String getEasyQuestion() {
        Random rnd = new Random();
        String question;
        int first = rnd.nextInt(100);
        int second = rnd.nextInt(100);
        String questionPlusResult = String.valueOf(first) + ' ' + '+' + ' ' + second + ' ' + '='  + ' ' + '?';
        String questionPlusParam;
        if(first > second)
            questionPlusParam = "?" + ' ' + '+' + ' ' + second + ' ' + '=' + ' ' + first;
        else
            questionPlusParam = "?" + ' ' + '+' + ' ' + first + ' ' + '=' + ' ' + second;
        String questionMinusResult = String.valueOf(first) + ' ' + '-' + ' ' + second + ' ' + '=' + ' ' + '?';
        String questionMinusParam;
        if(first > second)
            questionMinusParam = String.valueOf(first) + ' ' + '-' + ' ' + "?" + ' ' + '=' + ' ' + second;
        else
            questionMinusParam = String.valueOf(second) + ' ' + '-' + ' ' + "?" + ' ' + '=' + ' ' + first;
        int type = rnd.nextInt(4);
        if(type == 0)
        {
            mEasyAnswer = first + second;
            question = questionPlusResult;
        }
        else if (type == 1)
        {
            if(first > second)
                mEasyAnswer = first - second;
            else
                mEasyAnswer = second - first;
            question = questionPlusParam;
        }
        else if (type == 2)
        {
            mEasyAnswer = first - second;
            question = questionMinusResult;
        }
        else{
            if(first > second)
                mEasyAnswer = first - second;
            else
                mEasyAnswer = second - first;
            question = questionMinusParam;
        }

        return question;
    }

    private void setAnswersByLevel(String level, boolean isForHint) {
        String easy = getResources().getString(R.string.easyTxt);
        String medium = getResources().getString(R.string.mediumTxt);
        String hard = getResources().getString(R.string.hardTxt);
        String timerMode = getResources().getString(R.string.timerModeTxt);
        String versusMode = getResources().getString(R.string.two_players);
        if(level.equals(easy))
            setAnswers(mEasyAnswer, isForHint);
        else if(level.equals(medium))
            setAnswers(mMediumAnswer, isForHint);
        else if(level.equals(hard))
            setAnswers(mHardAnswer, isForHint);
        else if(level.equals(timerMode))
            setAnswers(mTimerModeAnswer, isForHint);
        else if(level.equals(versusMode))
            setAnswers(mTimerModeAnswer, isForHint);
    }

    private void setAnswers(int iAnswer, boolean isForHint){
        final String[] answers = setAnswersArray(iAnswer);
        if(isForHint) {
            int removed = 0;
            for(int i = 0; i < NUM_OF_ANSWERS; i++)
                if (!answers[i].equals(String.valueOf(iAnswer))) {
                    removed++;
                    if (removed > 3)
                        break;
                    answers[i] = "X";
                }
        }
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);
        final TypedArray images = getResources().obtainTypedArray(R.array.images);
        picker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return NUM_OF_ANSWERS;
            }

            @NotNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTextSize(75);
                item.setTypeface(ResourcesCompat.getFont(Game.this.getContext(), R.font.alloy_font));
                item.setTitle(answers[position]);
                item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                item.setTextColor(ContextCompat.getColor(Game.this.getContext(), android.R.color.white));
                item.setBackgroundImage(ContextCompat.getDrawable(Game.this.getContext(), images.getResourceId(0, 0)));
                return item;
            }
        });
    }

    private String[] setAnswersArray(int iAnswer) {
        final String[] answers;
        if(mLevel.equals(getResources().getString(R.string.easyTxt))){
            answers = getResources().getStringArray(R.array.answers_easy);
            answers[6] = String.valueOf(iAnswer);
        }
        else if(mLevel.equals(getResources().getString(R.string.mediumTxt))){
            answers = getResources().getStringArray(R.array.answers_medium);
            answers[9] = String.valueOf(iAnswer);
        }
        else{
            answers = getResources().getStringArray(R.array.answers_Hard);
            answers[11] = String.valueOf(iAnswer);
        }

        return answers;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hintBtn:
                hintClick();
                break;
        }
    }

    private void hintClick() {
        if(!hintClicked){
            if(mScore >= 50){
                getHint();
            }
            else
                Toast.makeText(Game.this.getContext(), R.string.not_enough_points, Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(Game.this.getContext(), R.string.one_hint_txt, Toast.LENGTH_SHORT).show();
    }

    private void getHint() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this.getContext());
        View dialogLayout = LayoutInflater.from(Game.this.getContext()).inflate(R.layout.hint_dialog, null);
        builder.setPositiveButton(R.string.yesTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                popBubbles();
                hintClicked = true;
            }
        });
        builder.setNegativeButton(R.string.noTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                hintClicked = false;
            }
        });
        builder.setView(dialogLayout);
        builder.show();
    }

    private void popBubbles() {
        setAnswersByLevel(mLevel, true);
        setScore(true);
        picker.setSelected(false);
        picker.requestLayout();
        picker.invalidate();
        pickerLayout.removeAllViews();
        pickerLayout.addView(picker);
        mainLayout.invalidate();
    }
}
