package com.cyber.tarzan.calculator.admob_new_implementation;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class AdTimerClass {
    static int counter = 0;
    static boolean isFirstTimeClicked = true;
    static TimerTask myTimer;

    public static boolean isEligibleForAd() {

        if (isFirstTimeClicked) {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //your method
                    myTimer = this;
                    if (counter >= 1) {
                        cancelTimer();
                    } else {
                        isFirstTimeClicked = false;
                    }
                    counter++;
                    Log.d("Ads_", ": Counter second: " + counter);
                }
            }, 0, 1000);
        }

        if (isFirstTimeClicked) {
            return true;
        }
        return false;
    }

    public static void cancelTimer() {
        counter = -1;
        isFirstTimeClicked = true;
        myTimer.cancel();
    }
}
