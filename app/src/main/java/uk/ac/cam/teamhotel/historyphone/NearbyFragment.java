package uk.ac.cam.teamhotel.historyphone;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: Add database integration (Harry is currently looking into this)
//TODO: Add methods for interacting with bluetooth beacons and pulling the Artifact meta-data dynamically

public class NearbyFragment extends Fragment {

    public static List<Artifact> artifacts = new ArrayList<Artifact>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate a new view with the nearby fragment layout.
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        //just for aesthetic example, for now
        artifacts.clear();
       artifacts.add(new Artifact("Harry","Chat to Harry", 5, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        artifacts.add(new Artifact("Sam","Chat to Sam", 2, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));

        //order object list by distance/location
        Collections.sort(artifacts, Artifact.SORT_BY_DIST);

        // Populate the list view with objects.
        ListView listView = (ListView) view.findViewById(R.id.nearby_list);
        listView.setAdapter(new CustomArrayAdapter(
                getActivity(),
                R.id.nearby_list,
                artifacts
            )
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                // TODO: Bundle item id with intent to multiplex chats.

                startActivity(intent);
            }
        });

        return view;
    }
}
