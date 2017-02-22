package uk.ac.cam.teamhotel.historyphone.ui;

import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
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

import static android.app.Activity.RESULT_OK;

public class NearbyFragment extends Fragment {

    public static final String TAG = "NearbyFragment";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private Observable<Pair<Artifact, Float>> entriesStream;

    /**
     * Construct the artifact entry pipeline from the raw beacon stream.
     */
    private void createPipeline() {
        // Stop scanning while the beacon pipeline is set up.
        stopScanning();

        // Compose the pipeline.
        Log.i(TAG, "Composing nearby beacon pipeline...");
        entriesStream = BeaconScanner.getInstance().getBeaconStream()
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

        // Begin scanning for beacons.
        startScanning();
    }

    /**
     * Start scanning for beacons via the beacon scanner. If Bluetooth is
     * not currently enabled, prompt the user to enable it.
     */
    private void startScanning() {
        BeaconScanner scanner = BeaconScanner.getInstance();
        try {
            if (scanner.isBluetoothEnabled()) {
                // If Bluetooth is currently enabled, start scanning.
                scanner.start();
            } else {
                // Otherwise, prompt the user to enable Bluetooth.
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
            }
        } catch (BluetoothNotSupportedException e) {
            // TODO: Indicate to user that Bluetooth is not supported.
        }
    }

    /**
     * Stop scanning for beacons via the beacon scanner.
     */
    private void stopScanning() {
        BeaconScanner.getInstance().stop();
    }

    @Override
    public void onActivityResult(int request, int result, Intent data) {
        switch (request) {
            case REQUEST_ENABLE_BLUETOOTH:
                // This is the result of prompting the user to enable Bluetooth.
                // Treat the result as their preference on whether we should be scanning.
                if (result == RESULT_OK) {
                    startScanning();
                } else {
                    stopScanning();
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
