package org.openhab.binding.switchbot.internal.config;

public class HubConfig {
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "HubConfig [deviceId=" + deviceId + "]";
    }
}
