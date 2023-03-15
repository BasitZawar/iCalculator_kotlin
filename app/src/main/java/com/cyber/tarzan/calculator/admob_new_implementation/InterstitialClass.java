package com.cyber.tarzan.calculator.admob_new_implementation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialClass {
    static InterstitialAd mInterstitialAd;
    static Activity mActivity;
    static String mInterstitialID;
    static String logTag = "Ads_";
    static ActionOnAdClosedListener mActionOnAdClosedListener;
    static boolean isAdDecided = false;
    static int DELAY_TIME = 0;

    static boolean stopInterstitial = false;
    static boolean timerCalled = false;

    public static void request_interstitial(Activity activity, String interstitial_id, ActionOnAdClosedListener actionOnAdClosedListenersm) {
        mActivity = activity;
        mInterstitialID = interstitial_id;
        mActionOnAdClosedListener = actionOnAdClosedListenersm;
        isAdDecided = false;

        if (AdTimerClass.isEligibleForAd()) {
            load_interstitial();
        } else {
            performAction();
        }

    }

    public static void load_interstitial() {
        if (mInterstitialAd == null) {
            showAdDialog();
            stopInterstitial = false;
            timerCalled = false;
            AdRequest adRequest_interstitial = new AdRequest.Builder().build();
            InterstitialAd.load(mActivity, mInterstitialID, adRequest_interstitial,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            isAdDecided = true;
                            Log.d(logTag, "Insterstitial Loaded.");

                            if (!timerCalled) {
                                closeAdDialog();
                                show_interstitial();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d(logTag, "Interstitial Failed to Load." + loadAdError.getMessage());
                            mInterstitialAd = null;
                            isAdDecided = true;
                            if (!timerCalled) {
                                closeAdDialog();
                                performAction();
                            }

                        }
                    });
            timerAdDecided();
        } else {

            Log.d(logTag, "Ad was already loaded.: ");
            stopInterstitial = false;
            showAdDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeAdDialog();
                    show_interstitial();
                }
            }, 5000);
        }


    }

    static void showAdDialog() {

            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setTitle("Please Wait.");
            progressDialog.setMessage("Please wait Full Screen Ad is expected to show.");
            progressDialog.setCancelable(false);
            progressDialog.create();
            progressDialog.show();

    }

    static void closeAdDialog() {
        if (progressDialog.isShowing() || progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    static void timerAdDecided() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAdDecided) {
                    stopInterstitial = true;
                    timerCalled = true;
                    Log.d(logTag, "Handler Cancel.");
                    AdTimerClass.cancelTimer();
                    closeAdDialog();
                    show_interstitial();
                }
            }
        }, 5000);
    }

    static ProgressDialog progressDialog;
    static AlertDialog alertDialog;


    static void show_interstitial() {
        if (mInterstitialAd != null && stopInterstitial == false) {
            mInterstitialAd.show(mActivity);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.d(logTag, "Insterstitial Failed to Show.");
                    mInterstitialAd = null;
                    closeAdDialog();
                    performAction();
                    isInterstitialShowing = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    isInterstitialShowing = true;
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.d(logTag, "Insterstitial Shown.");
                    mInterstitialAd = null;
                    closeAdDialog();
                    performAction();
                    isInterstitialShowing = false;
                }
            });
        } else {
            performAction();
        }
    }

    public static boolean isInterstitialShowing = false;

    static void performAction() {
        mActionOnAdClosedListener.ActionAfterAd();
    }
}
