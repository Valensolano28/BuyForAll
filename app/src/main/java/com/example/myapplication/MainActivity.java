package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String ACCESS_FINE_LOCATION = "2";
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    private TextView textView;
    private final int Int_Major = 10010;
    private final int Int_Minor = 54488;
    private final Region region = new Region("myRangingUniqueId", null, null, null);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION}, 2);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
            beaconManager.stopMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) { e.printStackTrace(); }
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) { e.printStackTrace(); }

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    beaconList = (List) beacons;

                    Log.e("beaconList", beacons.toString());
                    handler.sendEmptyMessage(0);
                } else
                    Log.e("No_beaconList", beacons.toString());
            }
        });
    }

    Handler find_beacon = new Handler() {
        public void handleMessage(Message msg) {
            try {
                beaconManager.stopRangingBeaconsInRegion(region);
                beaconManager.unbind(MainActivity.this);
            } catch (RemoteException e) { e.printStackTrace(); }
            Log.e("handle", "startActivity");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(intent);
            onStop();
        }
    };

        Handler handler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void handleMessage(Message msg) {
                for (Beacon beacon : beaconList) {
                    String NAME = "Name:" + beacon.getBluetoothName();
                    String RSSI = "\nRSSI:" + beacon.getRssi();
                    String UUID = "\nUUID:" + beacon.getId1();
                    String Major = "\nMajor:" + beacon.getId2();
                    String Minor = "\nMinor:" + beacon.getId3();
                    String Distance = "\nDistance:" + String.format("%.3f", beacon.getDistance()) + "m";
                    String temp_major = Major.split(":")[1];
                    String temp_minor = Minor.split(":")[1];
                    if (Int_Major == Integer.parseInt(temp_major) && Int_Minor == Integer.parseInt(temp_minor)) {
                        //Log.i("beacon", "TypeCode" + beacon.getBeaconTypeCode() + " " + beacon.getManufacturer());
                        String beaconinfo = NAME + RSSI + UUID + Major + Minor + Distance;
                        if (beacon.getDistance() <= 1) {
                            Log.e("beacon", "비콘 들어옴");
                            Toast.makeText(MainActivity.this, "비콘 들어옴(" + beacon.getBluetoothName() +" "+ String.format("%.3f", beacon.getDistance()) + "m)", Toast.LENGTH_SHORT).show();
                            beaconinfo += "\n비콘들어옴" + beacon.getBluetoothName();
                            find_beacon.sendEmptyMessage(1);
                        } else
                            Log.i("beacon","비콘 찾음(" + beacon.getBluetoothName() + ")");
                        textView.setText(beaconinfo);
                    }
                    beaconList.clear();
                }
               // handler.sendEmptyMessageDelayed(0, 1000);
            }
        };
}