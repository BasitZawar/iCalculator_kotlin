package com.cyber.tarzan.calculator.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdMobInterstitial {

    public static InterstitialAd mInterstitialAd;
    public static boolean isAlreadyLoaded = false;
    static Context mContext;
    static String interstitial_id;
    static String logTag = "inter_";

    public static void loadInterstitialAd(Context your_activity_context, String your_interstitial_id) {

        mContext = your_activity_context;
        interstitial_id = your_interstitial_id;

        if (!isAlreadyLoaded) {

            Log.d(logTag, "Interstitial Load Request Sent.");
            AdRequest adRequest_interstitial = new AdRequest.Builder().build();

            InterstitialAd.load(mContext, your_interstitial_id, adRequest_interstitial,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.d(logTag, "Insterstitial Loaded.");
                            isAlreadyLoaded = true;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d(logTag, "Interstitial Failed to Load." + loadAdError.getMessage());
                            mInterstitialAd = null;
                            isAlreadyLoaded = false;
                        }
                    });
        } else {
            Log.d(logTag, "Interstitial Already Loaded. Request not Sent.");
        }

    }


    public static void showInterstitial(Activity yourActivity, boolean isBackPressed) {

        if (isAlreadyLoaded) {
            mInterstitialAd.show(yourActivity);
            isAlreadyLoaded = false;

            Log.d(logTag, "Interstitial Shown.");
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    InterstitialClosedListenerImplementer.onInterstitialClosedCalled();

                    if (!isBackPressed)
                        loadInterstitialAd(mContext, AdIds.INSTANCE.admobInterstitialIdHome());
                    Log.d(logTag, "Interstitial Closed.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    InterstitialClosedListenerImplementer.onInterstitialFailedToShowCalled();
                    Log.d(logTag, "Interstitial Closed.");

                }
            });
        } else {
            Log.d(logTag, "Interstitial was not Loaded.");
            isAlreadyLoaded = false;
            if (!isBackPressed)
                loadInterstitialAd(mContext, AdIds.INSTANCE.admobInterstitialIdHome());
        }
    }


}
