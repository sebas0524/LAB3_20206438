package com.example.lab3_20206438;

import java.util.List;

public class PreguntaTrivia {
    private String categoria;
    private String pregunta;
    private String respuestaCorrecta;
    private List<String> respuestas;

    public PreguntaTrivia(String categoria, String pregunta, String respuestaCorrecta, List<String> respuestas) {
        this.categoria = categoria;
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
        this.respuestas = respuestas;
    }

    public String getCategoria() { return categoria; }
    public String getPregunta() { return pregunta; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public List<String> getRespuestas() { return respuestas; }
}