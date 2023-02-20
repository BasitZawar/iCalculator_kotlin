package com.cyber.tarzan.calculator.admob;


public class InterstitialClosedListenerImplementer {
    static InterstitialClosedListener closedListener;

    public static void setOnInterstitialClosedMaster(InterstitialClosedListener mClosedListenerMaster) {
        closedListener = mClosedListenerMaster;
    }

    public static void onInterstitialClosedCalled() {
        closedListener.onInterstitialClosed();
    }

    public static void onInterstitialFailedToShowCalled() {
        closedListener.onInterstitialFailedToShow();
    }


}

