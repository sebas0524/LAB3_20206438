package com.example.lab3_20206438;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class QuizActivity extends AppCompatActivity {

    private TextView textPregunta, textTimer, textProgreso, textCategoria;
    private RadioGroup radioGroupRespuestas;
    private Button btnSiguiente;

    private List<Pregunta> preguntas;
    private int indicePreguntaActual = 0;

    private int cantidad, tiempoTotal;
    private String categoria, dificultad;
    private String nombreCategoria;
    private int segundosPorPregunta;

    private Handler handler = new Handler();
    private int tiempoRestante;
    private Runnable temporizadorRunnable;
    // NUEVO: Contadores
    private int correctas = 0;
    private int incorrectas = 0;
    private int noRespondidas = 0;

    private boolean preguntaRespondida = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textPregunta = findViewById(R.id.textPregunta);
        textTimer = findViewById(R.id.textTimer);
        textProgreso = findViewById(R.id.textProgreso);
        textCategoria = findViewById(R.id.textCategoria);
        radioGroupRespuestas = findViewById(R.id.radioGroupRespuestas);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Datos de la MainActivity (via Intent)
        cantidad = getIntent().getIntExtra("cantidad", 3);
        categoria = getIntent().getStringExtra("categoria"); // ID en la API
        dificultad = getIntent().getStringExtra("dificultad");
        nombreCategoria = getIntent().getStringExtra("nombreCategoria");

        textCategoria.setText(nombreCategoria);

        switch (dificultad) {
            case "easy":
                segundosPorPregunta = 5;
                break;
            case "medium":
                segundosPorPregunta = 7;
                break;
            case "hard":
                segundosPorPregunta = 10;
                break;
            default:
                segundosPorPregunta = 5;
                break;
        }
        tiempoTotal = cantidad * segundosPorPregunta;
        tiempoRestante = tiempoTotal;

        cargarPreguntasDesdeApi();

        btnSiguiente.setOnClickListener(v -> {
            evaluarRespuesta();

            if (indicePreguntaActual < preguntas.size() - 1) {
                indicePreguntaActual++;
                mostrarPregunta();
            } else {
                finalizarJuego();
            }

            radioGroupRespuestas.clearCheck();
        });
    }

    private void cargarPreguntasDesdeApi() {
        String url = String.format(Locale.US,
                "https://opentdb.com/api.php?amount=%d&category=%s&difficulty=%s&type=multiple",
                cantidad, categoria, dificultad);

        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("API_URL", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    preguntas = new ArrayList<>();
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            String question = Html.fromHtml(obj.getString("question")).toString();
                            String correct = Html.fromHtml(obj.getString("correct_answer")).toString();

                            JSONArray incorrectArr = obj.getJSONArray("incorrect_answers");
                            List<String> incorrect = new ArrayList<>();
                            for (int j = 0; j < incorrectArr.length(); j++) {
                                incorrect.add(Html.fromHtml(incorrectArr.getString(j)).toString());
                            }

                            preguntas.add(new Pregunta(question, correct, incorrect));
                        }

                        mostrarPregunta();
                        iniciarTemporizador();
                    } catch (Exception e) {
                        Log.e("API_ERROR", e.getMessage());
                        Toast.makeText(this, "Error al cargar preguntas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VOLLEY_ERROR", error.toString());
                    Toast.makeText(this, "No se pudo conectar con la API", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void mostrarPregunta() {
        Pregunta p = preguntas.get(indicePreguntaActual);
        textPregunta.setText(p.getQuestion());
        textProgreso.setText("Pregunta " + (indicePreguntaActual + 1) + "/" + preguntas.size());

        radioGroupRespuestas.removeAllViews();
        List<String> opciones = new ArrayList<>(p.getIncorrectAnswers());
        opciones.add(p.getCorrectAnswer());
        Collections.shuffle(opciones);

        for (String opcion : opciones) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opcion);
            radioGroupRespuestas.addView(rb);
        }
    }

    private void iniciarTemporizador() {
        temporizadorRunnable = new Runnable() {
            @Override
            public void run() {
                tiempoRestante--;
                textTimer.setText("00:" + String.format("%02d", tiempoRestante));
                if (tiempoRestante <= 0) {
                    finalizarJuego();
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(temporizadorRunnable, 1000);
    }

    private void evaluarRespuesta() {
        int seleccionadoId = radioGroupRespuestas.getCheckedRadioButtonId();

        if (seleccionadoId == -1) {
            noRespondidas++;
        } else {
            RadioButton seleccionado = findViewById(seleccionadoId);
            String respuesta = seleccionado.getText().toString();
            String correcta = preguntas.get(indicePreguntaActual).getCorrectAnswer();

            if (respuesta.equals(correcta)) {
                correctas++;
            } else {
                incorrectas++;
            }
        }
    }
    private void finalizarJuego() {
        handler.removeCallbacks(temporizadorRunnable);

        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("correctas", correctas);
        intent.putExtra("incorrectas", incorrectas);
        intent.putExtra("no_respondidas", cantidad - (correctas + incorrectas));
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(temporizadorRunnable);
    }
}
