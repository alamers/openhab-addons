package org.openhab.binding.switchbot.internal.discovery;

public class SwitchbotDevice {

    private String name;
    private String deviceId;

    public boolean discoveryInformationPresent() {
        return deviceId != null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    public String getDeviceId() {
        // TODO Auto-generated method stub
        return deviceId;
    }

}
