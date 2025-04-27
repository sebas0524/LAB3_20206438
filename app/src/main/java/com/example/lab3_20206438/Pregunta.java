package com.example.lab3_20206438;

import java.util.List;

public class Pregunta {
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public Pregunta(String question, String correctAnswer, List<String> incorrectAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<String> getIncorrectAnswers() { return incorrectAnswers; }
}