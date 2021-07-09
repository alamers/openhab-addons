package org.openhab.binding.switchbot.internal.discovery;

/**
 * Represents a discovered Curtain device.
 * Curtain devices can be grouped. A specific CurtainDevice is created for the group, while the individual Curtain
 * devices are also available as non-grouped devices.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class CurtainDevice extends SwitchbotDevice {

    private boolean group;

    public CurtainDevice(String name, String deviceId, boolean group) {
        super(name, deviceId, DeviceType.CURTAIN);
        this.group = group;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }
}
