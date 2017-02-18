package uk.ac.cam.teamhotel.historyphone.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import io.reactivex.Observable;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.ble.BeaconScanner;
import uk.ac.cam.teamhotel.historyphone.utils.ListTools;

// TODO: Add database integration (Harry is currently looking into this).
// TODO: Add methods for interacting with bluetooth beacons and pulling the Artifact meta-data dynamically.

public class NearbyFragment extends Fragment {

    public static final String TAG = "NearbyFragment";

    private boolean isCreated = false;
    private boolean loaderProvided = false;
    private ArtifactLoader artifactLoader = null;
    private Observable<ArrayList<Pair<Artifact, Float>>> entriesStream = null;

    private void createPipeline() {
        BeaconScanner scanner = BeaconScanner.getInstance();
        scanner.stop();

        // Sort each scan of beacons, increasing by distance, then unzip into two separate arrays.
        Observable<Pair<ArrayList<Long>, ArrayList<Float>>> listsStream =
                scanner.getBeaconStream()
                .map(beacons -> {
                    Collections.sort(
                            beacons,
                            (left, right) -> left.getDistance().compareTo(right.getDistance()));
                    return beacons;
                })
                .map(ListTools::unzipPairs);
        // Load artifacts from the UUIDs stream.
        Observable<ArrayList<Artifact>> artifactsStream =
                artifactLoader.loadScanStream(listsStream.map(ListTools::projLeft));
        Observable<ArrayList<Float>> distancesStream =
                listsStream.map(ListTools::projRight);
        // Zip the artifact and distances streams back together.
        entriesStream = Observable.zip(artifactsStream, distancesStream, ListTools::zipPairs);

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
            listView.setAdapter(new ArtifactAdapter(getActivity(), entriesStream));
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
                    ((ArtifactAdapter) listView.getAdapter()).getItem(position);
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
        // Set the adapter of the list view to track the entries stream.
        if (loaderProvided) {
            listView.setAdapter(new ArtifactAdapter(getActivity(), entriesStream));
        }

        isCreated = true;

        return rootView;
    }
}
