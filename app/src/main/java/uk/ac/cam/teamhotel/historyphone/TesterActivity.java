package uk.ac.cam.teamhotel.historyphone;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

import uk.ac.cam.teamhotel.historyphone.server.MetaQuery;

public class TesterActivity extends AppCompatActivity {
    private static final String TAG = "TesterActivity";
    private ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Made new activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        im = (ImageView) findViewById(R.id.im);
        try {
            Bitmap bm = new DownloadAsyncTask().execute().get(); //TODO: use asynctask to load image.
            im.setImageBitmap(bm);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException i) {
            i.printStackTrace();
        }
    }

    private class DownloadAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            return MetaQuery.getImage(2L);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

}
