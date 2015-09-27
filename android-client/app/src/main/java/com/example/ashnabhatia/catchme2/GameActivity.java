package com.example.ashnabhatia.catchme2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameActivity extends Activity {

    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_IS_TARGET = "is_target";

    protected boolean isTarget;
    protected String gameId;

    private static final int TAKE_PICTURE = 1;
    private File outputFile;

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

        ((Button)findViewById(R.id.create_hint_picture_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                outputFile = PictureHandling.createImageFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));

                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        ((Button)findViewById(R.id.submit_hint_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(outputFile == null) {
                    return;
                }

                (new PictureHandling.ResizeEncodeTask(outputFile.getAbsolutePath()) {
                    @Override
                    protected void onPostExecute(String picBase64) {
                        final TextView text = (TextView) findViewById(R.id.hint_text);
                        HttpApi.createHint(gameId, text.getText().toString(), picBase64, new HttpApi.ApiObjectListener() {
                            @Override
                            public void onDone(JSONObject result) {
                                text.setText("");

                                ((ImageView) findViewById(R.id.create_hint_picture)).setImageResource(android.R.color.transparent);
                                outputFile = null;

                                reload();
                            }

                            @Override
                            public void onError(Exception err) {
                            }
                        });
                    }
                }).execute();
            }
        });

        ((Button)findViewById(R.id.submit_question_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText text = ((EditText) findViewById(R.id.question_text));

                HttpApi.createQuestion(gameId, text.getText().toString(), new HttpApi.ApiObjectListener() {
                    @Override
                    public void onDone(JSONObject result) {
                        text.setText("");
                        reload();
                    }

                    @Override
                    public void onError(Exception err) {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath(), options);

            ((ImageView) findViewById(R.id.create_hint_picture)).setImageBitmap(bitmap);

        }
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
