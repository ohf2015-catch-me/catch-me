package com.example.ashnabhatia.catchme2;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CreateGame extends AppCompatActivity implements View.OnClickListener {
    private static final int TAKE_PICTURE = 1;
    private Uri outputFileUri;
    private ImageView imgView;
    private Bitmap bitmap;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        imgView = (ImageView) findViewById(R.id.ImageView1);

        Button mUpload = (Button) findViewById(R.id.TakePicture);
        mUpload.setOnClickListener(this);
        Button button = (Button) findViewById(R.id.createGame);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.create_game_text);
                HttpApi.createGame(text.getText().toString(), null, new HttpApi.ApiObjectListener() {
                    @Override
                    public void onDone(JSONObject result) {
                        finish();
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.TakePicture:

                startSecondActivity();
                break;


            default:
                break;
        }
    }

    public void startSecondActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PICTURE) {
            //Uri contentURI = Uri.parse(data.getDataString());

            ContentResolver cr = getContentResolver();
            InputStream in = null;
            try {
                in = cr.openInputStream(outputFileUri);
                Log.i("URI ===> ", outputFileUri.getPath());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (in != null) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeStream(in, null, options);

            }

        }
    }
}