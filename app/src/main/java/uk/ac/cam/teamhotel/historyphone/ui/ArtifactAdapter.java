package uk.ac.cam.teamhotel.historyphone.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;

public class ArtifactAdapter extends ArrayAdapter<Artifact> {

    public ArtifactAdapter(Context context, List<Artifact> artifacts) {
        super(context, R.layout.list_item, artifacts);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // Get the data item for this position.
        Artifact artifact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view.
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Lookup view for data population.
        TextView title = (TextView) view.findViewById(R.id.artifact_title);
        TextView description = (TextView) view.findViewById(R.id.artifact_description);
        TextView distance = (TextView) view.findViewById(R.id.artifact_location);
        ImageView image = (ImageView) view.findViewById(R.id.artifact_image);

        // Populate the data into the template view using the data object.
        if (artifact == null) {
            // TODO: Display empty "loading" tile.
        } else {
            // Set parameters of artifact tile.
            title.setText(artifact.getName());
            description.setText(artifact.getDescription());

            // Format artifact image.
            byte[] outImage = getBitmapAsByteArray(artifact.getPicture());
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            image.setImageBitmap(theImage);

            // TODO: Set distance from data pushed in parallel with artifact.
        }

        // Return the completed view to render on screen.
        return view;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
