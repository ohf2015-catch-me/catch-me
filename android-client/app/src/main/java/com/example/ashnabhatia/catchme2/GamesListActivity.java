package com.example.ashnabhatia.catchme2;


import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;


public class GamesListActivity extends ListActivity {
    private Button mCreateGame;
    private String myGameId = null;

    protected GamesListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // note that use read_comments.xml instead of our single_post.xml
        setContentView(R.layout.activity_game_list);
        mCreateGame = (Button)findViewById(R.id.buttoncg);
        mCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GamesListActivity.this, CreateGame.class);

                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter = new GamesListAdapter(this) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if( view == null ){
                    //We must create a View:
                    view = GamesListActivity.this.getLayoutInflater()
                            .inflate(R.layout.games_list_row, parent, false);
                }

                JSONObject data = getItem(position);

                if(data != null) {
                    ((TextView) view.findViewById(R.id.games_list_row_text))
                            .setText(data.optString("text"));

                    final ImageView picture = (ImageView) view.findViewById(R.id.games_list_row_picture);
                    picture.setImageResource(android.R.color.transparent);
                    String picData = data.optString("picture");
                    (new PictureHandling.DecodeTask(picData) {
                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            if(bitmap != null) {
                                picture.setImageBitmap(bitmap);
                            }
                        }
                    }).execute();
                }

                return view;
            }
        };
        ListView lv = (ListView)findViewById(android.R.id.list);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent;
                intent = new Intent(GamesListActivity.this, GameActivity.class);
                intent.putExtra(GameActivity.EXTRA_GAME_ID, mAdapter.getItem(position).optString("uuid"));
                intent.putExtra(GameActivity.EXTRA_IS_TARGET, false);
                startActivity(intent);
            }
        });
        setListAdapter(mAdapter);

        findViewById(R.id.buttoncg).setVisibility(View.VISIBLE);
        findViewById(R.id.my_game).setVisibility(View.GONE);


        HttpApi.getMyGame(new HttpApi.ApiObjectListener() {
            @Override
            public void onDone(JSONObject result) {
                findViewById(R.id.buttoncg).setVisibility(result != null ? View.GONE : View.VISIBLE);
                findViewById(R.id.my_game).setVisibility(result != null ? View.VISIBLE : View.GONE);

                myGameId = result.optString("uuid");

                if (result != null) {
                    P2PManager.getInstance(GamesListActivity.this)
                            .setMyGameId(result.optString("uuid"));

                    ((TextView) findViewById(R.id.my_game_text)).setText(result.optString("text"));

                    final ImageView picture = (ImageView) findViewById(R.id.my_game_picture);
                    picture.setImageResource(android.R.color.transparent);
                    String picData = result.optString("picture");
                    (new PictureHandling.DecodeTask(picData) {
                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            if (bitmap != null) {
                                picture.setImageBitmap(bitmap);
                            }
                        }
                    }).execute();
                }
            }
        });

        findViewById(R.id.my_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myGameId != null) {
                    Intent intent;
                    intent = new Intent(GamesListActivity.this, GameActivity.class);
                    intent.putExtra(GameActivity.EXTRA_GAME_ID, myGameId);
                    intent.putExtra(GameActivity.EXTRA_IS_TARGET, true);
                    startActivity(intent);
                }
            }
        });

    }
}
