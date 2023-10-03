package com.example.golaundry;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.golaundry.adapter.RiderFindOrderAdapter;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.RiderFindOrderHolder;
import com.example.golaundry.viewModel.UserGetLocationHolder;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FindOrderFragment extends Fragment {
    private UserGetLocationHolder mUserGetLocationHolder;
    private String currentUserId, currentArea, fullAddress;
    private static final int REQUEST_CODE_MAP = 8;
    private TextView currentLocationTextView,noOrderTextView,notWorkingHourTextView;
    private DatabaseReference userOrderRef;
    private List<RiderFindOrderHolder> orderList;
    RiderFindOrderAdapter mRiderFindOrderAdapter;
    RecyclerView findOrderRecyclerView;

    public FindOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mUserGetLocationHolder = new ViewModelProvider(requireActivity()).get(UserGetLocationHolder.class);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userOrderRef = db.getReference().child("userOrder");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_order, container, false);
        currentLocationTextView = view.findViewById(R.id.ffo_tv_location);
        findOrderRecyclerView = view.findViewById(R.id.ffo_rv_order);
        noOrderTextView = view.findViewById(R.id.ffo_tv_no_order);
        notWorkingHourTextView = view.findViewById(R.id.ffo_tv_not_working_hour);

        if (currentArea == null) {
            getCurrentArea();
        } else {
            currentLocationTextView.setText(currentArea);
        }
        //get current location or choose another location
        currentLocationTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });
        if (!mUserGetLocationHolder.getIsGetCurrentLocation() && mUserGetLocationHolder.getFullAddress() == null) {
            getCurrentArea();
            mUserGetLocationHolder.setIsGetCurrentLocation(true);
        } else {
            fullAddress = mUserGetLocationHolder.getFullAddress();
            currentArea = mUserGetLocationHolder.getArea();
            currentLocationTextView.setText(currentArea);
        }

        orderList = new ArrayList<>();
        userOrderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel allOrder = orderSnapshot.getValue(OrderModel.class);
                        if (allOrder != null) {
                            String userAddress = allOrder.getAddressInfo().get("address");
                            LatLng userLatLng = getLocationFromAddress1(getContext(), userAddress);
                            LatLng riderLatLng = getLocationFromAddress1(getContext(), fullAddress);
                            double distance = 0;
                            if (riderLatLng != null && userLatLng != null) {
                                double dis = SphericalUtil.computeDistanceBetween(userLatLng, riderLatLng);
                                distance = dis / 1000;
                            }
                            RiderFindOrderHolder orderHolder = new RiderFindOrderHolder(allOrder, distance);
                            orderList.add(orderHolder);
                        }
                    }

                    orderList.sort((order1, order2) -> {
                        if (order1.getDistance() < order2.getDistance()) {
                            return -1;
                        } else if (order1.getDistance() > order2.getDistance()) {
                            return 1;
                        }
                        return 0;
                    });

                    if (orderList.size() != 0) {
                        //put into adapter
                        mRiderFindOrderAdapter = new RiderFindOrderAdapter(orderList,getContext());
                        findOrderRecyclerView.setAdapter(mRiderFindOrderAdapter);
                        findOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mRiderFindOrderAdapter.notifyDataSetChanged();

                        findOrderRecyclerView.setVisibility(View.VISIBLE);
                        noOrderTextView.setVisibility(View.GONE);
                        notWorkingHourTextView.setVisibility(View.GONE);
                    } else {
                        findOrderRecyclerView.setVisibility(View.GONE);
                        noOrderTextView.setVisibility(View.VISIBLE);
                        notWorkingHourTextView.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

    public LatLng getLocationFromAddress1(Context context, String theAddress) {

        if (theAddress == null || theAddress.isEmpty()) {
            return null;
        }
        if (!Geocoder.isPresent()) {
            return null;
        }
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(theAddress, 5);
            if (address == null || address.isEmpty()) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void getCurrentArea() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();

                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    assert addresses != null;
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        fullAddress = address.getAddressLine(0);
                        currentArea = address.getLocality();
                        currentLocationTextView.setText(currentArea);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentArea();
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_MAP && data != null) {
                String newArea = data.getStringExtra("area");
                fullAddress = data.getStringExtra("formattedAddress");
                currentLocationTextView.setText(newArea);

                if (!Objects.equals(currentArea, newArea)) {
                    currentArea = newArea;
                    currentLocationTextView.setText(currentArea);
                    mUserGetLocationHolder.setFullAddress(fullAddress);
                    mUserGetLocationHolder.setArea(currentArea);
                    updateOrderList();
                }
            }
        }
    }

    private void updateOrderList() {
        orderList.clear();
        userOrderRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        OrderModel allOrder = orderSnapshot.getValue(OrderModel.class);
                        if (allOrder != null) {
                            String userAddress = allOrder.getAddressInfo().get("address");
                            LatLng userLatLng = getLocationFromAddress1(getContext(), userAddress);
                            LatLng riderLatLng = getLocationFromAddress1(getContext(), fullAddress);
                            double distance = 0;
                            if (riderLatLng != null && userLatLng != null) {
                                double dis = SphericalUtil.computeDistanceBetween(userLatLng, riderLatLng);
                                distance = dis / 1000;
                            }
                            RiderFindOrderHolder orderHolder = new RiderFindOrderHolder(allOrder, distance);
                            orderList.add(orderHolder);
                        }
                    }

                    orderList.sort((order1, order2) -> {
                        if (order1.getDistance() < order2.getDistance()) {
                            return -1;
                        } else if (order1.getDistance() > order2.getDistance()) {
                            return 1;
                        }
                        return 0;
                    });

                    if (orderList.size() != 0) {
                        if (mRiderFindOrderAdapter == null) {
                            mRiderFindOrderAdapter = new RiderFindOrderAdapter(orderList, getContext());
                            findOrderRecyclerView.setAdapter(mRiderFindOrderAdapter);
                        }
                        mRiderFindOrderAdapter.notifyDataSetChanged();

                        findOrderRecyclerView.setVisibility(View.VISIBLE);
                        noOrderTextView.setVisibility(View.GONE);
                        notWorkingHourTextView.setVisibility(View.GONE);
                    } else {
                        findOrderRecyclerView.setVisibility(View.GONE);
                        noOrderTextView.setVisibility(View.VISIBLE);
                        notWorkingHourTextView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}