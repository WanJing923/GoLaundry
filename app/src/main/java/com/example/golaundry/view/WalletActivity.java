package com.example.golaundry.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golaundry.R;
import com.example.golaundry.model.CashOutModel;
import com.example.golaundry.viewModel.LaundryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WalletActivity extends AppCompatActivity {

    LaundryViewModel mLaundryViewModel;
    double balance;


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        mLaundryViewModel = new ViewModelProvider(this).get(LaundryViewModel.class);

        Toolbar toolbar = findViewById(R.id.aw_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        TextView balanceAmountTextView = findViewById(R.id.aw_tv_current_balance);
        EditText cashOutAmountEditText = findViewById(R.id.aw_et_amount);
        Button doneButton = findViewById(R.id.aw_btn_cash_out);
        ProgressBar mProgressBar = findViewById(R.id.aw_progressbar);

        mLaundryViewModel.getLaundryData(currentUserId).observe(WalletActivity.this, laundry -> {
            if (laundry != null) {
                //show user balance
                balance = laundry.getBalance();
                @SuppressLint("DefaultLocale") String showBalance = String.format("%.2f", laundry.getBalance());
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
                    mLaundryViewModel.cashOutBalance(currentUserId, mCashOutModel, newBalance).observe(WalletActivity.this, cashOutStatus -> {
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
                    Toast.makeText(WalletActivity.this, "Cash out amount cannot larger than current balance!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }

            } else {
                mProgressBar.setVisibility(View.GONE);
                cashOutAmountEditText.setError("Amount should be more than RM5");
                cashOutAmountEditText.requestFocus();
            }
        });

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