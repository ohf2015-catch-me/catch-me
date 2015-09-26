package com.example.ashnabhatia.catchme2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by bananer on 26.09.15.
 */
public final class HttpApi {

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    protected abstract static class GetRequest<T> extends AsyncTask<URL, Void, T> {
        protected ApiListener<T> mListener;

        public GetRequest(ApiListener<T> listener) {
            mListener = listener;
        }

        protected final T doInBackground(URL... params) {
            InputStream inputStream = null;
            String result = "";
            try {


                URL url = params[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    result = convertInputStreamToString(in);
                }
                finally {
                    urlConnection.disconnect();
                }

                return convertResponse(result);

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                return null;
            }
        }
        public abstract T convertResponse(String response) throws Exception;

        @Override
        protected void onPostExecute(T t) {
            mListener.onDone(t);
        }
    }

    protected static class GetJSONObjectRequest extends GetRequest<JSONObject> {

        public GetJSONObjectRequest(ApiListener<JSONObject> listener) {
            super(listener);
        }

        @Override
        public JSONObject convertResponse(String response) throws Exception {
            return new JSONObject(response);
        }
    }

    public interface ApiListener<T> {
        void onDone(T result);
    }

    public interface ApiObjectListener extends ApiListener<JSONObject> {}

    public static void getGameDetails(String gameId, ApiObjectListener listener) {
        (new GetJSONObjectRequest(listener))
                .execute(ApiUris.gameDetails(gameId));
    }
}
