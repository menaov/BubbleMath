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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static android.content.Context.MODE_PRIVATE;

public class LeaderboardsFragment extends Fragment implements View.OnClickListener {

    private Button easyBtn;
    private Button mediumBtn;
    private Button hardBtn;
    private Button timerBtn;
    private SharedPreferences sharedPreferences;
    private ListView leadersTable;
    private List<Map<String, Object>> data;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("GamePreferences", MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_leaderboards, container,false);
        easyBtn = rootView.findViewById(R.id.easy_button);
        easyBtn.setOnClickListener(this);
        mediumBtn = rootView.findViewById(R.id.medium_button);
        mediumBtn.setOnClickListener(this);
        hardBtn = rootView.findViewById(R.id.hard_button);
        hardBtn.setOnClickListener(this);
        timerBtn = rootView.findViewById(R.id.timer_button);
        timerBtn.setOnClickListener(this);
        leadersTable = rootView.findViewById(R.id.leadersTable);
        rootView.invalidate();
        View header = inflater.inflate(R.layout.table_header, null);
        leadersTable.addHeaderView(header);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        showEasyLeaders();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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
            case R.id.easy_button:
                showEasyLeaders();
                break;
            case R.id.medium_button:
                showMediumLeaders();
                break;
            case R.id.hard_button:
                showHardLeaders();
                break;
            case R.id.timer_button:
                showTimerLeaders();
                break;
        }
    }

    private void showTimerLeaders() {
        Animation animation = AnimationUtils.loadAnimation(timerBtn.getContext(), R.anim.bounce);
        timerBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                setLeaderListByLevel(getResources().getString(R.string.timerModeTxt));
            }
        }, 200);
        timerBtn.startAnimation(animation);
    }

    private void showHardLeaders() {
        Animation animation = AnimationUtils.loadAnimation(hardBtn.getContext(), R.anim.bounce);
        hardBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                setLeaderListByLevel(getResources().getString(R.string.hardTxt));
            }
        }, 200);
        hardBtn.startAnimation(animation);
    }

    private void showMediumLeaders() {
        Animation animation = AnimationUtils.loadAnimation(mediumBtn.getContext(), R.anim.bounce);
        mediumBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                setLeaderListByLevel(getResources().getString(R.string.mediumTxt));
            }
        }, 200);
        mediumBtn.startAnimation(animation);

    }

    private void showEasyLeaders() {
        Animation animation = AnimationUtils.loadAnimation(easyBtn.getContext(), R.anim.bounce);
        easyBtn.postOnAnimationDelayed(new Runnable() {
            @Override
            public void run() {
                setLeaderListByLevel(getResources().getString(R.string.easyTxt));
            }
        }, 200);
        easyBtn.startAnimation(animation);
    }

    private void setLeaderListByLevel(String iLevel) {
        Set<String> leaders = getLeaders(iLevel);
        String [] from = {"Rank","Name","Score"};
        int [] ids = {R.id.rankOutput, R.id.nameOutput, R.id.scoreOutput};

        initializeTable();
        if(leaders == null){
            HashMap<String, Object> toAdd = new HashMap<>();
            toAdd.put("Rank", " ");
            toAdd.put("Name", " ");
            toAdd.put("Score", " ");
            data.add(toAdd);
            SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(), data, R.layout.leader_cell,from,ids);
            leadersTable.setAdapter(simpleAdapter);
        }
        else{
            List<String> sortedLeaders = new ArrayList<>(leaders);
            Collections.sort(sortedLeaders, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String [] words1 = o1.split(" ");
                    String [] words2 = o2.split(" ");
                    if(Integer.parseInt(words1[2]) > Integer.parseInt(words2[2]))
                        return -1;
                    if(Integer.parseInt(words1[2]) > Integer.parseInt(words2[2]))
                        return 1;
                    return 0;
                }
            });
            leaders = new LinkedHashSet(sortedLeaders);
            Iterator<String> leader = leaders.iterator();
            int rank = 1;
            while(leader.hasNext()){
                HashMap<String, Object> toAdd = new HashMap<>();
                String [] words = leader.next().split(" ");
                toAdd.put("Rank", rank);
                toAdd.put("Name", words[1]);
                toAdd.put("Score", words[2]);
                data.add(0, toAdd);
                rank++;
            }

            if(data != null){
                Collections.sort(data, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        if((int)o1.get("Rank") < (int)o2.get("Rank"))
                            return -1;
                        if((int)o1.get("Rank") > (int)o2.get("Rank"))
                            return 1;
                        return 0;
                    }
                });
                SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(), data, R.layout.leader_cell,from,ids);
                leadersTable.setAdapter(simpleAdapter);
            }
        }
    }

    private Set<String> getLeaders(String iLevel) {
            Set<String> result;
            if(iLevel.equals(getResources().getString(R.string.easyTxt)))
                result = sharedPreferences.getStringSet("easyLeaders", null);
            else if(iLevel.equals(getResources().getString(R.string.mediumTxt)))
                result = sharedPreferences.getStringSet("mediumLeaders", null);
            else if(iLevel.equals(getResources().getString(R.string.hardTxt)))
                result = sharedPreferences.getStringSet("hardLeaders", null);
            else
                result = sharedPreferences.getStringSet("timerLeaders", null);

            return result;
    }

    private void initializeTable() {
        data = new ArrayList<>();
        leadersTable.invalidateViews();
        this.getView().invalidate();
    }

    private Set<String> getScoresByLevel(String iLevel) {
        Set<String> result;
        if(iLevel.equals(getResources().getString(R.string.easyTxt)))
            result = sharedPreferences.getStringSet("easyScores", null);
        else if(iLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = sharedPreferences.getStringSet("mediumScores", null);
        else if(iLevel.equals(getResources().getString(R.string.hardTxt)))
            result = sharedPreferences.getStringSet("hardScores", null);
        else
            result = sharedPreferences.getStringSet("timerScores", null);

        return result;
    }

    private Set<String> getLeadersNameByLevel(String iLevel) {
        Set<String> result;
        if(iLevel.equals(getResources().getString(R.string.easyTxt)))
            result = sharedPreferences.getStringSet("easyNames", null);
        else if(iLevel.equals(getResources().getString(R.string.mediumTxt)))
            result = sharedPreferences.getStringSet("mediumNames", null);
        else if(iLevel.equals(getResources().getString(R.string.hardTxt)))
            result = sharedPreferences.getStringSet("hardNames", null);
        else
            result = sharedPreferences.getStringSet("timerNames", null);

        return result;
    }
}
