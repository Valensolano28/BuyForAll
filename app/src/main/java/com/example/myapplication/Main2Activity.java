package com.example.myapplication;

import android.annotation.SuppressLint;
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
import android.media.MediaPlayer;
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class Main2Activity extends AppCompatActivity implements BeaconConsumer {

    private static final String ACCESS_FINE_LOCATION = "2";
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    private TextView textView;

    private final String set_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825";
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
    private String g_ad = "google beacon";
    private String d_ad = "daum beacon";
    private int a=0;
    private int b=0;
    private int e=0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity2_main);
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);
        onbutton = findViewById(R.id.onbutton);
        offbutton = findViewById(R.id.offbutton);


        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 2);//REQUISITO DE PERMISOS PARA UBICACION DEL TELEFONO

        beaconManager = BeaconManager.getInstanceForApplication(this);//INSTANCIAR LA APLICACION
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));//INSTANCIAR CARACTERISTICAS BEACON
       //BOTON DE MOSTRAR LISTA
        onbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconManager.bind(Main2Activity.this);
                Toast.makeText(getApplicationContext(),"Buscar beacons",Toast.LENGTH_LONG).show(); //QUE HACER CUANDO SE DA CLICK EN EL BOTON DE MOSTAR LISTA
            }
        });

        //BOTON DE DEJAR DE MOSTRAR LISTA
        offbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear(); //CONGELA LA LISTA
                arrayAdapter.notifyDataSetChanged(); //ADAPTAR EL VECTOR AL ESPACIO DE PANTALLA
                arrayAdapter.notifyDataSetChanged(); //ADAPTAR EL VECTOR AL ESPACIO DE PANTALLA
                textView.setText("Información de beacons...");

                try {
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                    beaconManager.stopRangingBeaconsInRegion(region);

                } catch (RemoteException e) { e.printStackTrace(); }
                beaconManager.unbind(Main2Activity.this);
                Toast.makeText(getApplicationContext(),"Ocultar lista",Toast.LENGTH_LONG).show(); //DEJAR DE ESCANEAR
            }
        });

        //LISTA DE BEACONS ACTIVOS
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
                //CUANDO SE SELECCIONA UN BEACON EN LA LISTA
            }
        });
    }


    //FUNCIONES DE LA LIBRERIA
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
        if (msg.equals(g_ad)) {
            url = g_url;
        }else if (msg.equals(d_ad)){
            url = d_url;
        }else{
            url = n_url;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
        builder.setTitle(msg);
        builder.setMessage(msg + " Do you want to connect?");
        builder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"no",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }


    Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void handleMessage(Message msg) { //PERMITE PROCESAR Y ENVIAR MENSAJES EJECUTABLES ASOCIADOS CON HILOS.
                String beaconinfo;
                String temp_beaconinfo ="";

                if(beaconList.size() > 0){
                    textView.setText("");
                    for (int i = 0; i < beaconList.size(); i++) {
                        Beacon beacon = beaconList.get(i);//OBJETO DE LA CLASE BEACON.
                        String NAME = "Name:" + beacon.getBluetoothName();
                        String RSSI = "\nRSSI:" + beacon.getRssi();
                        String UUID = "\nUUID:" + beacon.getId1();
                        String Major = "\nMajor:" + beacon.getId2();
                        String Minor = "\nMinor:" + beacon.getId3();
                        String Distance = "\nDistance:" + String.format("" + "%.3f", beacon.getDistance()) + "m\n";

                        String temp_UUID = UUID.split(":")[1];
                        String temp_major = Major.split(":")[1];
                        String temp_minor = Minor.split(":")[1];

                        beaconinfo = NAME + RSSI + UUID + Major + Minor + Distance;

                        if(temp_UUID.equals(set_UUID)) {
                            if (Int_Major1 == Integer.parseInt(temp_major) && Int_Minor1 == Integer.parseInt(temp_minor)) {
                                list.add(g_ad);
                                if (beacon.getDistance() <= 1) {
                                    //CUANDO EL BEACON ESTA A MENOS DE UN METRO

                                }
                            } else if (Int_Major2 == Integer.parseInt(temp_major) && Int_Minor2 == Integer.parseInt(temp_minor)) {
                                list.add(d_ad);
                            } else {
                                list.add(beacon.getBluetoothName());
                            }
                        }else {
                            list.add(beacon.getBluetoothName());
                        }
                        Log.i("beacon", "ListAdd "+ NAME + " " + temp_major +" " + temp_minor + " " + temp_UUID);
                        String uuid2=beacon.getId1().toString();
                        Log.i("uuidreal","El uuid encontrado en la region:"+ uuid2);

                        Intent intent = getIntent();
                        Bundle extras = intent.getExtras();

                        String uuid = extras.getString("UUID");
                        String comparar=uuid;
                        Log.i("uuidbasededatos","El uuid de la base de datos:"+ comparar);

                        String uuid1 = extras.getString("UUIDPRODUCTO");
                        String comparar1=uuid1;
                        Log.i("productouuidbasededatos","El uuid de la base de datos:"+ comparar1);


                        //SI SE ENCUENTRA DENTRO DEL PASILLO Y SE ENCUENTRA EL PRODUCTO
                        if ((b==0)&&(uuid2.equals(comparar1))){
                            handler.postDelayed(r1, 1000);
                            if (beacon.getDistance() >= 0.6) {
                                b=0;}
                        }
                        //SE ENTRA AL PASILLO FÍSICAMENTE
                        if(uuid2.equals(comparar)){
                            if ((a == 0) && (beacon.getDistance() <= 0.4)) {
                                handler.postDelayed(r, 1000);
                                //SE SALE DEL PASILLO FÍSICAMENTE
                            }if (beacon.getDistance() >= 0.6) {
                                a = 0;}
                            }
                        temp_beaconinfo += beaconinfo;
                    }
                    textView.append(temp_beaconinfo);
                    beaconList.clear();
                }
            }
        };


    Runnable r=new Runnable() {
        public void run() {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String audio= extras.getString("AUDIO");
            String audioreproducir=audio;
            Log.i("audio","audio es:"+ audio);
            MediaPlayer mp;
            mp=MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(audioreproducir,"raw",getPackageName()));
            mp.start();
            a=a+1;
        }
    };
    Runnable r1=new Runnable() {
        public void run() {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String audio= extras.getString("PRODUCTO");
            String audioreproducir=audio;
            Log.i("audio","audio es:"+ audio);
            MediaPlayer mp1;
             mp1=MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(audioreproducir,"raw",getPackageName()));
             mp1.start();
             b=b+1;
        }
    };


}