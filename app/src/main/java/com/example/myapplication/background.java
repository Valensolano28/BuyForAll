package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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
import java.nio.charset.StandardCharsets;
//CLASE PARA CONECTAR A LA INTERFAZ DE LOGIN CON LA BASE DE DATOS

public class background extends AsyncTask<String, Void, String> {

    AlertDialog dialog;  //Ventana para verificar el inicio de sesión
    Context context;

    public background(Context context) //Constructor
    {

        this.context = context;
    }

    @Override //Se ejecuta antes de la conexión con la base de datos
    protected void onPreExecute() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Login Status");
    }
    @Override //Se ejecuta después de la conexión con la base de datos
    protected void onPostExecute(String s) {
        dialog.setMessage(s);
        dialog.show();

        if(s.contains("login exitoso"))
        {
            Intent intent_name = new Intent();
            intent_name.setClass(context.getApplicationContext(),list.class);
            context.startActivity(intent_name);
        }
    }
    @Override
    protected String doInBackground(String... voids) {

        String result = "";
        String user = voids[0];
        String pass = voids[1];
        String ip = "192.168.0.21";
        String connstr = "http://"+ip+":80/login.php";  //Se pone los datos de IP del servidor donde esta corriendo la base de datos local y el PHP
        // El php se encuentra en la carpeta HTDOCS en donde esta instalado Xampp

        try {
            //Funciones para la conexión con la base de datos
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");   //Tipo de método de conexión de Android Studio con PHP
            http.setDoInput(true);
            http.setDoOutput(true);
            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));

            //Se envian los datos de usuario y contraseña al php
            String data = URLEncoder.encode("user","UTF-8")+"="+ URLEncoder.encode(user,"UTF-8")
                    +"&&"+ URLEncoder.encode("pass","UTF-8")+"="+ URLEncoder.encode(pass,"UTF-8");

            //Funciones para enviar los datos "data"
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();
            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.ISO_8859_1));

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

            //Se lee por consola El resultado enviado por la base de datos

            Log.i("inicial", "RESULTADO DE LA BASE DE DATOS:" + result);

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
