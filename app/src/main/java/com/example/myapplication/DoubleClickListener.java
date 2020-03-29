package com.example.myapplication;

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import java.util.concurrent.ExecutionException;

public abstract class DoubleClickListener implements View.OnClickListener {
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private boolean isSingleEvent;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    private Handler handler;
    private Runnable runnable;

    public DoubleClickListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isSingleEvent) {
                    onSingleClick();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            isSingleEvent = false;
            handler.removeCallbacks(runnable);
            try {
                onDoubleClick();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        isSingleEvent = true;
        handler.postDelayed(runnable, DEFAULT_QUALIFICATION_SPAN);
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick() throws ExecutionException, InterruptedException;
    public abstract void onSingleClick();
}