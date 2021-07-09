package org.openhab.binding.switchbot.internal.config;

public class CurtainConfig {
    private int refreshInterval;
    private String deviceId;
    boolean group;

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

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
        return "CurtainConfig [refreshInterval=" + refreshInterval + ", deviceId=" + deviceId + "]";
    }
}
