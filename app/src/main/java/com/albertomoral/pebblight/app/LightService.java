package com.albertomoral.pebblight.app;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * This intent service handles the turning on and off of the torch
 */
public class LightService extends Service {

    static boolean haveCamera = false;
    private Camera camera;
    private Camera.Parameters camParams;
    PowerManager.WakeLock wl;

    public static final String ACTION_LIGHT = "com.albertomoral.pebblight.app.action.LIGHT";
    public static final String ACTION_LIGHT_STATUS = "com.albertomoral.pebblight.app.status";

    public static void toggleLight(Context context) {
        Intent intent = new Intent(context, LightService.class);
        intent.setAction(ACTION_LIGHT);
        context.startService(intent);
    }

    public LightService() {
    }

    @Override
    public void onCreate(){
        // activate wake lock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wl.acquire(5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent != null && intent.getAction().equals(ACTION_LIGHT)) {

            // if device support camera?
            PackageManager pm = getApplicationContext().getPackageManager();
            if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Log.e("err", "Device has no camera!");
                return START_NOT_STICKY;
            }

            if (!haveCamera) {
                try {
                    camera = Camera.open();
                    camParams = camera.getParameters();
                } catch (Exception e){
                    return START_NOT_STICKY;
                }
            }

            if (camParams.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {

                Log.i("LightService", "turning torch off!");

                notifyActivity(false);

                //boolean connected = PebbleKit.isWatchConnected(this.getApplicationContext());
                //Log.e("LightService", "Pebble is " + (connected ? "connected" : "not connected"));

                if (camera != null) {

                    camParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(camParams);
                    camera.stopPreview();
                    // we need to release the camera here otherwise other application won't be able to use it.
                    camera.release();

                }

                haveCamera = false;


            } else {

                Log.i("LightService", "turning torch on!");

                notifyActivity(true);

                if (camera != null) {

                    camParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    camera.setParameters(camParams);
                    camera.startPreview();
                }

                haveCamera = true;

            }

        }

        return START_NOT_STICKY;
    }


    private void notifyActivity(boolean lightOn){
        Intent intent = new Intent();
        intent.setAction(ACTION_LIGHT_STATUS);
        intent.putExtra("lightOn", lightOn);

        sendBroadcast(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        wl.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
