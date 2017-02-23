package uk.ac.cam.teamhotel.historyphone.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.util.SortedList;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactCache;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

public class NearbyAdapter extends ArrayAdapter<Pair<Artifact, Float>> {

    public static final String TAG = "NearbyAdapter";

    private static final float THRESHOLD = 300.0f;

    private ArrayList<Pair<Artifact, Float>> contents;
    private HashMap<Long, Integer> positions;

    public NearbyAdapter(Activity activity, Observable<Pair<Artifact, Float>> entryStream) {
        this(activity, entryStream, new ArrayList<>());
    }

    private NearbyAdapter(Activity activity, Observable<Pair<Artifact, Float>> entryStream,
                          ArrayList<Pair<Artifact, Float>> contents) {

        super(activity, R.layout.list_item, contents);
        contents.add(new Pair<>(ArtifactCache.getInstance().get(0L), 200f));
        contents.add(new Pair<>(ArtifactCache.getInstance().get(123L), 300f));

        this.contents = contents;
        positions = new HashMap<>();

        if (entryStream == null) {
            Log.w(TAG, "Passed entry stream is null.");
            return;
        }

        entryStream.subscribe(entry -> {
            if (entry.second <= THRESHOLD) {
                activity.runOnUiThread(() -> insert(entry.first, entry.second));
            } else {
                activity.runOnUiThread(() -> remove(entry.first));
            }
        });
    }

    /**
     * If a pair keyed by the given Artifact object does not exists in
     * the data set, insert one with the given distance. Otherwise,
     * update the position of the pair according to the new distance.
     *
     * Must be called on the UI thread.
     *
     * @param artifact Artifact keying the concerned pair.
     * @param distance New distance for the pair.
     */
    public void insert(Artifact artifact, float distance) {
        if (positions.containsKey(artifact.getUUID())) {
            // Remove the entry from the contents.
            remove(artifact);
        }

        // Insert, maintaining the contents' order by distance.
        int position = -1 - Collections.binarySearch(contents, new Pair<>(null, distance),
                (left, right) -> {
                    int distanceComparison = left.second.compareTo(right.second);
                    // If another pair in the data set has the same distance,
                    // arbitrarily insert to the right.
                    return (distanceComparison != 0) ? distanceComparison : 1;
                });
        if (contents.size() > 0) {
            contents.add(contents.get(contents.size() - 1));
            for (int i = contents.size() - 1; i >= position + 1; i--) {
                contents.set(i + 1, contents.get(i));
            }
            contents.set(position, new Pair<>(artifact, distance));
        } else {
            contents.add(new Pair<>(artifact, distance));
        }
        positions.put(artifact.getUUID(), position);

        // Update the list view.
        notifyDataSetChanged();
    }

    /**
     * Remove the pair keyed by the given Artifact object from the data
     * set (if one such pair exists).
     *
     * Must be called on the UI thread.
     *
     * @param artifact Artifact to remove from the adapter.
     */
    public void remove(Artifact artifact) {
        if (positions.containsKey(artifact.getUUID())) {
            contents.remove(positions.get(artifact.getUUID()).intValue());
            positions.remove(artifact.getUUID());
        }

        // Update the list view.
        notifyDataSetChanged();
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

            //temporary fix for now
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
