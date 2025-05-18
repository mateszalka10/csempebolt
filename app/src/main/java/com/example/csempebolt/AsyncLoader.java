package com.example.csempebolt;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class AsyncLoader extends AsyncTaskLoader<String> {

    public AsyncLoader(@NonNull Context context) {
        super(context);
    }
    @NonNull
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        int ms = 15*300;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "guest";
    }
}
