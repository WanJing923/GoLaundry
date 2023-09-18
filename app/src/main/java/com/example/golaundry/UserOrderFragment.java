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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.adapter.ServiceAdapter;
import com.example.golaundry.adapter.ShowServiceAdapter;
import com.example.golaundry.adapter.UserOrderShowLaundryAdapter;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.UserGetLocationHolder;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UserOrderFragment extends Fragment {

    UserGetLocationHolder mUserGetLocationHolder;
    String currentUserId;
    LaundryViewModel mLaundryViewModel;
    ArrayList<CombineLaundryData> laundryList;
    UserOrderShowLaundryAdapter mUserOrderShowLaundryAdapter;
    RecyclerView laundryRecyclerView;
    TextView discoverTextView, currentLocationTextView;
    CardView recentlyOrderCardView;
    boolean recentlyOrderVisible;
    private static final int REQUEST_CODE_MAP = 7;
    String currentArea, fullAddress;
    TextView noResultsTextView, filterTextView, allTextView;

    public UserOrderFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mLaundryViewModel = new ViewModelProvider(requireActivity()).get(LaundryViewModel.class);
        mUserGetLocationHolder = new ViewModelProvider(requireActivity()).get(UserGetLocationHolder.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_order, container, false);

        discoverTextView = view.findViewById(R.id.uof_tv_discover);
        recentlyOrderCardView = view.findViewById(R.id.uof_cv_recently_order);
        currentLocationTextView = view.findViewById(R.id.uof_tv_current_address);
        laundryRecyclerView = view.findViewById(R.id.uof_rv_laundry);
        noResultsTextView = view.findViewById(R.id.uof_tv_no_result);
        filterTextView = view.findViewById(R.id.uof_tv_filter);
        allTextView = view.findViewById(R.id.uof_tv_all);
        setDiscoverTv();

        currentLocationTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });

        if (currentArea == null) {
            getCurrentArea();
        } else {
            currentLocationTextView.setText(currentArea);
        }

        if (!mUserGetLocationHolder.getIsGetCurrentLocation() && mUserGetLocationHolder.getFullAddress() == null) {
            getCurrentArea();
            mUserGetLocationHolder.setIsGetCurrentLocation(true);
        } else {
            fullAddress = mUserGetLocationHolder.getFullAddress();
            currentArea = mUserGetLocationHolder.getArea();
            currentLocationTextView.setText(currentArea);
        }

        //initialize
        laundryList = new ArrayList<>();
        mUserOrderShowLaundryAdapter = new UserOrderShowLaundryAdapter(laundryList, getContext(), fullAddress);
        laundryRecyclerView.setAdapter(mUserOrderShowLaundryAdapter);
        laundryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //adapter
        mLaundryViewModel.getAllLaundryData().observe(getViewLifecycleOwner(), allLaundryData -> {
            mLaundryViewModel.getAllShopData().observe(getViewLifecycleOwner(), allShopData -> {
                if (allLaundryData != null && allShopData != null) {
                    LiveData<List<CombineLaundryData>> combinedLiveData = mLaundryViewModel.combineAndNotifyData(allLaundryData, allShopData);

                    combinedLiveData.observe(getViewLifecycleOwner(), combinedDataList -> {
                        if (combinedDataList != null) {
                            laundryList.clear();
                            laundryList.addAll(combinedDataList);
                            mUserOrderShowLaundryAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        });

        String newFullAddress = fullAddress;
        mUserOrderShowLaundryAdapter.updateFullAddress(newFullAddress);

        EditText searchBarEditText = view.findViewById(R.id.uof_et_search_bar);
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchLaundryList(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });



        return view;
    }

    private void searchLaundryList(String query) {
        ArrayList<CombineLaundryData> filteredList = new ArrayList<>();
        for (CombineLaundryData laundry : laundryList) {
            if (laundry.getLaundry().getShopName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(laundry);
            }
        }
        if (filteredList.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE);
            laundryRecyclerView.setVisibility(View.GONE);
            discoverTextView.setVisibility(View.GONE);
            currentLocationTextView.setVisibility(View.GONE);
            filterTextView.setVisibility(View.GONE);
            allTextView.setVisibility(View.GONE);
        } else {
            noResultsTextView.setVisibility(View.GONE);
            laundryRecyclerView.setVisibility(View.VISIBLE);
            discoverTextView.setVisibility(View.VISIBLE);
            currentLocationTextView.setVisibility(View.VISIBLE);
            filterTextView.setVisibility(View.VISIBLE);
            allTextView.setVisibility(View.VISIBLE);
            mUserOrderShowLaundryAdapter.filterList(filteredList);
        }
    }

    private void getCurrentArea() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    if (mUserOrderShowLaundryAdapter != null) {
                        mUserOrderShowLaundryAdapter.updateFullAddress(fullAddress);
                    }
                    mUserGetLocationHolder.setFullAddress(fullAddress);
                    mUserGetLocationHolder.setArea(currentArea);
                }
            }
        }
    }

    private void setDiscoverTv() {
        recentlyOrderVisible = (recentlyOrderCardView.getVisibility() == View.VISIBLE);
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) currentLocationTextView.getLayoutParams();
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) discoverTextView.getLayoutParams();

        if (recentlyOrderVisible) {
            params1.addRule(RelativeLayout.BELOW, R.id.uof_cv_recently_order);
            params2.setMargins(25, 35, 0, 0);
        } else {
            params1.addRule(RelativeLayout.BELOW, R.id.uof_et_search_bar);
            params2.setMargins(25, 35, 0, 0);
        }

        discoverTextView.setLayoutParams(params1);
        discoverTextView.setLayoutParams(params2);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_top, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tm_btn_notification) {
            //intent notification
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
