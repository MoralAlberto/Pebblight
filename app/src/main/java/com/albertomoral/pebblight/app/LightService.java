package com.albertomoral.pebblight.app;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;

/**
 * This intent service handles the turning on and off of the torch
 */
public class LightService extends Service {

    static boolean lightOn = false;
    private Camera camera;
    private Camera.Parameters camParams;

    public static final String ACTION_LIGHT = "com.albertomoral.pebblight.app.action.LIGHT";
    public static final String ACTION_LIGHT_STATUS = "com.albertomoral.pebblight.app.status";

    public static void toggleLight(Context context) {
        Intent intent = new Intent(context, LightService.class);
        intent.setAction(ACTION_LIGHT);
        context.startService(intent);
    }

    public LightService() {
        camera = Camera.open();
        camParams = camera.getParameters();
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

            if (camParams.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {

                Log.i("LightService", "torch is turn off!");

                //boolean connected = PebbleKit.isWatchConnected(this.getApplicationContext());
                //Log.e("LightService", "Pebble is " + (connected ? "connected" : "not connected"));

                camParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(camParams);
                camera.stopPreview();
                lightOn = false;

                notifyActivity(false);

            } else {

                Log.i("LightService", "torch is turn on!");

                camParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                camera.setParameters(camParams);
                camera.startPreview();
                lightOn = true;

                notifyActivity(true);

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

        if (!lightOn || camera != null) {
            camera.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
