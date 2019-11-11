package com.example.myapplication;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

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
    private Button button;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION}, 2);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        handler.sendEmptyMessage(0);


        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
                    Log.e("beaconManager","stopRangingBeacons");
                } catch (RemoteException e) {
                    Log.e("beaconManager","" + e);
                    e.printStackTrace();
                }
                Beacon beacon = new Beacon.Builder()
                        .setId1("fda50693-a4e2-4fb1-afcf-c6eb07647825")
                        .setId2("1")
                        .setId3("2")
                        .setManufacturer(0x0118)
                        .setTxPower(-59)
                        .setDataFields(Arrays.asList(new Long[] {0l}))
                        .build();
                BeaconParser beaconParser = new BeaconParser()
                        .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                beaconTransmitter.startAdvertising(beacon);

            }
        });

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                beaconList.clear();
                beaconList = (List)beacons;
            }
        });
    }

    Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void handleMessage(Message msg) {
               for(Beacon beacon : beaconList){
                   if(beaconList.size() == 0){
                       textView.setText("");
                   }else {
                       Log.e("beacon", "TypeCode" + beacon.getBeaconTypeCode() + " " + beacon.getManufacturer());
                       String beaconinfo = "Name:" + beacon.getBluetoothName() + "\nRSSI:" + beacon.getRssi() + "\nUUID:" + beacon.getId1() + " \nMajor:" + beacon.getId2() +
                               "\nMinor:" + beacon.getId3() + " \nDistance:" + String.format("%.3f", beacon.getDistance());
                       textView.setText(beaconinfo);
                   }
               }
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };
}



