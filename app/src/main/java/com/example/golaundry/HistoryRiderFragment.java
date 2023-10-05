package com.example.golaundry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HistoryRiderFragment extends Fragment {
    private DatabaseReference userOrderRef;

    public HistoryRiderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userOrderRef = db.getReference().child("userOrder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_history_rider, container, false);

        TabLayout historyRiderTab = view.findViewById(R.id.hrf_tab);
        RecyclerView OrderRiderRecyclerView = view.findViewById(R.id.hrf_rv_orders);

//        List<YourDataModel> initialData = getInitialData();
//        YourRecyclerViewAdapter adapter = new YourRecyclerViewAdapter(initialData);
//        recyclerView.setAdapter(adapter);

        historyRiderTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Update RecyclerView data based on the selected tab
//                String selectedTabText = tab.getText().toString();
//                List<YourDataModel> newData = getNewDataForTab(selectedTabText);
//                adapter.updateData(newData);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }


}