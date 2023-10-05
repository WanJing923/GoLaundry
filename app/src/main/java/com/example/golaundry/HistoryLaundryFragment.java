package com.example.golaundry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class HistoryLaundryFragment extends Fragment {

    public HistoryLaundryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_history_laundry, container, false);

        TabLayout historyLaundryTab = view.findViewById(R.id.hlf_tab);
        RecyclerView OrderLaundryRecyclerView = view.findViewById(R.id.hlf_rv_orders);

//        List<YourDataModel> initialData = getInitialData();
//        YourRecyclerViewAdapter adapter = new YourRecyclerViewAdapter(initialData);
//        recyclerView.setAdapter(adapter);

        historyLaundryTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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