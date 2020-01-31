package com.feelrobotics.bringtoforeground;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

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
        KeyguardManager km = (KeyguardManager) getReactApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        // final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
        // kl.disableKeyguard();

        if (km.isDeviceLocked()) {
            mKeyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
                @Override
                public void onDismissSucceeded() {
                    super.onDismissSucceeded();
                    launch(param);
                }
            });
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
