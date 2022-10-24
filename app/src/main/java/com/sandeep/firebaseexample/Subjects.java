package com.sandeep.firebaseexample;

public class Subjects {
    private String sno;
    private String datetime;
    private String qpcode;
    private String status;
    private String subname;
    private String sem;
    private String regno;

    public Subjects() {
    }
    public Subjects(String sno, String datetime, String qpcode, String status, String subname, String sem, String regno) {
        this.sno = sno;
        this.datetime = datetime;
        this.qpcode = qpcode;
        this.status = status;
        this.subname = subname;
        this.sem = sem;
        this.regno = regno;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getQpcode() {
        return qpcode;
    }

    public void setQpcode(String qpcode) {
        this.qpcode = qpcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }
    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }
}
