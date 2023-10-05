package com.example.golaundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;


public class HistoryFragment extends Fragment {

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        TabLayout historyUserTab = view.findViewById(R.id.hf_tab);
        RecyclerView OrderUserRecyclerView = view.findViewById(R.id.hf_rv_orders);

//        List<YourDataModel> initialData = getInitialData();
//        YourRecyclerViewAdapter adapter = new YourRecyclerViewAdapter(initialData);
//        recyclerView.setAdapter(adapter);

        historyUserTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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