package com.example.lab3_20206438;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategoria, spinnerDificultad;
    private EditText editCantidad;
    private Button btnConexion, btnComenzar;
    private boolean conexionExitosa = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerDificultad = findViewById(R.id.spinnerDificultad);
        editCantidad = findViewById(R.id.editCantidad);
        btnConexion = findViewById(R.id.btnConexion);
        btnComenzar = findViewById(R.id.btnComenzar);

        // Opciones
        String[] categorias = {"Cultura General", "Libros", "Películas", "Música", "Computación", "Matemática", "Deportes", "Historia"};
        String[] dificultades = {"Fácil", "Medio", "Difícil"};

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<String> dificultadAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dificultades);
        spinnerDificultad.setAdapter(dificultadAdapter);

        btnConexion.setOnClickListener(v -> {
            if (validarCampos()) {
                if (hayConexionInternet()) {
                    conexionExitosa = true;
                    Toast.makeText(this, "Success Toast", Toast.LENGTH_SHORT).show();
                    btnComenzar.setEnabled(true);
                } else {
                    Toast.makeText(this, "‘Error Toast", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnComenzar.setOnClickListener(v -> {
            if (conexionExitosa) {
                String cantidadStr = editCantidad.getText().toString().trim();
                String categoriaNombre = spinnerCategoria.getSelectedItem().toString();
                String dificultadTexto = spinnerDificultad.getSelectedItem().toString().toLowerCase();

                if (cantidadStr.isEmpty()) {
                    Toast.makeText(this, "Ingresa la cantidad de preguntas", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidadPreguntas;
                try {
                    cantidadPreguntas = Integer.parseInt(cantidadStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Traducir dificultad al formato de la API
                if (dificultadTexto.equals("fácil")) dificultadTexto = "easy";
                else if (dificultadTexto.equals("medio")) dificultadTexto = "medium";
                else if (dificultadTexto.equals("difícil")) dificultadTexto = "hard";

                Map<String, String> categorias = CategoriaMapper.class;
                String categoriaCodigo = categorias.get(categoriaNombre);

                // Ir a TriviaActivity con parámetros
                Intent intent = new Intent(MainActivity.this, JuegoTriviaActivity.class);
                intent.putExtra("categoria", categoriaCodigo);
                intent.putExtra("categoriaNombre", categoriaNombre);
                intent.putExtra("dificultad", dificultadTexto);
                intent.putExtra("cantidad", cantidadPreguntas);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Primero verifica la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos() {
        String cantidadText = editCantidad.getText().toString().trim();
        if (spinnerCategoria.getSelectedItem() == null || spinnerDificultad.getSelectedItem() == null || cantidadText.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        int cantidad = Integer.parseInt(cantidadText);
        if (cantidad <= 0) {
            Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean hayConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

}