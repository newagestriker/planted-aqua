package com.newage.aquapets.models;



public class ChatBoxObject {


   /* chatDatabase.child(userID).child("SN").setValue(Long.toString(sn));
                chatDatabase.child(userID).child("DN").setValue(displayName);
                chatDatabase.child(userID).child("MSG").setValue(send_msg);
                chatDatabase.child(userID).child("PU").setValue(PU);
                chatDatabase.child(userID).child("TS").setValue(String.format(Locale.getDefault(),"%d",calendar.getTimeInMillis()));*/

    private String SN;
    private String DN;
    private  String MSG;
    private String PU;
    private String TS;
    private String UID;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getDN() {
        return DN;
    }

    public void setDN(String DN) {
        this.DN = DN;
    }

    public String getMSG() {
        return MSG;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }

    public String getPU() {
        return PU;
    }

    public void setPU(String PU) {
        this.PU = PU;
    }

    public String getTS() {
        return TS;
    }

    public void setTS(String TS) {
        this.TS = TS;
    }
}

