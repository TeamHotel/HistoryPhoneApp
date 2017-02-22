package uk.ac.cam.teamhotel.historyphone.ui;

import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import io.reactivex.Observable;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.ble.BeaconScanner;
import uk.ac.cam.teamhotel.historyphone.ble.BluetoothNotSupportedException;
import uk.ac.cam.teamhotel.historyphone.utils.StreamTools;

// TODO: Add database integration (Harry is currently looking into this).

public class NearbyFragment extends Fragment {

    public static final String TAG = "NearbyFragment";

    private boolean isCreated = false;
    private boolean loaderProvided = false;
    private ArtifactLoader artifactLoader = null;
    private Observable<Pair<Artifact, Float>> entriesStream = null;

    private void createPipeline() {
        BeaconScanner scanner;
        try {
            scanner = BeaconScanner.getInstance();
        } catch (BluetoothNotSupportedException e) {
            e.printStackTrace();
            return;
        }

        // Stop scanning while the beacon pipeline is set up.
        scanner.stop();

        // Compose the pipeline.
        Log.i(TAG, "Composing nearby beacon pipeline...");
        entriesStream = scanner.getBeaconStream()
                // Load Artifact objects from beacon UUIDs.
                .compose(StreamTools.mapLeft(ArtifactLoader::load))
                // Group the pairs by their beacon UUID.
                .groupBy(pair -> pair.first.getUUID())
                // Send a timeout to remove stale entries from the list.
                .map(stream -> {
                    // Wrap in an array to get around Java's weird closure semantics.
                    final Artifact[] group = new Artifact[] { null };
                    return stream
                            // Store the artifact this stream is grouped by.
                            .doOnNext(pair -> group[0] = pair.first)
                            // After a timeout without seeing this artifact, throw an error.
                            .timeout(4000, TimeUnit.MILLISECONDS)
                            // When an error is streamed, forward the group artifact at +inf metres.
                            .onErrorReturn(error -> new Pair<>(
                                    group[0],
                                    Float.POSITIVE_INFINITY
                            ));
                })
                // Recombine the streams into one.
                .flatMap(stream -> stream);

        // TODO: If Bluetooth is enabled...
        scanner.start();
        // TODO: ...else prompt for Bluetooth.
    }

    public void setArtifactLoader(ArtifactLoader artifactLoader) {
        this.artifactLoader = artifactLoader;
        loaderProvided = true;

        // If we have already created the view, set up the beacon scanning
        // pipeline immediately.
        if (isCreated) {
            createPipeline();

            // Set the adapter of the list view to track the entries stream.
            View rootView = getView();
            assert rootView != null;
            ListView listView = (ListView) rootView.findViewById(R.id.nearby_list);
            listView.setAdapter(new NearbyAdapter(getActivity(), entriesStream));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // If the artifact loader has already been provided, we may create the
        // beacon scanning pipeline immediately. Otherwise, it will be created
        // on calling setArtifactLoader.
        if (loaderProvided) {
            createPipeline();
        }

        // Inflate a new view with the nearby fragment layout.
        View rootView = inflater.inflate(R.layout.fragment_nearby, container, false);

        // Set up the click listener for the list view.
        final ListView listView = (ListView) rootView.findViewById(R.id.nearby_list);
        listView.setOnItemClickListener((parent, rowView, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);

            // Pass artifact name and UUID to chat session.
            Pair<Artifact, Float> entry =
                    ((NearbyAdapter) listView.getAdapter()).getItem(position);
            assert entry != null;
            if (entry.first == null) {
                Log.d(TAG, "NULL ARTIFACT");
                return;
            }
            Log.d(TAG, entry.first.toString());
            intent.putExtra("ARTIFACT_TITLE", entry.first.getName());
            intent.putExtra("UUID", entry.first.getUUID());

            startActivity(intent);
        });
        // Create the beacon scanning pipeline and set the list
        // adapter to track the entries stream.
        createPipeline();
        listView.setAdapter(new NearbyAdapter(getActivity(), entriesStream));

        return rootView;
    }
}
