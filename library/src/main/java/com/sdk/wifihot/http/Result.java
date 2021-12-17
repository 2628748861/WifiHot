package com.sdk.wifihot.http;

public class Result {
    private String key;
    private String ssid;
    private String password;
    private String code;

    public Result() {
    }

    public Result(String key, String ssid, String password, String code) {
        this.key = key;
        this.ssid = ssid;
        this.password = password;
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Result{" +
                "key='" + key + '\'' +
                ", ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
