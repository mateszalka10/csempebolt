package com.example.csempebolt;

import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {
    private WeakReference<TextView> mTextView;

    public AsyncTask(TextView textView) {
        mTextView = new WeakReference<>(textView);
    }

    @Override
    protected String doInBackground(Void... voids) {
        int ms = 5*300;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "anonim";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mTextView.get().setText(s);
    }
}
