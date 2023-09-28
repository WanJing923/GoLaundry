package com.example.golaundry;

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

import com.example.golaundry.model.CurrentMembershipModel;
import com.example.golaundry.model.TopUpModel;
import com.example.golaundry.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TopUpActivity extends AppCompatActivity {

    UserViewModel mUserViewModel;
    double balance,monthlyTopUp;
    String membershipRate,monthYear,newMembershipRate;


    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        Toolbar toolbar = findViewById(R.id.toa_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon((getResources().getDrawable(R.drawable.ic_toolbar_back)));

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        TextView balanceAmountTextView = findViewById(R.id.toa_tv_current_balance);
        EditText topUpAmountEditText = findViewById(R.id.toa_et_amount);
        Button doneButton = findViewById(R.id.toa_btn_top_up);
        ProgressBar mProgressBar = findViewById(R.id.toa_progressbar);

        mUserViewModel.getUserData(currentUserId).observe(TopUpActivity.this, user -> {
            if (user != null) {
                //show user balance
                membershipRate = user.getMembershipRate();
                balance = user.getBalance();
                String showBalance = String.format("%.2f", user.getBalance());
                balanceAmountTextView.setText(showBalance);
            }
        });

        mUserViewModel.getCurrentMembership(currentUserId).observe(TopUpActivity.this, membershipModel -> {
            if (membershipModel != null) {
                monthlyTopUp = membershipModel.getMonthlyTopUp();
                monthYear = membershipModel.getMonthYear();


            }
        });

        doneButton.setOnClickListener(view -> {
            double amount = topUpAmountEditText.getText().length();

            if (amount >= 5){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateTime = sdf.format(new Date());
                TopUpModel mTopUpModel = new TopUpModel(dateTime,amount,balance);

                String currentMonthYear = getCurrentMonthYear();

                if (Objects.equals(currentMonthYear, monthYear)){ //if is in this month
                    double currentMonthlyTopUp = monthlyTopUp+amount;
                    //update current membership table
                    mUserViewModel.updateMonthlyTopUp(currentUserId,currentMonthlyTopUp).observe(TopUpActivity.this, updateStatus -> {
                        if (updateStatus) {

                            //check whether membership history got this month or not
                            mUserViewModel.getMembershipHistory(currentUserId,currentMonthYear).observe(TopUpActivity.this, checkStatus -> {
                                if (checkStatus) {

                                    //update membership history
                                    mUserViewModel.updateMembershipHistory(currentUserId,currentMonthYear,currentMonthlyTopUp).observe(TopUpActivity.this, updateMembershipHistoryStatus -> {
                                        if (updateMembershipHistoryStatus) {

                                            //update user table balance and current membership rate
                                            double newBalance = balance+currentMonthlyTopUp;
                                            if (newBalance > 0 && newBalance <= 5){
                                                newMembershipRate = "GL05";
                                            } else if (newBalance >= 6 && newBalance <= 10){
                                                newMembershipRate = "GL05";
                                            }


                                            mUserViewModel.updateUserMembershipBalance(currentUserId,newBalance,newMembershipRate).observe(TopUpActivity.this, topUpStatus -> {
                                                if (topUpStatus) {
                                                    mProgressBar.setVisibility(View.GONE);
                                                    Toast.makeText(this, "Top up successful", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    mProgressBar.setVisibility(View.GONE);
                                                    Toast.makeText(this, "Top up failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        } else {
                                            mProgressBar.setVisibility(View.GONE);
                                            Toast.makeText(this, "Update membership history failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    //if don't have this month, add new

                                }
                            });



                            //update membership history table
                            updateMembershipHistory(currentUserId,currentMonthYear,currentMonthlyTopUp);
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Update monthly top up amount failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    //if is a new month
                    CurrentMembershipModel currentMembershipModel = new CurrentMembershipModel(currentMonthYear,amount);
                    mUserViewModel.updateCurrentMembership(currentUserId,currentMembershipModel).observe(TopUpActivity.this, updateStatus -> {
                        if (updateStatus) {
                            //add membership history
                            updateMembershipHistory(currentUserId,currentMonthYear,amount);




                            mUserViewModel.topUpBalance(currentUserId,mTopUpModel).observe(TopUpActivity.this, topUpStatus -> {
                                if (topUpStatus) {
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Top up successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Top up failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Update current membership failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                //update user table
                //update user membership history

            } else {
                mProgressBar.setVisibility(View.GONE);
                balanceAmountTextView.setError("Amount should be more than RM5");
                balanceAmountTextView.requestFocus();
            }
        });




    }

    private void updateMembershipHistory(String currentUserId, String currentMonthYear, double monthlyTopUp){

    }

    public String getCurrentMonthYear() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
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