package uk.ac.cam.teamhotel.historyphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;

public class NearbyFragment extends Fragment {

    public static String[] strings = { "Harry", "Sam" };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate a new view with the nearby fragment layout.
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        // Populate the list view with objects. TODO: actual objects.
        ListView listView = (ListView) view.findViewById(R.id.nearby_list);
        listView.setAdapter(new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item,
                R.id.artifact_title,
                strings)
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
