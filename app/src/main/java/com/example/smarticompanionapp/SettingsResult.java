/*
 * Represents an object that encapsulates all of the fields that we wish to send
 * and save through the settings
 */

package com.example.smarticompanionapp;

public class SettingsResult {
    private int minLength;
    private int maxLength;
    private boolean pushNotifs;
    private int severityThres;

    public SettingsResult() { }

    public SettingsResult(int minLength, int maxLength, int saveTime, int bitRate, boolean pushNotifs, boolean physNotifs, int severityThres) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pushNotifs = pushNotifs;
        this.severityThres = severityThres;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setPushNotifs(boolean pushNotifs) {
        this.pushNotifs = pushNotifs;
    }
    public boolean getPushNotifs() {
        return pushNotifs;
    }

    public void setSeverityThres(int severityThres) {
        this.severityThres = severityThres;
    }
    public int getSeverityThres() {
        return severityThres;
    }
}
