package com.example.lab3_20206438;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategoria, spinnerDificultad;
    private EditText editTextCantidad;
    private Button btnComprobarConexion, btnComenzar;

    private boolean conexionValidada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerDificultad = findViewById(R.id.spinnerDificultad);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        btnComprobarConexion = findViewById(R.id.btnComprobarConexion);
        btnComenzar = findViewById(R.id.btnComenzar);


        String[] dificultades = {"Dificultad","fácil", "medio", "difícil"};
        String[] categorias = {"Categoría", "Cultura General", "Libros", "Películas", "Música", "Computación", "Matemática", "Deportes", "Historia"};
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);

        spinnerCategoria.setAdapter(adapterCategoria);

        ArrayAdapter<String> adapterDificultad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dificultades);
        spinnerDificultad.setAdapter(adapterDificultad);



        btnComprobarConexion.setOnClickListener(v -> {
            if (validarEntradas()) {
                if (hayConexionInternet()) {
                    Toast.makeText(MainActivity.this, "¡Success Toast!", Toast.LENGTH_SHORT).show();
                    conexionValidada = true;
                    btnComenzar.setEnabled(true);
                    btnComenzar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#388E3C"))); // o un azul si prefieres
                    btnComenzar.setTextColor(Color.WHITE);
                } else {
                    Toast.makeText(MainActivity.this, "Error Toast", Toast.LENGTH_SHORT).show();
                    conexionValidada = false;
                    btnComenzar.setEnabled(false);
                }
            }
        });


        btnComenzar.setOnClickListener(v -> {
            if (conexionValidada) {
                String categoriaNombre = spinnerCategoria.getSelectedItem().toString();
                String dificultadTexto = spinnerDificultad.getSelectedItem().toString().toLowerCase();
                String cantidadStr = editTextCantidad.getText().toString().trim();

                if (dificultadTexto.equals("fácil")) dificultadTexto = "easy";
                else if (dificultadTexto.equals("medio")) dificultadTexto = "medium";
                else if (dificultadTexto.equals("difícil")) dificultadTexto = "hard";

                Map<String, String> mapaCategorias = obtenerCategorias();
                String categoriaCodigo = mapaCategorias.get(categoriaNombre);


                if (categoriaCodigo == null) {
                    Toast.makeText(MainActivity.this, "Categoría no válida", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = Integer.parseInt(cantidadStr);

                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("categoria", categoriaCodigo);
                intent.putExtra("dificultad", dificultadTexto);
                intent.putExtra("cantidad", cantidad);
                startActivity(intent);
            }
        });



    }

    private boolean validarEntradas() {
        String cantidadStr = editTextCantidad.getText().toString().trim();
        String categoriaSeleccionada = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "";
        String dificultadSeleccionada = spinnerDificultad.getSelectedItem() != null ? spinnerDificultad.getSelectedItem().toString() : "";

        if (categoriaSeleccionada.equals("Selecciona tu categoría") ||
                dificultadSeleccionada.equals("Selecciona tu categoría") ||
                cantidadStr.isEmpty()) {

            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                Toast.makeText(this, "La cantidad debe ser un número positivo", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean hayConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
    }

    private Map<String, String> obtenerCategorias() {
        Map<String, String> categorias = new HashMap<>();
        categorias.put("Cultura General", "9");
        categorias.put("Libros", "10");
        categorias.put("Películas", "11");
        categorias.put("Música", "12");
        categorias.put("Computación", "18");
        categorias.put("Matemática", "19");
        categorias.put("Deportes", "21");
        categorias.put("Historia", "23");
        return categorias;
    }


}