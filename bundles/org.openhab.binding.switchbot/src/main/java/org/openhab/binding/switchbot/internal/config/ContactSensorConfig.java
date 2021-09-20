package org.openhab.binding.switchbot.internal.config;

public class ContactSensorConfig {
    private String deviceId;
    private int refreshInterval;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    @Override
    public String toString() {
        return "HumidifierConfig [deviceId=" + deviceId + ", refreshInterval=" + refreshInterval + "]";
    }
}
