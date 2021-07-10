package org.openhab.binding.switchbot.internal.config;

public class HubMiniConfig {
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "HubMiniConfig [deviceId=" + deviceId + "]";
    }
}
