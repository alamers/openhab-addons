package org.openhab.binding.switchbot.internal.config;

public class CurtainConfig {
    private int refreshInterval;
    private String deviceId;

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    @Override
    public String toString() {
        return "CurtainConfig [refreshInterval=" + refreshInterval + ", deviceId=" + deviceId + "]";
    }

}
