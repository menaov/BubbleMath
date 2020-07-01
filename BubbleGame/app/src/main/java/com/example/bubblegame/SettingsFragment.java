package com.example.bubblegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String MAIN_FRAGMENT = "MainFragment";
    private Button saveBtn;
    private SharedPreferences sharedpreferences;
    private LinearLayout mainContent;
    private Switch backgroundMusicSwitch;
    private Switch soundEffectsSwitch;
    private Switch vibrationSwitch;


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
        View rootView = inflater.inflate(R.layout.custom_dialog_settings, container,false);
        saveBtn = rootView.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        backgroundMusicSwitch = rootView.findViewById(R.id.backgroundMusicSwitch);
        soundEffectsSwitch = rootView.findViewById(R.id.soundEffectsSwitch);
        vibrationSwitch = rootView.findViewById(R.id.vibrationSwitch);
        mainContent = rootView.findViewById(R.id.introductionMainContent);
        initializeSettings();
        return rootView;
    }

    private void initializeSettings() {
        boolean backgroundMusic = sharedpreferences.getBoolean("BackgroundMusic",true);
        boolean soundEffects = sharedpreferences.getBoolean("SoundEffects",true);
        boolean vibration = sharedpreferences.getBoolean("vibration",true);

        backgroundMusicSwitch.setChecked(backgroundMusic);
        soundEffectsSwitch.setChecked(soundEffects);
        vibrationSwitch.setChecked(vibration);
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
        save();
    }

    private void save() {
        Switch backgroundMusic = getView().findViewById(R.id.backgroundMusicSwitch);
        Switch soundEffects = getView().findViewById(R.id.soundEffectsSwitch);
        Switch vibration = getView().findViewById(R.id.vibrationSwitch);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("BackgroundMusic", backgroundMusic.isChecked());
        editor.putBoolean("SoundEffects", soundEffects.isChecked());
        editor.putBoolean("vibration", vibration.isChecked());
        editor.commit();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.introductionMain, new IntroductionMainFragment(), MAIN_FRAGMENT).commit();

        Toast.makeText(getContext(), R.string.settings_saved_txt, Toast.LENGTH_SHORT).show();
    }
}
