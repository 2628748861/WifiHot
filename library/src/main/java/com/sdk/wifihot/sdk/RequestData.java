package com.sdk.wifihot.sdk;

public class RequestData {
    private String ssid;
    private String password;
    private String identification;
    private String model;
    private String version;
    private String code;
    private String timestamp;
    private String firmware;

    private RequestData(){}

    private RequestData(String ssid, String password, String identification, String model, String version, String code, String timestamp, String firmware) {
        this.ssid = ssid;
        this.password = password;
        this.identification = identification;
        this.model = model;
        this.version = version;
        this.code = code;
        this.timestamp = timestamp;
        this.firmware = firmware;
    }

    public String getCode() {
        return code;
    }

    public String getFirmware() {
        return firmware;
    }

    public String getIdentification() {
        return identification;
    }

    public String getModel() {
        return model;
    }

    public String getPassword() {
        return password;
    }

    public String getSsid() {
        return ssid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public static class Builder{
        private String ssid;
        private String password;
        private String identification;
        private String model;
        private String version;
        private String code;
        private String timestamp;
        private String firmware;

        public Builder ssid(String ssid){
            this.ssid=ssid;
            return this;
        }
        public Builder password(String password){
            this.password=password;
            return this;
        }
        public Builder identification(String identification){
            this.identification=identification;
            return this;
        }
        public Builder model(String model){
            this.model=model;
            return this;
        }
        public Builder code(String code){
            this.code=code;
            return this;
        }
        public Builder timestamp(String timestamp){
            this.timestamp=timestamp;
            return this;
        }
        public Builder firmware(String firmware){
            this.firmware=firmware;
            return this;
        }
        public Builder version(String version){
            this.version=version;
            return this;
        }
        public RequestData build(){
            return new RequestData(ssid,password,identification,model,version,code,timestamp,firmware);
        }
    }
}
