package com.example.ashnabhatia.catchme2;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bananer on 26.09.15.
 */
public abstract class GamesListAdapter extends BaseAdapter
{
    protected Context context;
    protected P2PManager manager;

    protected SortedMap<String, JSONObject> games = new TreeMap<>();

    public GamesListAdapter(Context ctx) {
        super();
        context = ctx;
        manager = P2PManager.getInstance(ctx);

        manager.addListener(gameListener);
    }

    protected void onGamesUpdated() {
        // TODO: sort games...
        notifyDataSetChanged();
    }

    private P2PManager.GameListener gameListener = new P2PManager.GameListener() {
        @Override
        public void onGameFound(final String gameId) {
            HttpApi.getGameDetails(gameId, new HttpApi.ApiObjectListener() {
                @Override
                public void onDone(JSONObject result) {
                    games.put(gameId, result);
                    onGamesUpdated();
                }
            });
        }
    };


    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public JSONObject getItem(int position) {
        if(position >= getCount()) {
            return null;
        }
        return games.values().toArray(new JSONObject[0])[position];
    }

    @Override
    public long getItemId(int position) {
        return games.keySet().toArray()[position].hashCode();
    }

}
