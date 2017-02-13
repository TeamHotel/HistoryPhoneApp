package uk.ac.cam.teamhotel.historyphone.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;

// TODO: Add database integration (Harry is currently looking into this).
// TODO: Add methods for interacting with bluetooth beacons and pulling the Artifact meta-data dynamically.

public class NearbyFragment extends Fragment {

    public static List<Artifact> artifacts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate a new view with the nearby fragment layout.
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        // Just for aesthetic example, for now.
        artifacts.clear();
        artifacts.add(new Artifact("Harry", "Chat to Harry", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        artifacts.add(new Artifact("Sam", "Chat to Sam", BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));

        // Populate the list view with objects.
        final ListView listView = (ListView) view.findViewById(R.id.nearby_list);
        listView.setAdapter(new ArtifactAdapter(getActivity(), artifacts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //pass artifact name to chat session
                Artifact currentArtifact = (Artifact) listView.getAdapter().getItem(position);
                intent.putExtra("ARTIFACT_TITLE", currentArtifact.getName());
                // TODO: Bundle item id with intent to multiplex chats.

                startActivity(intent);
            }
        });

        return view;
    }
}
