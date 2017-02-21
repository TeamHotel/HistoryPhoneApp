package uk.ac.cam.teamhotel.historyphone.ui;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactCache;

public class RecentAdapter extends ArrayAdapter<Pair<Long, String>> {

    private List<Pair<Long, String>> artifactList = new ArrayList<Pair<Long, String>>();


    public RecentAdapter(Context context, List<Pair<Long, String>> objects) {
        super(context, R.layout.list_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Pair<Long, String>  entry = getItem(position);

        //TODO: error check this
        //get current artifact by using uuid to pull from cache
        Artifact currentArtifact = ArtifactCache.getInstance().get(entry.first);

        // Check if an existing view is being reused, otherwise inflate the view.
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Lookup view for data population.
        TextView titleView = (TextView) view.findViewById(R.id.artifact_title);
        TextView descriptionView = (TextView) view.findViewById(R.id.artifact_description);
        ImageView imageView = (ImageView) view.findViewById(R.id.artifact_image);

        // Populate the data into the template view using the artifact object.
        if (entry == null) {
            // TODO: test if this will ever arise.

        } else {
            // Set parameters of artifact tile.
            titleView.setText(currentArtifact.getName());
            descriptionView.setText(currentArtifact.getDescription());

            // Format artifact image.
            if (currentArtifact.getPicture() != null) {
                byte[] outImage = getBitmapAsByteArray(currentArtifact.getPicture());
                ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                Bitmap image = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(image);
            }
        }

        return view;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
