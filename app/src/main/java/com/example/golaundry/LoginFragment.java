package com.example.golaundry;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.golaundry.viewModel.UserViewModel;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private UserViewModel mUserViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String param1 = getArguments().getString(ARG_PARAM1);
            String param2 = getArguments().getString(ARG_PARAM2);
        }

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText emailAddressEditText = view.findViewById(R.id.fl_et_email);
        EditText passwordEditText = view.findViewById(R.id.fl_et_enter_password);
        CheckBox rmbMeCheckBox = view.findViewById(R.id.fl_checkBox);

        loginPreferences = requireActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            emailAddressEditText.setText(loginPreferences.getString("email", ""));
            passwordEditText.setText(loginPreferences.getString("password", ""));
            rmbMeCheckBox.setChecked(true);
        }

        view.findViewById(R.id.fl_tv_needhelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpCenterActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.fl_tv_forgetpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.fl_btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.fl_progressbar);
                // show the visibility of progress bar to show loading
                mProgressBar.setVisibility(View.VISIBLE);

                // Take the value of two edit texts in Strings
                String email = emailAddressEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (rmbMeCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

                // validations for input email and password
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Please enter email!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Please enter password!!", Toast.LENGTH_LONG).show();
                    return;
                }

                //vm create user
                mUserViewModel.loginUser(email, password)
                        .observe(requireActivity(), signInResult -> {
                            if (signInResult != null && signInResult) {
                                // User login success
                                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);

                                //intent to home
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);

                            } else {
                                // User login failed
                                Toast.makeText(getActivity(), "Login failed!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });


            }
        });


    }


}