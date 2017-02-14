package uk.ac.cam.teamhotel.historyphone.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import io.reactivex.Observable;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.artifact.ArtifactLoader;
import uk.ac.cam.teamhotel.historyphone.ble.Beacon;
import uk.ac.cam.teamhotel.historyphone.ble.BeaconScanner;
import uk.ac.cam.teamhotel.historyphone.utils.StreamTools;

// TODO: Add database integration (Harry is currently looking into this).
// TODO: Add methods for interacting with bluetooth beacons and pulling the Artifact meta-data dynamically.

public class NearbyFragment extends Fragment {

    public static final String TAG = "NearbyFragment";

    public static List<Artifact> artifacts = new ArrayList<>();

    private boolean isCreated = false;
    private ArtifactLoader artifactLoader = null;
    private Observable<ArrayList<Pair<Artifact, Float>>> entriesStream = null;

    private void createPipeline() {
        BeaconScanner scanner = BeaconScanner.getInstance();
        // Sort each scan of beacons, increasing by distance.
        Observable<ArrayList<Beacon>> sortedStream = scanner.getBeaconStream()
                .map(beacons -> {
                    Collections.sort(
                            beacons,
                            (left, right) -> left.getDistance().compareTo(right.getDistance()));
                    return beacons;
                });
        // Unzip the beacon scans into two separate arrays and project them into separate streams.
        Observable<Pair<ArrayList<Long>, ArrayList<Float>>> listsStream =
                StreamTools.unzipPairs(sortedStream);
        Observable<ArrayList<Long>> uuidsStream =
                StreamTools.projLeft(listsStream);
        Observable<ArrayList<Float>> distancesStream =
                StreamTools.projRight(listsStream);
        // Load artifacts from the UUIDs stream.
        Observable<ArrayList<Artifact>> artifactStream =
                artifactLoader.loadScanStream(uuidsStream);
        // Zip the artifact and distances streams back together.
        entriesStream = StreamTools.zipPairs(artifactStream, distancesStream);

        if (!scanner.isScanning() /* TODO: && Bluetooth is enabled. */) {
            scanner.start();
        } // TODO: else prompt for Bluetooth.
    }

    public void setArtifactLoader(ArtifactLoader artifactLoader) {
        this.artifactLoader = artifactLoader;

        // If we have already created the view, set up the beacon scanning
        // pipeline immediately.
        if (isCreated) {
            createPipeline();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // If the artifact loader has already been provided, we may create the
        // beacon scanning pipeline immediately. Otherwise, it will be created
        // on calling setArtifactLoader.
        if (artifactLoader != null) {
            createPipeline();
        }

        // Inflate a new view with the nearby fragment layout.
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        // Just for aesthetic example, for now.
        artifacts.clear();
        artifacts.add(new Artifact("Harry", "Chat to Harry", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        artifacts.add(new Artifact("Sam", "Chat to Sam", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));

        // Populate the list view with objects.
        final ListView listView = (ListView) view.findViewById(R.id.nearby_list);
        listView.setAdapter(new ArtifactAdapter(getActivity(), artifacts));
        listView.setOnItemClickListener((parent, rowView, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);

            //pass artifact name to chat session
            Artifact currentArtifact = (Artifact) listView.getAdapter().getItem(position);
            intent.putExtra("ARTIFACT_TITLE", currentArtifact.getName());
            // TODO: Bundle UUID with intent to multiplex chats.

            startActivity(intent);
        });

        isCreated = true;

        return view;
    }
}
