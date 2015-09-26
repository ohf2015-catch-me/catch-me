package com.example.ashnabhatia.catchme2;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

    private static String encodeBitmap(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    protected abstract static class Request<T> extends AsyncTask<Void, Void, T> {
        protected ApiListener<T> mListener;
        protected URL mUrl;

        public Request(URL url, ApiListener<T> listener) {
            mListener = listener;
            mUrl = url;
        }

        protected abstract HttpURLConnection getConnection() throws Exception;

        protected final T doInBackground(Void... params) {
            InputStream inputStream = null;
            String result = "";
            try {

                HttpURLConnection conn = getConnection();

                try {
                    conn.connect();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    result = convertInputStreamToString(in);
                }
                finally {
                    conn.disconnect();
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

    protected abstract static class GetRequest<T> extends Request<T> {

        public GetRequest(URL url, ApiListener<T> listener) {
            super(url, listener);
        }

        public HttpURLConnection getConnection() throws Exception {
            return (HttpURLConnection) mUrl.openConnection();
        }
    }

    protected abstract static class DataRequest<T> extends Request<T> {
        public DataRequest(URL url, ApiListener<T> listener) {
            super(url, listener);
        }

        protected abstract JSONObject getData() throws Exception;

        public HttpURLConnection getConnection() throws Exception {
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream printout = new DataOutputStream(conn.getOutputStream ());
            printout.writeBytes(URLEncoder.encode(getData().toString(), "UTF-8"));
            printout.flush();
            printout.close();

            return conn;
        }
    }

    protected abstract static class PostRequest<T> extends DataRequest<T> {

        public PostRequest(URL url, ApiListener<T> listener) {
            super(url, listener);
        }

        public HttpURLConnection getConnection() throws Exception {
            HttpURLConnection ret = super.getConnection();
            ret.setRequestMethod("POST");
            return ret;
        }
    }


    protected abstract static class PutRequest<T> extends DataRequest<T> {

        public PutRequest(URL url, ApiListener<T> listener) {
            super(url, listener);
        }

        public HttpURLConnection getConnection() throws Exception {
            HttpURLConnection ret = super.getConnection();
            ret.setRequestMethod("POST");
            return ret;
        }
    }

    protected static class GetJSONObjectRequest extends GetRequest<JSONObject> {

        public GetJSONObjectRequest(URL url, ApiListener<JSONObject> listener) {
            super(url, listener);
        }

        @Override
        public JSONObject convertResponse(String response) throws Exception {
            return new JSONObject(response);
        }
    }

    protected abstract static class PostJSONObjectRequest extends PostRequest<JSONObject> {

        public PostJSONObjectRequest(URL url, ApiListener<JSONObject> listener) {
            super(url, listener);
        }

        @Override
        public JSONObject convertResponse(String response) throws Exception {
            return new JSONObject(response);
        }
    }


    protected abstract static class PutJSONObjectRequest extends PutRequest<JSONObject> {

        public PutJSONObjectRequest(URL url, ApiListener<JSONObject> listener) {
            super(url, listener);
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
        (new GetJSONObjectRequest(ApiUrls.gameDetails(gameId), listener)).execute();
    }

    public static void createGame(final String text, final Bitmap picture, ApiObjectListener listener) {
        (new PostJSONObjectRequest(ApiUrls.createGame(), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("text", text);
                data.put("picture", encodeBitmap(picture));
                return data;
            }
        }).execute();
    }

    public static void createQuestion(String gameId, final String text, ApiObjectListener listener) {
        (new PostJSONObjectRequest(ApiUrls.createQuestion(gameId), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("text", text);
                return data;
            }
        }).execute();
    }


    public static void answerQuestion(String gameId, String questionId, final String answer, ApiObjectListener listener) {
        (new PostJSONObjectRequest(ApiUrls.createQuestion(gameId), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("answer", answer);
                return data;
            }
        }).execute();
    }


    public static void createHint(String gameId, final String text, final Bitmap picture, ApiObjectListener listener) {
        (new PostJSONObjectRequest(ApiUrls.createQuestion(gameId), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("text", text);
                data.put("picture", encodeBitmap(picture));
                return data;
            }
        }).execute();
    }


    public static void foundTarget(String gameId, final String secret, ApiObjectListener listener) {
        (new PostJSONObjectRequest(ApiUrls.createQuestion(gameId), listener) {
            @Override
            protected JSONObject getData() throws Exception {
                JSONObject data = new JSONObject();
                data.put("secret", secret);
                return data;
            }
        }).execute();
    }

}
