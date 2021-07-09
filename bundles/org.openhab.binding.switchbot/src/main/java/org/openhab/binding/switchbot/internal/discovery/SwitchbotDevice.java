package org.openhab.binding.switchbot.internal.discovery;

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.*;

import org.openhab.core.thing.ThingTypeUID;

public class SwitchbotDevice {

    private String name;
    private String deviceId;

    private DeviceType deviceType;

    public enum DeviceType {
        CURTAIN(THING_TYPE_CURTAIN),
        HUB_MINI(THING_TYPE_HUB_MINI);

        private ThingTypeUID thingType;

        private DeviceType(ThingTypeUID thingType) {
            this.thingType = thingType;
        }

        ThingTypeUID toThingType() {
            return thingType;
        }
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
