package org.openhab.binding.switchbot.internal.discovery;

/**
 * Represents a discovered Bot device.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class BotDevice extends SwitchbotDevice {

    public BotDevice(String name, String deviceId) {
        super(name, deviceId, DeviceType.BOT);
    }
}
