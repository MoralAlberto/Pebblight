package com.albertomoral.pebblight.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.util.UUID;


public class MainActivity extends Activity {

    private final static UUID PEBBLE_APP_UUID = UUID.fromString("b9152c98-067a-44e2-8ad2-9fb548d6c480");

    ImageView image;
    LightReceiver lightReceiver;

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lightReceiver!=null)
            unregisterReceiver(lightReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.buttonFlashlight);
        image = (ImageView) findViewById(R.id.background);


        final Context context = this;

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                LightService.toggleLight(context);
            }
        });

        // Setup Service broadcast listener
        lightReceiver = new LightReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LightService.ACTION_LIGHT_STATUS);
        registerReceiver(lightReceiver, intentFilter);

        // Launching my app
        PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);

    }

    private class LightReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("lightOn", false)){
                image.setImageResource(R.drawable.ic_fondo_on);
            } else {
                image.setImageResource(R.drawable.ic_fondo_off);
            }
        }

    }
}