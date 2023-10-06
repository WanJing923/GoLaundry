package com.example.golaundry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.adapter.HistoryFragmentAdapter;
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


public class HistoryFragment extends Fragment {

    String currentUserId;
    private DatabaseReference userOrderRef;
    private final List<OrderModel> toCollectList = new ArrayList<>();
    private final List<OrderModel> toReceiveList = new ArrayList<>();
    private final List<OrderModel> completeList = new ArrayList<>();
    private final List<OrderModel> cancelledList = new ArrayList<>();
    private HistoryFragmentAdapter toCollectAdapter, completeAdapter, toReceiveAdapter, cancelledAdapter;
    private RecyclerView OrderUserRecyclerView;

    public HistoryFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        TabLayout historyUserTab = view.findViewById(R.id.hf_tab);
        OrderUserRecyclerView = view.findViewById(R.id.hf_rv_orders);

        LaundryViewModel mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);
        UserViewModel mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        RiderViewModel mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        toCollectAdapter = new HistoryFragmentAdapter(toCollectList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        toReceiveAdapter = new HistoryFragmentAdapter(toReceiveList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        completeAdapter = new HistoryFragmentAdapter(completeList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);
        cancelledAdapter = new HistoryFragmentAdapter(cancelledList, getContext(), mLaundryViewModel, mUserViewModel, mRiderViewModel);

        OrderUserRecyclerView.setAdapter(toCollectAdapter);
        OrderUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        historyUserTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getUserOrderDataForToCollect();
                        OrderUserRecyclerView.setAdapter(toCollectAdapter);
                        break;
                    case 1:
                        getUserOrderDataForToReceive();
                        OrderUserRecyclerView.setAdapter(toReceiveAdapter);
                        break;
                    case 2:
                        getUserOrderDataForComplete();
                        OrderUserRecyclerView.setAdapter(completeAdapter);
                        break;
                    case 3:
                        getUserOrderDataForCancelled();
                        OrderUserRecyclerView.setAdapter(cancelledAdapter);
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

        getUserOrderDataForToCollect();

        return view;
    }

    private void getUserOrderDataForToCollect() {
        toCollectList.clear();
        userOrderRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order created".equals(currentStatus) || "Rider accept order".equals(currentStatus)) {
                                toCollectList.add(order);
                            }
                        }
                    }
                    toCollectList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    toCollectAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserOrderDataForToReceive() {
        toReceiveList.clear();
        userOrderRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
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
                                toReceiveList.add(order);
                            }
                        }
                    }
                    toReceiveList.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
                    toReceiveAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserOrderDataForComplete() {
        completeList.clear();
        userOrderRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
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

    private void getUserOrderDataForCancelled() {
        cancelledList.clear();
        userOrderRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel order = orderSnapshot.getValue(OrderModel.class);
                        if (order != null) {
                            String currentStatus = order.getCurrentStatus();
                            if ("Order cancelled".equals(currentStatus)) {
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