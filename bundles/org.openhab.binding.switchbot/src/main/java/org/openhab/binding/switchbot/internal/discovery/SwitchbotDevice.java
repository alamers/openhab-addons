package org.openhab.binding.switchbot.internal.discovery;

public class SwitchbotDevice {

    private String name;
    private String deviceId;
    private DeviceType deviceType;

    public enum DeviceType {
        CURTAIN
    }

    public boolean discoveryInformationPresent() {
        return deviceId != null;
    }

    public SwitchbotDevice(String name, String deviceId, DeviceType deviceType) {
        this.name = name;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
