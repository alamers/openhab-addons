package org.openhab.binding.switchbot.internal.discovery;

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.*;

import org.openhab.core.thing.ThingTypeUID;

public class SwitchbotDevice {

    private String name;
    private String deviceId;

    private DeviceType deviceType;

    public enum DeviceType {
        CURTAIN(THING_TYPE_CURTAIN),
        BOT(THING_TYPE_BOT),
        PLUG(THING_TYPE_PLUG),
        METER(THING_TYPE_METER),
        HUMIDIFIER(THING_TYPE_HUMIDIFIER),
        SMARTFAN(THING_TYPE_SMARTFAN),
        HUB(THING_TYPE_HUB);

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
