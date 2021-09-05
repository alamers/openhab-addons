package org.openhab.binding.switchbot.internal.config;

public class BotConfig {
    private int refreshInterval;
    private String deviceId;

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
        return "BotConfig [refreshInterval=" + refreshInterval + ", deviceId=" + deviceId + "]";
    }
}
