package com.example.ashnabhatia.catchme2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

public class CreateGame extends Activity {
    private static final int TAKE_PICTURE = 1;
    private File outputFile;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);


        ((Button) findViewById(R.id.TakePicture)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                outputFile = PictureHandling.createImageFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));

                startActivityForResult(intent, TAKE_PICTURE);
            }
        });


        ((Button) findViewById(R.id.createGame)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                findViewById(R.id.spinner).setVisibility(View.VISIBLE);
                findViewById(R.id.createGame).setVisibility(View.GONE);


                (new PictureHandling.ResizeEncodeTask(outputFile.getAbsolutePath()) {
                    @Override
                    protected void onPostExecute(String picBase64) {
                        TextView text = (TextView) findViewById(R.id.create_game_text);
                        HttpApi.createGame(text.getText().toString(), picBase64, new HttpApi.ApiObjectListener() {
                            @Override
                            public void onDone(JSONObject result) {
                                Toast.makeText(CreateGame.this, "Game created!", Toast.LENGTH_SHORT);
                                finish();

                                findViewById(R.id.spinner).setVisibility(View.GONE);
                                findViewById(R.id.createGame).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception err) {
                                findViewById(R.id.spinner).setVisibility(View.GONE);
                                findViewById(R.id.createGame).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).execute();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath(), options);

            ((ImageView) findViewById(R.id.PicturePreview)).setImageBitmap(bitmap);

        }
    }
}