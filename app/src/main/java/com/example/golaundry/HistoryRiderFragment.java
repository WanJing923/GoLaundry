package com.example.golaundry;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.golaundry.adapter.HistoryFragmentAdapter;
import com.example.golaundry.adapter.HistoryRiderFragmentAdapter;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistoryRiderFragment extends Fragment {
    String currentUserId;
    private DatabaseReference userOrderRef;
    private final List<OrderModel> toPickUpList = new ArrayList<>();
    private final List<OrderModel> toDeliverList = new ArrayList<>();
    private final List<OrderModel> completeList = new ArrayList<>();
    private final List<OrderModel> cancelledList = new ArrayList<>();
    private HistoryRiderFragmentAdapter toPickUpAdapter, toDeliverAdapter, completeAdapter, cancelledAdapter;
    private RecyclerView OrderRiderRecyclerView;

    public HistoryRiderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userOrderRef = db.getReference().child("userOrder");
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_rider, container, false);

        TabLayout historyRiderTab = view.findViewById(R.id.hrf_tab);
        OrderRiderRecyclerView = view.findViewById(R.id.hrf_rv_orders);

        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        toPickUpAdapter = new HistoryRiderFragmentAdapter(toPickUpList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        toDeliverAdapter = new HistoryRiderFragmentAdapter(toDeliverList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        completeAdapter = new HistoryRiderFragmentAdapter(completeList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        cancelledAdapter = new HistoryRiderFragmentAdapter(cancelledList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);

        OrderRiderRecyclerView.setAdapter(toPickUpAdapter);
        OrderRiderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        toPickUpAdapter.notifyDataSetChanged();

        historyRiderTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getRiderOrderDataForToPickUp();
                        OrderRiderRecyclerView.setAdapter(toPickUpAdapter);
                        toPickUpAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        getRiderOrderDataForToDeliver();
                        OrderRiderRecyclerView.setAdapter(toDeliverAdapter);
                        toDeliverAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        getRiderOrderDataForComplete();
                        OrderRiderRecyclerView.setAdapter(completeAdapter);
                        completeAdapter.notifyDataSetChanged();
                        break;
                    case 3:
                        getRiderOrderDataForCancelled();
                        OrderRiderRecyclerView.setAdapter(cancelledAdapter);
                        cancelledAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        getRiderOrderDataForToPickUp();
        return view;
    }

    private void getRiderOrderDataForToPickUp() {
        toPickUpList.clear();
        userOrderRef.orderByChild("riderId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Rider accept order".equals(currentStatus)) {
                                toPickUpList.add(order);
                            }
                        }
                    }
                    toPickUpList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    toPickUpAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getRiderOrderDataForToDeliver() {
        toDeliverList.clear();
        userOrderRef.orderByChild("riderId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Rider pick up".equals(currentStatus) || "Order reached laundry shop".equals(currentStatus)
                                    || "Laundry done process".equals(currentStatus) || "Order out of delivery".equals(currentStatus)
                                    || "Order delivered".equals(currentStatus)) {
                                toDeliverList.add(order);
                            }
                        }
                    }
                    toDeliverList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    toDeliverAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getRiderOrderDataForComplete() {
        completeList.clear();
        userOrderRef.orderByChild("riderId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order completed".equals(currentStatus)) {
                                completeList.add(order);
                            }
                        }
                    }
                    completeList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    completeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getRiderOrderDataForCancelled() {
        cancelledList.clear();
        userOrderRef.orderByChild("riderId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order cancelled by customer".equals(currentStatus) || "Order cancelled by laundry shop".equals(currentStatus)) {
                                cancelledList.add(order);
                            }
                        }
                    }
                    cancelledList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    cancelledAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}