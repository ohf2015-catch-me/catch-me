package com.example.ashnabhatia.catchme2;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameActivity extends Activity {

    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_IS_TARGET = "is_target";

    protected boolean isTarget;
    protected String gameId;

    protected TimelineListAdapter mListAdapter;

    protected class TimelineListAdapter extends BaseAdapter {

        protected List<JSONObject> mEntries;

        public TimelineListAdapter() {
            mEntries = new ArrayList<>();
        }

        public void setData(JSONObject rawData) throws JSONException{

            JSONArray hints = rawData.optJSONArray("hints");
            JSONArray questions = rawData.optJSONArray("questions");

            mEntries = new ArrayList<>(hints.length() + questions.length());

            for(int i = 0; i< hints.length(); i++) {
                JSONObject e = hints.optJSONObject(i);
                e.put("type", "hint");
                mEntries.add(e);
            }
            for(int i = 0; i< questions.length(); i++) {
                JSONObject e = questions.optJSONObject(i);
                e.put("type", "question");
                mEntries.add(e);
            }

            Collections.sort(mEntries, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {
                    return (int)(HttpApi.parseDate(lhs.optString("updatedAT")).getTime() -
                            HttpApi.parseDate(rhs.optString("updatedAT")).getTime());
                }
            });

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mEntries.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return mEntries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject entry = getItem(position);

            String type = entry.optString("type");
            LayoutInflater li = getLayoutInflater();

            if(type.equals("hint")) {
                View v = li.inflate(R.layout.timeline_entry_hint, parent, false);

                return v;
            }
            else if(type.equals("question")) {
                View v = li.inflate(R.layout.timeline_entry_question, parent, false);

                String answer = entry.optString("answer");
                if(answer != null && !answer.isEmpty()) {
                    if(isTarget) {

                    }
                    else {
                        // "waiting for answer"
                    }
                }
                else {

                }

                return v;
            }

            throw new RuntimeException("unknown type");
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TimelineListAdapter();
        setContentView(R.layout.activity_game);
        ((ListView)findViewById(R.id.game_timeline)).setAdapter(mListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameId = getIntent().getExtras().getString(EXTRA_GAME_ID);
        isTarget = getIntent().getExtras().getBoolean(EXTRA_IS_TARGET);
        reload();
    }

    protected void reload() {
        HttpApi.getGameDetails(gameId, new HttpApi.ApiObjectListener() {
            @Override
            public void onDone(JSONObject result) {
                ((TextView)findViewById(R.id.game_text))
                        .setText(result.optString("text"));

                findViewById(R.id.submit_hint_form).setVisibility(isTarget ? View.VISIBLE : View.GONE);
                findViewById(R.id.submit_question_form).setVisibility(isTarget ? View.GONE : View.VISIBLE);

                try {
                    mListAdapter.setData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception err) {

            }
        });
    }

}
