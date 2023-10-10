package com.example.golaundry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM Message Data", "Message data payload: " + remoteMessage.getData());

            String message = remoteMessage.getData().get("message");

            showInAppMessage(message);
        }
    }

    private void showInAppMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Notification");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
