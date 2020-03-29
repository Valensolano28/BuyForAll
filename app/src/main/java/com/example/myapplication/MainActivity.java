package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText pas,usr;
    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usr = (EditText) findViewById(R.id.username);
        pas = (EditText) findViewById(R.id.password);
        ip="192.168.0.13";
       // MediaPlayer mp;
       // mp=MediaPlayer.create(getApplicationContext(), getResources().getIdentifier("intro","raw",getPackageName()));
       // mp.start();
    }

    public void loginBtn(View view) {
        String user = usr.getText().toString();
        String pass = pas.getText().toString();
        background bg = new background(this);
        Intent i = new Intent(this,background.class);
        Bundle extras = new Bundle();
        extras.putString("IP",ip);
        i.putExtras(extras);
        bg.execute(user,pass);
    }

    public void redirect(View view) {
        Intent i = new Intent(this,list.class);
        Bundle extras = new Bundle();
        extras.putString("IP",ip);
        i.putExtras(extras);
        startActivity(i);
    }

}
