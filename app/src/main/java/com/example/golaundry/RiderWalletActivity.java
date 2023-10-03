package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.model.CashOutModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.example.golaundry.viewModel.RiderViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RiderWalletActivity extends AppCompatActivity {

    RiderViewModel mRiderViewModel;
    double balance;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_wallet);
        mRiderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);

        Toolbar toolbar = findViewById(R.id.arw_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        TextView balanceAmountTextView = findViewById(R.id.arw_tv_current_balance);
        EditText cashOutAmountEditText = findViewById(R.id.arw_et_amount);
        Button doneButton = findViewById(R.id.arw_btn_cash_out);
        ProgressBar mProgressBar = findViewById(R.id.arw_progressbar);

        mRiderViewModel.getRiderData(currentUserId).observe(RiderWalletActivity.this, rider -> {
            if (rider != null) {
                //show user balance
                balance = rider.getBalance();
                @SuppressLint("DefaultLocale") String showBalance = String.format("%.2f", rider.getBalance());
                balanceAmountTextView.setText("Current balance: RM" + showBalance);
            }
        });

        doneButton.setOnClickListener(view -> {
            String amountString = cashOutAmountEditText.getText().toString();
            double amount = Double.parseDouble(amountString);

            if (amount >= 5.0 - 0.01) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateTime = sdf.format(new Date());
                CashOutModel mCashOutModel = new CashOutModel(dateTime, amount, balance);

                double newBalance = balance - amount;
                if (newBalance >= 0) {
                    mRiderViewModel.cashOutBalanceRider(currentUserId, mCashOutModel, newBalance).observe(RiderWalletActivity.this, cashOutStatus -> {
                        if (cashOutStatus) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Cash out successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Cash out failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(RiderWalletActivity.this, "Cash out amount cannot larger than current balance!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }

            } else {
                mProgressBar.setVisibility(View.GONE);
                cashOutAmountEditText.setError("Amount should be more than RM5");
                cashOutAmountEditText.requestFocus();
            }
        });


    }
}