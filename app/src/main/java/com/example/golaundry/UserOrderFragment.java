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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.adapter.ServiceAdapter;
import com.example.golaundry.adapter.ShowServiceAdapter;
import com.example.golaundry.adapter.UserOrderShowLaundryAdapter;
import com.example.golaundry.model.CombineLaundryData;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.LaundryServiceModel;
import com.example.golaundry.model.OrderModel;
import com.example.golaundry.model.RateModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.UserGetLocationHolder;
import com.example.golaundry.viewModel.UserViewModel;
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
import java.util.Map;
import java.util.Objects;

public class UserOrderFragment extends Fragment {

    UserGetLocationHolder mUserGetLocationHolder;
    String currentUserId, currentArea, fullAddress;
    LaundryViewModel mLaundryViewModel;
    UserViewModel mUserViewModel;
    ArrayList<CombineLaundryData> laundryList;
    UserOrderShowLaundryAdapter mUserOrderShowLaundryAdapter;
    RecyclerView laundryRecyclerView;
    TextView discoverTextView, currentLocationTextView, noResultsTextView, filterTextView, allTextView, filterRatingsTextView, filterDistanceTextView,lastRecentTextView;
    CardView recentlyOrderCardView, filterCardView;
    boolean recentlyOrderVisible;
    static final int REQUEST_CODE_MAP = 7;
    int currentRatingsFilter = 0;
    int currentDistanceFilter = 0;
    SeekBar ratingsFilterSeekBar, distanceFilterSeekBar;
    String laundryIdLRO;
    OrderModel latestOrderData;

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
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "DefaultLocale"})
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
        filterCardView = view.findViewById(R.id.uof_cv_filter);
        filterCardView = view.findViewById(R.id.uof_cv_filter);
        ratingsFilterSeekBar = view.findViewById(R.id.uof_sb_rating);
        distanceFilterSeekBar = view.findViewById(R.id.uof_sb_distance);
        Button filterDoneButton = view.findViewById(R.id.uof_btn_filter);
        filterRatingsTextView = view.findViewById(R.id.uof_tv_filter_rating_number);
        filterDistanceTextView = view.findViewById(R.id.uof_tv_filter_distance_number);
        lastRecentTextView = view.findViewById(R.id.uof_tv_last_recently);

        TextView laundryNameLROTextView = view.findViewById(R.id.uof_tv_laundry_name);
        TextView ratingsLROTextView = view.findViewById(R.id.uof_tv_rating);
        RatingBar starLRORatingBar = view.findViewById(R.id.uof_rb_star);
        TextView servicesTextView = view.findViewById(R.id.uof_tv_service);
        Button repeatOrderButton = view.findViewById(R.id.uof_btn_repeat_order);

        mUserViewModel.getLatestOrder(currentUserId).observe(getViewLifecycleOwner(), latestOrder -> {
            if (latestOrder != null) {
                latestOrderData = latestOrder;
                lastRecentTextView.setVisibility(View.VISIBLE);
                recentlyOrderCardView.setVisibility(View.VISIBLE);
                laundryIdLRO = latestOrder.getLaundryId();

                setDiscoverTv();

                mLaundryViewModel.getLaundryData(laundryIdLRO).observe(getViewLifecycleOwner(), laundryLRO -> {
                    if (laundryLRO != null) {
                        String shopNameLRO = laundryLRO.getShopName();
                        laundryNameLROTextView.setText(shopNameLRO);
                        ratingsLROTextView.setText(String.format("%.2f", laundryLRO.getRatingsAverage()));
                        starLRORatingBar.setRating(laundryLRO.getRatingsAverage());
                    }
                });

                Map<String, Integer> selectedServicesLRO = latestOrder.getSelectedServices();
                if (selectedServicesLRO != null && !selectedServicesLRO.isEmpty()) {
                    StringBuilder servicesBuilder = new StringBuilder();
                    int entryCount = 0;
                    for (Map.Entry<String, Integer> entry : selectedServicesLRO.entrySet()) {
                        String serviceName = entry.getKey();
                        int serviceCount = entry.getValue();
                        servicesBuilder.append(serviceName).append(" (").append(serviceCount).append(")");
                        entryCount++;
                        if (entryCount < selectedServicesLRO.size()) {
                            servicesBuilder.append(", ");
                        }
                    }
                    servicesTextView.setText(servicesBuilder.toString());
                } else {
                    servicesTextView.setText("No services selected");
                }
            }
        });

        repeatOrderButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), OrderLocationActivity.class);
            intent.putExtra("latestOrderData", latestOrderData);
            startActivity(intent);
        });

        setDiscoverTv(); //show or not show latest order card

        if (currentArea == null) {
            if (mUserGetLocationHolder.getIsGetCurrentLocation()){
                currentArea = mUserGetLocationHolder.getArea();
            } else {
                getCurrentArea();
            }
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

        //initialize adapter
        laundryList = new ArrayList<>();
        mUserOrderShowLaundryAdapter = new UserOrderShowLaundryAdapter(laundryList, getContext(), fullAddress);
        laundryRecyclerView.setAdapter(mUserOrderShowLaundryAdapter);
        laundryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //get data and put into adapter
        mLaundryViewModel.getFilteredLaundryData().observe(getViewLifecycleOwner(), allLaundryData ->
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
                }));

        String newFullAddress = fullAddress;
        mUserOrderShowLaundryAdapter.updateFullAddress(newFullAddress);

        //search laundry shop name
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

        //filter laundry shop
        filterTextView.setOnClickListener(v -> filterCardView.setVisibility(View.VISIBLE));
        setupSeekBarListeners();
        filterDoneButton.setOnClickListener(v -> applyFilters(currentRatingsFilter, currentDistanceFilter));

        return view;
    }

    private void applyFilters(int ratingsFilter, int distanceFilter) {
        ArrayList<CombineLaundryData> filteredList = new ArrayList<>();
        boolean itemsFound = false;

        for (CombineLaundryData laundry : laundryList) {
            if (laundry.getLaundry().getRatingsAverage() >= ratingsFilter) {
            String laundryAddress = laundry.getLaundry().getAddress();
            LatLng laundryLatLng = mUserOrderShowLaundryAdapter.getLocationFromAddress(requireContext(), laundryAddress);
            LatLng userLatLng = mUserOrderShowLaundryAdapter.getLocationFromAddress(requireContext(), fullAddress);
            if (laundryLatLng != null && userLatLng != null) {
                double distance = SphericalUtil.computeDistanceBetween(laundryLatLng, userLatLng);
                if (distance <= distanceFilter * 1000) {
                    filteredList.add(laundry);
                    itemsFound = true;
                }
            }
            }
            if (itemsFound) {
                noResultsTextView.setVisibility(View.GONE);
            } else {
                noResultsTextView.setVisibility(View.VISIBLE);
            }
        }

        // update the filtered list
        mUserOrderShowLaundryAdapter.filterList(filteredList);
        filterCardView.setVisibility(View.GONE);
    }

    private void setupSeekBarListeners() {
        ratingsFilterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRatingsFilter = progress;
                filterRatingsTextView.setText(String.valueOf(currentRatingsFilter));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        distanceFilterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentDistanceFilter = progress;
                filterDistanceTextView.setText(currentDistanceFilter + "km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
                        mUserGetLocationHolder.setArea(currentArea);
                        mUserGetLocationHolder.setFullAddress(fullAddress);
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
            params2.addRule(RelativeLayout.BELOW, R.id.uof_cv_recently_order);
            params2.setMargins(25, 35, 0, 0);
        } else {
            params1.addRule(RelativeLayout.BELOW, R.id.uof_et_search_bar);
            params2.addRule(RelativeLayout.BELOW, R.id.uof_et_search_bar);
            params2.setMargins(25, 35, 0, 0);
        }

        discoverTextView.setLayoutParams(params1);
        discoverTextView.setLayoutParams(params2);
    }
}
