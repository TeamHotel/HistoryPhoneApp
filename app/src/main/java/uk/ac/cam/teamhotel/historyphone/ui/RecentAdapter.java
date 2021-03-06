package uk.ac.cam.teamhotel.historyphone.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.ac.cam.teamhotel.historyphone.HistoryPhoneApplication;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.utils.StoreBitmapUtility;
import uk.ac.cam.teamhotel.historyphone.utils.TimeStampHelper;

public class RecentAdapter extends ArrayAdapter<Pair<Long, String>> {

    public static final String TAG = "RecentAdapter";

    public RecentAdapter(Context context, List<Pair<Long, String>> objects) {
        super(context, R.layout.list_item_nearby, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Pair<Long, String> entry = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view.
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_recent, parent, false);
        }

        // No entries should be null.
        if (entry == null) {
            Log.e(TAG, "Null entry found in recents list.");
            return view;
        }

        // Get the current artifact by using the uuid to pull from cache.
        Activity activity = (Activity) getContext();
        ArtifactLoader artifactLoader =
                ((HistoryPhoneApplication) activity.getApplication()).getArtifactLoader();
        Artifact artifact = artifactLoader.load(entry.first);

        // Lookup view for data population.
        TextView titleView = (TextView) view.findViewById(R.id.artifact_title);
        TextView descriptionView = (TextView) view.findViewById(R.id.artifact_description);
        ImageView imageView = (ImageView) view.findViewById(R.id.artifact_image);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);

        // Populate the data into the template view using the artifact object.
        titleView.setText(artifact.getName());
        descriptionView.setText(artifact.getDescription());
        timestamp.setText(TimeStampHelper.formatTimeStamp(entry.second));

        // Format artifact image.
        Bitmap image = StoreBitmapUtility.loadImageFromStorage(artifact.getUUID(),
                getContext().getApplicationContext());
        if(image != null) {
            imageView.setImageBitmap(image);
        } else {
            // Otherwise, fall back on the loading icon.
            imageView.setImageBitmap(BitmapFactory.decodeResource(view.getResources(),
                    R.drawable.ic_loading));
        }

        return view;
    }
}
