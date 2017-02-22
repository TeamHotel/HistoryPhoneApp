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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

public class ArtifactAdapter extends ArrayAdapter<Pair<Artifact, Float>> {

    public static final String TAG = "ArtifactAdapter";

    public ArtifactAdapter(Activity activity,
                           Observable<ArrayList<Pair<Artifact, Float>>> entriesStream) {
        super(activity, R.layout.list_item, new LinkedList<>());

        entriesStream.subscribe(entries -> {
            activity.runOnUiThread(() -> {
                clear();
                addAll(entries);
            });
        });
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // Get the data item for this position.
        Pair<Artifact, Float> entry = getItem(position);
        if (entry == null) {
            Log.e(TAG, "Artifact list contains null entry.");
            return view;
        }
        Artifact artifact = entry.first;
        float distance = entry.second;

        // Check if an existing view is being reused, otherwise inflate the view.
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Lookup view for data population.
        TextView titleView = (TextView) view.findViewById(R.id.artifact_title);
        TextView descriptionView = (TextView) view.findViewById(R.id.artifact_description);
        TextView distanceView = (TextView) view.findViewById(R.id.artifact_location);
        ImageView imageView = (ImageView) view.findViewById(R.id.artifact_image);

        // Populate the data into the template view using the artifact object.
        if (artifact == null) {
            // TODO: Display flashier "loading" tile.
            titleView.setText("Loading...");
            descriptionView.setText("");
        } else {



            // Set parameters of artifact tile.
            titleView.setText(artifact.getName());
            descriptionView.setText(artifact.getDescription());

            // Format artifact image.
            if (artifact.getPicture() != null) {
                byte[] outImage = getBitmapAsByteArray(artifact.getPicture());
                ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                Bitmap image = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(image);
            }else{
                imageView.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_launcher));
                artifact.setPicture(BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_launcher));
            }

            DatabaseHelper dbhelper = new DatabaseHelper(this.getContext());
            dbhelper.addArtifact(artifact);

        }
        // TODO: Reformat as resource string.
        distanceView.setText(String.valueOf(distance) + "m");

        // Return the completed view to render on screen.
        return view;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
