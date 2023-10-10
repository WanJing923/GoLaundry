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
import com.example.golaundry.adapter.HistoryLaundryFragmentAdapter;
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

public class HistoryLaundryFragment extends Fragment {

    String currentUserId;
    private DatabaseReference userOrderRef;
    private final List<OrderModel> toProcessList = new ArrayList<>();
    private final List<OrderModel> toConfirmList = new ArrayList<>();
    private final List<OrderModel> completeList = new ArrayList<>();
    private final List<OrderModel> cancelledList = new ArrayList<>();
    private HistoryLaundryFragmentAdapter toProcessAdapter, toConfirmAdapter, completeAdapter, cancelledAdapter;
    private RecyclerView OrderLaundryRecyclerView;

    public HistoryLaundryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userOrderRef = db.getReference().child("userOrder");
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_laundry, container, false);
        TabLayout historyLaundryTab = view.findViewById(R.id.hlf_tab);
        OrderLaundryRecyclerView = view.findViewById(R.id.hlf_rv_orders);

        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        toProcessAdapter = new HistoryLaundryFragmentAdapter(toProcessList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        toConfirmAdapter = new HistoryLaundryFragmentAdapter(toConfirmList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        completeAdapter = new HistoryLaundryFragmentAdapter(completeList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        cancelledAdapter = new HistoryLaundryFragmentAdapter(cancelledList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);

        OrderLaundryRecyclerView.setAdapter(toProcessAdapter);
        OrderLaundryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        historyLaundryTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getUserOrderDataForToProcess();
                        OrderLaundryRecyclerView.setAdapter(toProcessAdapter);
                        break;
                    case 1:
                        getUserOrderDataForToConfirm();
                        OrderLaundryRecyclerView.setAdapter(toConfirmAdapter);
                        break;
                    case 2:
                        getUserOrderDataForComplete();
                        OrderLaundryRecyclerView.setAdapter(completeAdapter);
                        break;
                    case 3:
                        getUserOrderDataForCancelled();
                        OrderLaundryRecyclerView.setAdapter(cancelledAdapter);
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

        getUserOrderDataForToProcess();
        return view;
    }

    private void getUserOrderDataForToProcess() {
        toProcessList.clear();
        userOrderRef.orderByChild("laundryId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order created".equals(currentStatus) || "Rider accept order".equals(currentStatus)
                                    || "Rider pick up".equals(currentStatus) || "Order reached laundry shop".equals(currentStatus)) {
                                toProcessList.add(order);
                            }
                        }
                    }
                    toProcessList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    toProcessAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserOrderDataForToConfirm() {
        toConfirmList.clear();
        userOrderRef.orderByChild("laundryId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            if (Objects.equals(order.getCurrentStatus(), "Laundry done process")
                                    || Objects.equals(order.getCurrentStatus(), "Order out of delivery")
                                    || Objects.equals(order.getCurrentStatus(), "Order delivered")) {
                                toConfirmList.add(order);
                            }
                        }
                    }
                    toConfirmList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    toConfirmAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserOrderDataForComplete() {
        completeList.clear();
        userOrderRef.orderByChild("laundryId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            if (Objects.equals(order.getCurrentStatus(), "Order completed")) {
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

    private void getUserOrderDataForCancelled() {
        cancelledList.clear();
        userOrderRef.orderByChild("laundryId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            if (Objects.equals(order.getCurrentStatus(), "Order cancelled by customer") || "Order cancelled by laundry shop".equals(order.getCurrentStatus())) {
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