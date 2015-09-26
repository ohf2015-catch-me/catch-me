package com.example.ashnabhatia.catchme2;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;


public class GamesListActivity extends ListActivity implements View.OnClickListener {
    private Button mCreateGame;

    protected GamesListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // note that use read_comments.xml instead of our single_post.xml
        setContentView(R.layout.activity_game_list);
        mCreateGame = (Button)findViewById(R.id.buttoncg);
        mCreateGame.setOnClickListener(this);

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
                    ((TextView) view.findViewById(R.id.games_list_row_text)).setText(data.optString("text"));
                }

                return view;
            }
        };

        setListAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: reload games
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttoncg:

                startSecondActivity();
                break;


            default:
                break;
        }
    }
    public void startSecondActivity(){
        Intent i = new Intent(this, CreateGame.class);
        //i.putExtra("text_label",userid );
        startActivity(i);
    }
}
