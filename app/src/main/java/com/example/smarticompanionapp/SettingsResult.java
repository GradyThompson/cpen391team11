/*
 * Represents an object that encapsulates all of the fields that we wish to send
 * and save through the settings
 */

package com.example.smarticompanionapp;

public class SettingsResult {
    private int minLength;
    private int maxLength;
    private int saveTime;
    private int bitRate;
    private boolean pushNotifs;
    private boolean physNotifs;
    private int severityThres;

    public SettingsResult() { }

    public SettingsResult(int minLength, int maxLength, int saveTime, int bitRate, boolean pushNotifs, boolean physNotifs, int severityThres) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.saveTime = saveTime;
        this.bitRate = bitRate;
        this.pushNotifs = pushNotifs;
        this.physNotifs = physNotifs;
        this.severityThres = severityThres;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setSaveTime(int saveTime) {
        this.saveTime = saveTime;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setPushNotifs(boolean pushNotifs) {
        this.pushNotifs = pushNotifs;
    }

    public void setPhysNotifs(boolean physNotifs) {
        this.physNotifs = physNotifs;
    }

    public void setSeverityThres(int severityThres) {
        this.severityThres = severityThres;
    }
}
