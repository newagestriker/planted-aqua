package com.newage.aquapets.models;

public class microDetails {

    private double ppm=0;
    public static double volumeinltrs=0;
    private double rootweight=0;
    private double childweight=0;

    public microDetails(double rootweight, double childweight, double ppm){
        this.rootweight=rootweight;
        this.childweight=childweight;
        this.ppm=ppm;
    }
    public double calcDosage(){
        double dosageingrams=0;
        dosageingrams=((ppm/(childweight/rootweight))*volumeinltrs)/1000d;
        return dosageingrams;
    }
    public double calcPPM(double quantityingrams){
        ppm=((childweight/rootweight)*quantityingrams*1000d)/volumeinltrs;
        return ((Math.round(ppm*1000d))/1000d);

    }
}
