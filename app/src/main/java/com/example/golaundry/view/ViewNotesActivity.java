package com.example.golaundry.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.golaundry.R;
import com.example.golaundry.model.OrderModel;

import java.util.Objects;

public class ViewNotesActivity extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        Toolbar toolbar = findViewById(R.id.vna_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));

        EditText laundryNoteEditText = findViewById(R.id.vna_et_laundry_note);
        EditText riderNoteEditText = findViewById(R.id.vna_et_rider_note);

        OrderModel mOrderModel = (OrderModel) getIntent().getSerializableExtra("HistoryOrderData");
        if (mOrderModel != null) {
            laundryNoteEditText.setText(mOrderModel.getNoteToLaundry());
            riderNoteEditText.setText(mOrderModel.getNoteToRider());
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}