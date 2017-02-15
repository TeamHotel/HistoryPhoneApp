package uk.ac.cam.teamhotel.historyphone;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.server.MetaQuery;

public class TesterActivity extends AppCompatActivity {
    private static final String TAG = "TesterActivity";
    private ImageView im;
    private TextView name;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Made new activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        im = (ImageView) findViewById(R.id.im);
        name = (TextView) findViewById(R.id.name);
        description = (TextView) findViewById(R.id.description);

        try {
            //Use Asynctask to download artifact image and metadata on a different thread - android crashes if this is done on main thread.
            //UUID 123 is a sample bot - will change dynamically in actual program
            Artifact artifact = new DownloadAsyncTask().execute(123L).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException i) {
            i.printStackTrace();
        }
    }

    private class DownloadAsyncTask extends AsyncTask<Long, Void, Artifact> {

        @Override
        protected Artifact doInBackground(Long... params) {
            //invoke static method to download artifact with uuid 123.
            return MetaQuery.getArtifact(params[0]);
        }

        @Override
        protected void onPostExecute(Artifact artifact) {
            //set image/description/name after the artifact has been downloaded and created.
            im.setImageBitmap(artifact.getPicture());
            name.append(artifact.getName());
            description.append(artifact.getDescription());

            super.onPostExecute(artifact);
        }
    }

}
