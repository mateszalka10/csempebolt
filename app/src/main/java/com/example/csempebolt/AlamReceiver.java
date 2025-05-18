package com.example.csempebolt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NotificationHandler(context).send("Itt a legjobb alkalom Csempét venni!");
    }
}