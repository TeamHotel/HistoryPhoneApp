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
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by Harry Graham on 05/02/2017.
 */
public class NearbyFragment extends Fragment {
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_nearby, container, false);
        TextView textView = (TextView) view.findViewById(R.id.rowTextView);

        ListView lv =  (ListView) view.findViewById(R.id.list);
        String[] strings = {"Harry", "Sam"};
        arrayList = new ArrayList<>(Arrays.asList(strings));
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, strings);
        lv.setAdapter(arrayAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                startActivity(intent);
            }
        });

        return view;
    }

}
