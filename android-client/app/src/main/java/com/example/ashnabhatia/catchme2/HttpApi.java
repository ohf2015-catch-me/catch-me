package com.example.ashnabhatia.catchme2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by bananer on 26.09.15.
 */
public final class HttpApi {

    private static final String userIdHeaderField = "catch-me-uuid";
    private static String userId;

    protected static AsyncHttpClient client = new AsyncHttpClient();


    private static String encodeBitmap(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public interface ApiListener<T> {
        void onDone(T result);
    }

    public static void setupAuth(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CatchMeHttpApi", 0);
        userId = prefs.getString("userId", "");
        if(userId.equals("")) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString("userId", userId);
        }
        client.addHeader(userIdHeaderField, userId);
    }

    public interface ApiObjectListener extends ApiListener<JSONObject> {}

    public static class ObjectListenerResponseHandler extends AsyncHttpResponseHandler {

        private ApiObjectListener mListener;
        public ObjectListenerResponseHandler(ApiObjectListener listener) {
            mListener = listener;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            try {
                mListener.onDone(new JSONObject(new String(responseBody)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

        }
    }

    public static void getGameDetails(String gameId, ApiObjectListener listener) {
        client.get(ApiUrls.gameDetails(gameId).toString(), new ObjectListenerResponseHandler(listener));
    }

    public static void getMyGame(ApiObjectListener listener) {
        client.get(ApiUrls.myGame().toString(), new ObjectListenerResponseHandler(listener));
    }

    public static void createGame(final String text, final Bitmap picture, ApiObjectListener listener) {
        try {
            JSONObject data = new JSONObject();
            data.put("text", text);
            // TODO:
            data.put("picture", "=");

            client.post(null, ApiUrls.createGame().toString(), new StringEntity(data.toString()),
                    "application/json", new ObjectListenerResponseHandler(listener));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createQuestion(String gameId, final String text, ApiObjectListener listener) {
        try {
            JSONObject data = new JSONObject();
            data.put("text", text);

            client.post(null, ApiUrls.createQuestion(gameId).toString(), new StringEntity(data.toString()),
                    "application/json", new ObjectListenerResponseHandler(listener));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void answerQuestion(String gameId, String questionId, final String answer, ApiObjectListener listener) {

        try {
            JSONObject data = new JSONObject();
            data.put("answer", answer);

            client.put(null, ApiUrls.createQuestion(gameId).toString(), new StringEntity(data.toString()),
                    "application/json", new ObjectListenerResponseHandler(listener));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createHint(String gameId, final String text, final Bitmap picture, ApiObjectListener listener) {
        /*try {
            JSONObject data = new JSONObject();
            data.put("text", text);

            client.post(null, ApiUrls.createHint(gameId).toString(), new StringEntity(data.toString()),
                    "application/json", new ObjectListenerResponseHandler(listener));

        } catch (Exception e) {
            e.printStackTrace();
        }

        (new PostJSONObjectRequest(ApiUrls.createQuestion(gameId), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("text", text);
                data.put("picture", encodeBitmap(picture));
                return data;
            }
        }).execute();*/
    }


    public static void foundTarget(String gameId, final String secret, ApiObjectListener listener) {
        /*(new PostJSONObjectRequest(ApiUrls.createQuestion(gameId), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("secret", secret);
                return data;
            }
        }).execute();*/
    }

}
