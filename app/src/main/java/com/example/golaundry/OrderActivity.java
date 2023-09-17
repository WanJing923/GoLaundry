package com.example.golaundry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OrderActivity extends AppCompatActivity {

    AppCompatButton btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        btn_next = findViewById(R.id.order_btn_next);

        btn_next.setOnClickListener(view -> {
            Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}