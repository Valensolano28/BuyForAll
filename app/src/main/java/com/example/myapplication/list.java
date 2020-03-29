package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class list extends AppCompatActivity {
    //Atributos de la clase list
    TextView result;
    Button button;
    String resultado;
    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        result = findViewById(R.id.result);
        button = findViewById(R.id.button);
             //  MediaPlayer mp1;
             //  mp1=MediaPlayer.create(getApplicationContext(), getResources().getIdentifier("lista","raw",getPackageName()));
             //  mp1.start();

        button.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick() {
                getSpeechInputDialog();
            }
            @Override
            public void onDoubleClick() throws ExecutionException, InterruptedException {
                redirect2();
                Log.i("dobleclick", "... "+ "dobleclick"); //Se imprime el UUID respuesta de la base de datos
            }
        });

    }
    public void getSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

        CustomRecognitionListener listener = new CustomRecognitionListener(result);
        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(listener);
        sr.startListening(intent);
    }

    public void getSpeechInputDialog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        //Locale.getDefault take the language of the device
        //To change the language change to Locale.LANGUAGE_NAME
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> resultString = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                result.setText(resultString.get(0));
                resultado=resultString.get(0);
                Log.i("pasillo", "... "+ resultado); //Se imprime el UUID respuesta de la base de datos
            }
        }
    }
    public void redirect2() throws ExecutionException, InterruptedException {

        background2 bg = new background2(this);
        String uuid=bg.execute(resultado).get();
        Log.i("consultainicial", "... "+ uuid); //Se imprime el UUID respuesta de la base de datos

        background3 bg1=new background3(this);
        String audio=bg1.execute(resultado).get();
        Log.i("consultainicial", "... "+ audio); //Se imprime el Audio del Pasillo de la base de datos

        background4 bg2=new background4(this);
        String producto=bg2.execute(resultado).get();
        Log.i("consultainicial", "... "+ producto); //Se imprime el Audio del producto de la base de datos

        background5 bg3=new background5(this);
        String uuidproducto=bg3.execute(resultado).get();
        Log.i("consultainicial", "... "+ uuidproducto); //Se imprime el uuid del producto de la base de datos

        Intent i = new Intent(this,Main2Activity.class);
        Bundle extras = new Bundle();

        extras.putString("UUID",uuid);
        extras.putString("AUDIO",audio);
        extras.putString("PRODUCTO",producto);
        extras.putString("UUIDPRODUCTO",uuidproducto);
        i.putExtras(extras);
        startActivity(i);
   }
    public class background2 extends AsyncTask<String, Void, String> {

        Context context;
        public background2(Context context) //Constructor
        {

            this.context = context;
        }
        @Override //Se ejecuta antes de la conexión con la base de datos
        protected void onPreExecute() {

        }
        @Override //Se ejecuta después de la conexión con la base de datos
        protected void onPostExecute(String s) {

         }
        @Override
        protected String doInBackground(String... voids) {

            String result = "";
            String pasillo = voids[0];
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String ip = extras.getString("IP");
            String connstr ="http://"+ip+"/consulta.php";             //Se pone los datos de IP del servidor donde esta corriendo la base de datos local y el PHP
            // El php se encuentra en la carpeta HTDOCS en donde esta instalado Xampp

            try {
                //Funciones para la conexión con la base de datos
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                http.setRequestMethod("POST"); //Tipo de método de conexión de Android Studio con PHP

                http.setDoInput(true);
                http.setDoOutput(true);
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

                //Se envia el dato del pasillo obtenido del comando de voz obtenido de la aplicación
                String data = URLEncoder.encode("pasillo","UTF-8")+"="+ URLEncoder.encode(pasillo,"UTF-8");

                //Funciones para enviar los datos "data"
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();
                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));


                //Variable para recibir respuesta de la base de datos
                String line ="";

                //Recepción de resultados de la base de datos
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }

                //Se cierra el lector de la base de datos y se desconecta
                reader.close();
                ips.close();
                http.disconnect();


                //Se retorna el resultado
                return result;
                //Se capturan excepciones
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
            return result;
        }
    }


    public class background3 extends AsyncTask<String, Void, String> {

        Context context;
        public background3(Context context) //Constructor
        {

            this.context = context;
        }
        @Override //Se ejecuta antes de la conexión con la base de datos
        protected void onPreExecute() {
        }
        @Override //Se ejecuta después de la conexión con la base de datos
        protected void onPostExecute(String s) {

        }
        @Override
        protected String doInBackground(String... voids) {

            String result = "";
            String pasillo = voids[0];
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String ip = extras.getString("IP");
            String connstr="http://"+ip+"/consulta1.php";//Se pone los datos de IP del servidor donde esta corriendo la base de datos local y el PHP
            // El php se encuentra en la carpeta HTDOCS en donde esta instalado Xampp

            try {
                //Funciones para la conexión con la base de datos
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST"); //Tipo de método de conexión de Android Studio con PHP
                http.setDoInput(true);
                http.setDoOutput(true);
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

                //Se envia el dato del pasillo obtenido del comando de voz obtenido de la aplicación
                String data = URLEncoder.encode("pasillo","UTF-8")+"="+ URLEncoder.encode(pasillo,"UTF-8");

                //Funciones para enviar los datos "data"
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();
                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));

                //Variable para recibir respuesta de la base de datos
                String line ="";

                //Recepción de resultados de la base de datos
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }

                //Se cierra el lector de la base de datos y se desconecta
                reader.close();
                ips.close();
                http.disconnect();

                //Se retorna el resultado
                return result;
                //Se capturan excepciones
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
            return result;
        }
    }

    public class background4 extends AsyncTask<String, Void, String> {

        Context context;
        public background4(Context context) //Constructor
        {

            this.context = context;
        }
        @Override //Se ejecuta antes de la conexión con la base de datos
        protected void onPreExecute() {
        }
        @Override //Se ejecuta después de la conexión con la base de datos
        protected void onPostExecute(String s) {

        }
        @Override
        protected String doInBackground(String... voids) {

            String result = "";
            String pasillo = voids[0];
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String ip = extras.getString("IP");
            String connstr="http://"+ip+"/consulta2.php";
            //Se pone los datos de IP del servidor donde esta corriendo la base de datos local y el PHP
            // El php se encuentra en la carpeta HTDOCS en donde esta instalado Xampp

            try {
                //Funciones para la conexión con la base de datos
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST"); //Tipo de método de conexión de Android Studio con PHP
                http.setDoInput(true);
                http.setDoOutput(true);
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

                //Se envia el dato del pasillo obtenido del comando de voz obtenido de la aplicación
                String data = URLEncoder.encode("pasillo","UTF-8")+"="+ URLEncoder.encode(pasillo,"UTF-8");

                //Funciones para enviar los datos "data"
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();
                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));

                //Variable para recibir respuesta de la base de datos
                String line ="";

                //Recepción de resultados de la base de datos
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }

                //Se cierra el lector de la base de datos y se desconecta
                reader.close();
                ips.close();
                http.disconnect();

                //Se retorna el resultado
                return result;
                //Se capturan excepciones
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
            return result;
        }
    }

    public class background5 extends AsyncTask<String, Void, String> {

        Context context;
        public background5(Context context) //Constructor
        {

            this.context = context;
        }
        @Override //Se ejecuta antes de la conexión con la base de datos
        protected void onPreExecute() {
        }
        @Override //Se ejecuta después de la conexión con la base de datos
        protected void onPostExecute(String s) {

        }
        @Override
        protected String doInBackground(String... voids) {

            String result = "";
            String pasillo = voids[0];
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String ip = extras.getString("IP");
            String connstr="http://"+ip+"/consulta3.php";//Se pone los datos de IP del servidor donde esta corriendo la base de datos local y el PHP
            // El php se encuentra en la carpeta HTDOCS en donde esta instalado Xampp

            try {
                //Funciones para la conexión con la base de datos
                URL url = new URL(connstr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST"); //Tipo de método de conexión de Android Studio con PHP
                http.setDoInput(true);
                http.setDoOutput(true);
                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));

                //Se envia el dato del pasillo obtenido del comando de voz obtenido de la aplicación
                String data = URLEncoder.encode("pasillo","UTF-8")+"="+ URLEncoder.encode(pasillo,"UTF-8");

                //Funciones para enviar los datos "data"
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();
                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));

                //Variable para recibir respuesta de la base de datos
                String line ="";

                //Recepción de resultados de la base de datos
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }

                //Se cierra el lector de la base de datos y se desconecta
                reader.close();
                ips.close();
                http.disconnect();

                //Se retorna el resultado
                return result;
                //Se capturan excepciones
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }
            return result;
        }
    }

}
