package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String ACCESS_FINE_LOCATION = "2";
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    private TextView textView;
    private final int Int_Major1 = 10010;
    private final int Int_Minor1 = 54488;
    private final int Int_Major2 = 10004;
    private final int Int_Minor2 = 54480;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private Button onbutton;
    private Button offbutton;
    private List<String> list = new ArrayList();
    private final Region region = new Region("myRangingUniqueId", null, null, null);
    private String g_ad = "google 광고";
    private String d_ad = "daum 광고";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);
        onbutton = findViewById(R.id.onbutton);
        offbutton = findViewById(R.id.offbutton);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 2);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        onbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconManager.bind(MainActivity.this);
            }
        });
        offbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) { e.printStackTrace(); }
                beaconManager.unbind(MainActivity.this);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = list.get(position);
                if(temp.equals(g_ad)){
                    dialog(temp);
                }else if(temp.equals(d_ad)){
                    dialog(temp);
                }else{
                    dialog(temp);
                }
            }
        });
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
                    list.clear();
                    handler.sendEmptyMessage(0);
                } else
                    Log.e("No_beaconList", beacons.toString());
            }
        });
    }

    public void dialog(String msg) {
        String g_url = "http://www.google.com";
        String d_url = "http://www.daum.net";
        String n_url = "http://www.naver.com";
        final String url;
        if(msg.equals(g_ad)){
           url = g_url;
        }else if(msg.equals(d_ad))
            url = d_url;
        else
            url = n_url;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(msg);
        builder.setMessage(msg + " 연결?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

        Handler handler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void handleMessage(Message msg) {
                String beaconinfo;
                String temp_beaconinfo ="";
                if(beaconList.size() > 0){
                    textView.setText("");
                    for (int i = 0; i < beaconList.size(); i++) {
                        Beacon beacon = beaconList.get(i);
                        String NAME = "Name:" + beacon.getBluetoothName();
                        String RSSI = "\nRSSI:" + beacon.getRssi();
                        String UUID = "\nUUID:" + beacon.getId1();
                        String Major = "\nMajor:" + beacon.getId2();
                        String Minor = "\nMinor:" + beacon.getId3();
                        String Distance = "\nDistance:" + String.format("" + "%.3f", beacon.getDistance()) + "m\n ";
                        String temp_major = Major.split(":")[1];
                        String temp_minor = Minor.split(":")[1];
                        beaconinfo = NAME + RSSI + UUID + Major + Minor + Distance;
                        if (Int_Major1 == Integer.parseInt(temp_major) && Int_Minor1 == Integer.parseInt(temp_minor)) {
                            list.add(g_ad);
                            if (beacon.getDistance() <= 1) {
                                //거리 1m이하일때
                                //Log.e("beacon", "비콘 1m안에 들어옴");
                                //Toast.makeText(MainActivity.this, "비콘 들어옴(" + beacon.getBluetoothName() +" "+ String.format("%.3f", beacon.getDistance()) + "m)", Toast.LENGTH_SHORT).show();
                                //beaconinfo += "\n비콘들어옴" + beacon.getBluetoothName();
                                //find_beacon.sendEmptyMessage(1);
                            }
                        }else if(Int_Major2 == Integer.parseInt(temp_major) && Int_Minor2 == Integer.parseInt(temp_minor)) {
                            list.add(d_ad);
                        }else{
                            list.add(beacon.getBluetoothName());
                        }
                        Log.i("beacon", "ListAdd "+ NAME + " " + temp_major +" " + temp_minor);
                        temp_beaconinfo += beaconinfo;
                    }
                    textView.append(temp_beaconinfo);
                    beaconList.clear();
            }
            }

        };
}