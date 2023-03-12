package com.cyber.tarzan.calculator.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.cyber.tarzan.calculator.R;


public class InfoUtil {
    Activity activity;

    public InfoUtil(Activity activity) {
        this.activity = activity;
    }


    public void supportUs() {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException activityNotFoundException) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void inviteFriends() {
        try {
            final String appPackageName = activity.getPackageName();
            String myUrl = "https://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, myUrl);
            sendIntent.setType("text/plain");
            activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share)));

        } catch (Exception e) {
            // Catch Exception here
        }
    }

    public void privacy() {
        try {
            String url = "https://www.cybertarzan.com/privacy";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);

        } catch (Exception e) {
            // Catch Exception here
        }
    }

    public void moreApps() {
        try {
            String url = "https://play.google.com/store/apps/collection/cluster?clp=igM4ChkKEzc1ODE1MjgzNTQ4NTg3OTU3ODMQCBgDEhkKEzc1ODE1MjgzNTQ4NTg3OTU3ODMQCBgDGAA%3D:S:ANO1ljLGUc8&gsr=CjuKAzgKGQoTNzU4MTUyODM1NDg1ODc5NTc4MxAIGAMSGQoTNzU4MTUyODM1NDg1ODc5NTc4MxAIGAMYAA%3D%3D:S:ANO1ljKmN6o";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);

        } catch (Exception e) {
            // Catch Exception here
        }
    }

    public void checkUpdate() {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException activityNotFoundException) {
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (Exception e) {
                // Catch Exception here
            }
        }
    }

    public void shareImage(String url) {
        try {
            final String appPackageName = activity.getPackageName();
            String myUrl = "https://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, myUrl + "\n" + url);
            sendIntent.setType("text/plain");
            activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share)));

        } catch (Exception e) {
            // Catch Exception here
        }
    }
}
