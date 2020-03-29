package com.example.myapplication;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
//CLASE USADA PARA CAPTURAR EL SONIDO DE VOZ Y TRADUCIRLO A STRING

public class CustomRecognitionListener  implements RecognitionListener {
    //En el textview de la parte superior de la interfaz se imprime el resultado
    private TextView result;
    //Método para imprimer palabra en la interfaz
    CustomRecognitionListener(View view) {
        result = (TextView) view;
    }

    //Métodos para leer la voz
    private static final String TAG = "RecognitionListener";

    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
    }

    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged");
    }

    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    public void onEndOfSpeech() {
        Log.d(TAG, "onEndofSpeech");
    }

    public void onError(int error) {
        Log.e(TAG, "error " + error);
    }

    //Vector de resultados
    public void onResults(Bundle results) {
        ArrayList<String> resultString =
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        result.setText(resultString.get(0));
    }

    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType);
    }
}

