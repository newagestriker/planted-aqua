package com.newage.aquapets.models;

public class PresetCalc {

    private float multiplier;
    private float dosageInGrams;
    private float[] PfertzMicros;


    public float calcDosage(float ppm, float volInLitres) {

        multiplier = PfertzMicros[0];

        dosageInGrams = ppm*volInLitres/multiplier;
        return Float.parseFloat(String.format("%.2f",dosageInGrams));


    }

    public PresetCalc(){


        PfertzMicros = new float[]{3.8f};

    }


}

