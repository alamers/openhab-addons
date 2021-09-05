package org.openhab.binding.switchbot.internal.config;

public class SmartfanConfig {
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
        return "MeterConfig [deviceId=" + deviceId + ", refreshInterval=" + refreshInterval + "]";
    }
}
