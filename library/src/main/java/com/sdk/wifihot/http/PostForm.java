package com.sdk.wifihot.http;

public class PostForm {
    private String identification;
    private String model;
    private String version;
    private String code;
    private String timestamp;
    private String firmware;
    private String token;

    public PostForm(String identification, String model, String version, String code, String timestamp, String firmware, String token) {
        this.identification = identification;
        this.model = model;
        this.version = version;
        this.code = code;
        this.timestamp = timestamp;
        this.firmware = firmware;
        this.token = token;
    }

    public PostForm() {
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "PostForm{" +
                "identification='" + identification + '\'' +
                ", model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", code='" + code + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", firmware='" + firmware + '\'' +
                ", token='" + token + '\'' +
                '}';
    }


}
