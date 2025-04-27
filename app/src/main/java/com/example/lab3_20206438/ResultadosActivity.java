package com.example.lab3_20206438;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultadosActivity extends AppCompatActivity {
    TextView txtCorrectas, txtIncorrectas, txtNoRespondidas;
    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        txtCorrectas = findViewById(R.id.txtCorrectas);
        txtIncorrectas = findViewById(R.id.txtIncorrectas);
        txtNoRespondidas = findViewById(R.id.txtNoRespondidas);
        btnVolver = findViewById(R.id.btnVolverJugar);

        // Recibe los datos del intent
        int correctas = getIntent().getIntExtra("correctas", 0);
        int incorrectas = getIntent().getIntExtra("incorrectas", 0);
        int noRespondidas = getIntent().getIntExtra("no_respondidas", 0);

        txtCorrectas.setText("✔️ Correctas: " + correctas);
        txtIncorrectas.setText("❌ Incorrectas: " + incorrectas);
        txtNoRespondidas.setText("⚪ No respondidas: " + noRespondidas);

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadosActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}