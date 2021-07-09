package org.openhab.binding.switchbot.internal.discovery;

/**
 * Represents a discovered Hub Mini device.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class HubMiniDevice extends SwitchbotDevice {

    public HubMiniDevice(String name, String deviceId) {
        super(name, deviceId, DeviceType.HUB_MINI);
    }
}
