package com.example.bubblegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerNames extends Fragment implements View.OnClickListener {

    private static final String GAME_FRAGMENT = "GameFragment";
    private Button submitBtn;
    private EditText player1Name;
    private EditText player2Name;

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
        View rootView = inflater.inflate(R.layout.activity_player_names, container,false);
        submitBtn = rootView.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);
        player1Name = rootView.findViewById(R.id.player1Input);
        player2Name = rootView.findViewById(R.id.player2Input);
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
        submitClick();
    }

    private void submitClick() {
        String result1;
        String result2;

        if(!player1Name.getText().toString().isEmpty() && !player2Name.getText().toString().isEmpty()){
            Bundle bundle = new Bundle();
            Fragment gameFragment = new Game();
            result1 = player1Name.getText().toString();
            result2 = player2Name.getText().toString();
            bundle.putString("Player1", result1);
            bundle.putString("Player2", result2);
            bundle.putString("Level", getString(R.string.two_players));
            gameFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.introductionMain, gameFragment, GAME_FRAGMENT).commit();
        }
        else
            Toast.makeText(PlayerNames.this.getContext(), R.string.name_empty_txt, Toast.LENGTH_SHORT).show();
    }
}
