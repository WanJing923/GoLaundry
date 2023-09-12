package com.example.golaundry;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserOrderFragment extends Fragment {

    TextView discoverTextView, currentLocationTextView;
    CardView recentlyOrderCardView;
    boolean recentlyOrderVisible;
    private static final int REQUEST_CODE_MAP = 7;
    String currentArea;

    public UserOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDiscoverTv();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_order, container, false);

        discoverTextView = view.findViewById(R.id.uof_tv_discover);
        recentlyOrderCardView = view.findViewById(R.id.uof_cv_recently_order);
        EditText searchBarEditText = view.findViewById(R.id.uof_et_search_bar);
        currentLocationTextView = view.findViewById(R.id.uof_tv_current_address);

        currentLocationTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_MAP && data != null) {
                //get return intent
                currentArea = data.getStringExtra("area");
                currentLocationTextView.setText(currentArea);
            }
        }
    }



    private void setDiscoverTv() {
        recentlyOrderVisible = (recentlyOrderCardView.getVisibility() == View.VISIBLE);
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) currentLocationTextView.getLayoutParams();
        if (recentlyOrderVisible) {
            params1.addRule(RelativeLayout.BELOW, R.id.uof_cv_recently_order);
        } else {
            params1.addRule(RelativeLayout.BELOW, R.id.uof_et_search_bar);
        }
        discoverTextView.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) discoverTextView.getLayoutParams();
        if (recentlyOrderVisible) {
            params2.addRule(RelativeLayout.BELOW, R.id.uof_cv_recently_order);
        } else {
            params2.addRule(RelativeLayout.BELOW, R.id.uof_et_search_bar);
        }
        discoverTextView.setLayoutParams(params2);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // First clear current all the menu items
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