package com.albertomoral.pebblight.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.albertomoral.pebblight.app.PebbleKit.*;
import com.albertomoral.pebblight.app.util.PebbleDictionary;

import java.util.UUID;


public class MainActivity extends Activity {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("b9152c98-067a-44e2-8ad2-9fb548d6c480");

    //flag to detect flash is on or off
    private boolean isLighOn = false;
    private Camera camera;

    private Button button;
    ImageView image;

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.buttonFlashlight);
        image = (ImageView) findViewById(R.id.background);


        Context context = this;
        PackageManager pm = context.getPackageManager();

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }

        camera = Camera.open();
        final Parameters p = camera.getParameters();

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isLighOn) {

                    Log.i("info", "torch is turn off!");

                    boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
                    Log.e(getLocalClassName(), "Pebble is " + (connected ? "connected" : "not connected"));


                    p.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    camera.stopPreview();
                    isLighOn = false;


                    image.setImageResource(R.drawable.ic_fondo_off);


                } else {

                    Log.i("info", "torch is turn on!");

                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                    camera.setParameters(p);
                    camera.startPreview();
                    isLighOn = true;


                    image.setImageResource(R.drawable.ic_fondo_on);

                }

            }
        });

        // Launching my app
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);

        PebbleKit.registerPebbleConnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(getLocalClassName(), "Pebble connected!");
            }
        });

        PebbleKit.registerPebbleDisconnectedReceiver(getApplicationContext(), new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(getLocalClassName(), "Pebble disconnected!");
            }
        });

        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                //Log.i(getLocalClassName(), "Received value=" + data.getUnsignedInteger(0) + " for key: 0");
                if (isLighOn) {

                    Log.i("info", "torch is turn off!");

                    boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
                    Log.e(getLocalClassName(), "Pebble is " + (connected ? "connected" : "not connected"));


                    p.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    camera.stopPreview();
                    isLighOn = false;

                    image.setImageResource(R.drawable.ic_fondo_off);


                } else {

                    Log.i("info", "torch is turn on!");

                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                    camera.setParameters(p);
                    camera.startPreview();
                    isLighOn = true;

                    image.setImageResource(R.drawable.ic_fondo_on);

                }

                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
            }
        });

    }
}