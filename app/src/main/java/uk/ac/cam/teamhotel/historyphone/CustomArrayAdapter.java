package uk.ac.cam.teamhotel.historyphone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

//Custom ArrayAdapter implementation to allow dynamic update of list item contents
public class CustomArrayAdapter extends ArrayAdapter<Artifact> {
    public CustomArrayAdapter(Context context, int resource, List<Artifact> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Artifact artifact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView artifact_title = (TextView) convertView.findViewById(R.id.artifact_title);
        TextView artifact_description = (TextView) convertView.findViewById(R.id.artifact_description);
        TextView artifact_distance = (TextView) convertView.findViewById(R.id.artifact_location);
        ImageView artifact_image = (ImageView) convertView.findViewById(R.id.artifact_image);

        // Populate the data into the template view using the data object
        artifact_title.setText(artifact.name);
        artifact_description.setText(artifact.description);

        //loading artifact image
        byte[] outImage= getBitmapAsByteArray(artifact.picture);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        artifact_image.setImageBitmap(theImage);

        artifact_distance.setText(Float.toString(artifact.distance) + "m" );

        // Return the completed view to render on screen
        return convertView;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
