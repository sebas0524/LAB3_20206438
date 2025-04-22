package com.example.lab3_20206438;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JuegoTriviaActivity extends AppCompatActivity {

    TextView txtCategoria, txtTiempo, txtPregunta, txtContador;
    RadioGroup grupoOpciones;
    Button btnSiguiente;

    List<Pregunta> listaPreguntas = new ArrayList<>();
    int indiceActual = 0;
    int cantidadPreguntas;
    int tiempoTotal;
    CountDownTimer countDownTimer;
    long tiempoRestante;
    String categoria, dificultad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_trivia);

        txtCategoria = findViewById(R.id.txtCategoria);
        txtTiempo = findViewById(R.id.txtTiempo);
        txtPregunta = findViewById(R.id.txtPregunta);
        txtContador = findViewById(R.id.txtContador);
        grupoOpciones = findViewById(R.id.radioGroupOpciones);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Obtener parámetros de intent
        cantidadPreguntas = getIntent().getIntExtra("cantidad", 5);
        categoria = getIntent().getStringExtra("categoria");
        dificultad = getIntent().getStringExtra("dificultad");

        // Mostrar categoría
        txtCategoria.setText(categoria);

        // Calcular tiempo total
        int tiempoPorPregunta = dificultad.equals("easy") ? 5 : dificultad.equals("medium") ? 7 : 10;
        tiempoTotal = cantidadPreguntas * tiempoPorPregunta * 1000;

        obtenerPreguntasDesdeAPI();
    }

    private void iniciarTemporizadorGlobal() {
        countDownTimer = new CountDownTimer(tiempoTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
                int segundos = (int) (millisUntilFinished / 1000);
                txtTiempo.setText(String.format("%02d:%02d", segundos / 60, segundos % 60));
            }

            @Override
            public void onFinish() {
                irAEstadisticas();
            }
        };
        countDownTimer.start();
    }

    private void mostrarPregunta(Pregunta pregunta) {
        txtContador.setText("Pregunta " + (indiceActual + 1) + "/" + cantidadPreguntas);
        txtPregunta.setText(pregunta.getPregunta());

        grupoOpciones.removeAllViews();
        for (String opcion : pregunta.getOpciones()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opcion);
            grupoOpciones.addView(rb);
        }
    }

    private void obtenerPreguntasDesdeAPI() {
        new Thread(() -> {
            try {
                int codigoCategoria = CategoriaMapper.obtenerCodigo(categoria);
                URL url = new URL("https://opentdb.com/api.php?amount=" + cantidadPreguntas +
                        "&category=" + codigoCategoria + "&difficulty=" + dificultad + "&type=multiple");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();
                parsearJSON(result.toString());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al obtener preguntas", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void parsearJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray results = jsonObject.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            String pregunta = HtmlCompat.fromHtml(item.getString("question"), HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
            String correcta = item.getString("correct_answer");
            JSONArray incorrectas = item.getJSONArray("incorrect_answers");
            List<String> opciones = new ArrayList<>();
            opciones.add(correcta);
            for (int j = 0; j < incorrectas.length(); j++) opciones.add(incorrectas.getString(j));
            Collections.shuffle(opciones);
            listaPreguntas.add(new Pregunta(pregunta, correcta, opciones));
        }

        runOnUiThread(() -> {
            iniciarTemporizadorGlobal();
            mostrarPregunta(listaPreguntas.get(indiceActual));

            btnSiguiente.setOnClickListener(view -> {
                indiceActual++;
                if (indiceActual < listaPreguntas.size()) {
                    mostrarPregunta(listaPreguntas.get(indiceActual));
                } else {
                    irAEstadisticas();
                }
            });
        });
    }

    private void irAEstadisticas() {
        if (countDownTimer != null) countDownTimer.cancel();
        Intent intent = new Intent(this, EstadisticasActivity.class);
        // Puedes agregar extras con resultados si lo necesitas
        startActivity(intent);
        finish();
    }
}

class Pregunta {
    private final String pregunta;
    private final String respuestaCorrecta;
    private final List<String> opciones;

    public Pregunta(String pregunta, String respuestaCorrecta, List<String> opciones) {
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
        this.opciones = opciones;
    }

    public String getPregunta() {
        return pregunta;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public List<String> getOpciones() {
        return opciones;
    }
}

class CategoriaMapper {
    public static int obtenerCodigo(String nombre) {
        switch (nombre) {
            case "Cultura General": return 9;
            case "Libros": return 10;
            case "Películas": return 11;
            case "Música": return 12;
            case "Computación": return 18;
            case "Matemática": return 19;
            case "Deportes": return 21;
            case "Historia": return 23;
            default: return 9;
        }
    }
}