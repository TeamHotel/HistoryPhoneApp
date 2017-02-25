package uk.ac.cam.teamhotel.historyphone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

public class RecentFragment extends Fragment{

    public static final String TAG = "RecentFragment";
    private List<Pair<Long, String>> conversationList = new ArrayList<Pair<Long, String>>();
    private DatabaseHelper dbHelper;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_recent, container, false);

        //setup helper and load list of pairs from database, in order of time
        dbHelper = new DatabaseHelper(this.getActivity());
        loadConversationsFromDB();

        listView = (ListView) rootView.findViewById(R.id.recent_list);
        listView.setAdapter(new RecentAdapter(getActivity(), conversationList));

        // Set up the click listener for the list view.
        listView.setOnItemClickListener((parent, rowView, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);

            // Pass artifact name and UUID to chat session.
            Pair<Long, String> entry =
                    ((RecentAdapter) listView.getAdapter()).getItem(position);

            Log.d(TAG, entry.first.toString());

            assert entry != null;
            if (entry.first == null) {
                Log.d(TAG, "NULL ARTIFACT");
                return;
            }
            Log.d(TAG, entry.first.toString());
            //intent.putExtra("ARTIFACT_TITLE", entry.first.getName());
            intent.putExtra("UUID", entry.first);

            startActivity(intent);

        });

        return rootView;

    }

    /**
     * Method overridden to load in additions from the DB, re-attach the list adapter and then notify it of changes.
     * This is called when the Tab is reselected by the user, allowing for dynamic refreshing.
     */
    @Override
    public void onResume() {

        super.onResume();
        loadConversationsFromDB();
        listView.setAdapter(new RecentAdapter(getActivity(), conversationList));
        ((RecentAdapter)listView.getAdapter()).notifyDataSetChanged();

    }

    /**
     * Method to pull in changes from the DB and load the conversationsList member variable.
     */
    private void loadConversationsFromDB(){
           conversationList = dbHelper.returnAllConversations();
    }
}
