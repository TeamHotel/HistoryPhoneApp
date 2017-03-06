package uk.ac.cam.teamhotel.historyphone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.teamhotel.historyphone.HistoryPhoneApplication;
import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.database.DatabaseHelper;

public class RecentFragment extends Fragment{

    public static final String TAG = "RecentFragment";

    private List<Pair<Long, String>> conversationList = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        // Setup helper and load list of pairs from database, in order of time.
        databaseHelper = ((HistoryPhoneApplication) getActivity().getApplication())
                .getDatabaseHelper();

        listView = (ListView) rootView.findViewById(R.id.recent_list);
        listView.setAdapter(new RecentAdapter(getActivity(), conversationList));

        // Set up the click listener for the list view.
        listView.setOnItemClickListener((parent, rowView, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);

            // Pass artifact name and UUID to chat session.
            Pair<Long, String> entry =
                    ((RecentAdapter) listView.getAdapter()).getItem(position);

            assert entry != null;

            if (entry.first == null) {
                Log.d(TAG, "Null artifact.");
                return;
            }
            Log.d(TAG, entry.first.toString());

            // Tell the chat view that we want chatting disabled and also send the UUID.
            intent.putExtra("ENABLE_CHAT", false);
            intent.putExtra("UUID", entry.first);

            startActivity(intent);
        });

        loadConversationsFromDB();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        databaseHelper = ((HistoryPhoneApplication) ((Activity) context).getApplication())
                .getDatabaseHelper();
    }

    /**
     * Method overridden to load in additions from the DB, re-attach the list adapter
     * and then notify it of changes. This is called when the tab is reselected by the
     * user, allowing for dynamic refreshing.
     */
    @Override
    public void onResume() {
        super.onResume();

        loadConversationsFromDB();
    }

    /**
     * Method to pull in changes from the DB and load the conversationsList member variable.
     */
    private void loadConversationsFromDB() {
        if (databaseHelper == null) {
            Log.w(TAG, "Database helper is null.");
            return;
        }

        List<Pair<Long, String>> newConversationList = databaseHelper.returnAllConversations();
        conversationList.clear();
        conversationList.addAll(newConversationList);
        databaseHelper.printConversations();
        ((RecentAdapter) listView.getAdapter()).notifyDataSetChanged();
    }
}
