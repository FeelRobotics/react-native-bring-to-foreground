package com.feelrobotics.bringtoforeground;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.PowerManager;
import android.view.WindowManager;

import static android.content.Context.POWER_SERVICE;

public class BringToForegroundModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public BringToForegroundModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "BringToForeground";
    }

    private void launch(String param) {
        final String packageName = reactContext.getPackageName();
        Intent dialogIntent = getReactApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);

        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Bundle b = new Bundle();
        b.putString("param", param);
        dialogIntent.putExtras(b);
        getReactApplicationContext().startActivity(dialogIntent);
    }

    @ReactMethod
    public void open(String param) {
        final String packageName = reactContext.getPackageName();

        PowerManager.WakeLock screenLock = ((PowerManager) getReactApplicationContext()
                .getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "LAUNCHAPP:");
        screenLock.acquire();

        screenLock.release();
        final KeyguardManager km = (KeyguardManager) getReactApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);

        if (km.isDeviceLocked()) {
            new android.os.Handler().postDelayed(
              new Runnable() {
                  public void run() {
                    final Activity activity = getCurrentActivity();
                    km.requestDismissKeyguard(activity, null);
                  }
              },
            1000);
        } else {
            launch(param);
        }
    }

    @ReactMethod
    public void getLaunchParameters(final Promise promise) {
        final Activity activity = getCurrentActivity();
        final Intent intent = activity.getIntent();
        Bundle b = intent.getExtras();
        String value = "";
        if(b != null)
            value = b.getString("param", "");
        promise.resolve(value);
    }

    @ReactMethod
    public void clearLaunchParameters() {
        final Activity activity = getCurrentActivity();
        final Intent intent = activity.getIntent();
        Bundle b = new Bundle();
        intent.putExtras(b);
    }
}
