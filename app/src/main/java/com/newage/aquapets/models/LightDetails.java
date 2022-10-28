package com.newage.aquapets.models;

public class LightDetails {

    private String lightType;
    private  String lumensPerWatt;
    private String count;
    private String wattPerCount;
    private float totalLumens;
    private long id;

    public LightDetails(){
        lightType ="";
        lumensPerWatt="1";
        count ="1";
        wattPerCount="1";
        totalLumens =0f;

    }

    public void setTotalLumens(float efficiency) {

        totalLumens = Float.parseFloat(count.replace(",","."))*Float.parseFloat(wattPerCount.replace(",","."))*Float.parseFloat(lumensPerWatt.replace(",","."))*efficiency;
    }

    public void setTotalLumens(String totalLumens) {

        this.totalLumens = Float.parseFloat(totalLumens.replace(",","."));
    }

    public String getLightType() {
        return lightType;
    }

    public void setLightType(String lightType) {
        this.lightType = lightType;
    }

    public String getLumensPerWatt() {
        return lumensPerWatt;
    }

    public void setLumensPerWatt(String lumensPerWatt) {
        this.lumensPerWatt = lumensPerWatt;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getWattPerCount() {
        return wattPerCount;
    }

    public void setWattPerCount(String wattPerCount) {
        this.wattPerCount = wattPerCount;
    }

   public float getTotalLumens() {

       return totalLumens;
   }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
