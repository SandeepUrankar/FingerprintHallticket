package com.sandeep.firebaseexample;

public class Students {
    private String name;
    private String regno;
    private String sem;
    private String imageurl;
    private String fingerprinttemplate;

    public Students() { }
    public Students(String name, String regno, String sem, String imageurl, String fingerprinttemplate) {
        this.name = name;
        this.regno = regno;
        this.sem = sem;
        this.imageurl = imageurl;
        this.fingerprinttemplate = fingerprinttemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getFingerprinttemplate() {
        return fingerprinttemplate;
    }

    public void setFingerprinttemplate(String fingerprinttemplate) {
        this.fingerprinttemplate = fingerprinttemplate;
    }
}
