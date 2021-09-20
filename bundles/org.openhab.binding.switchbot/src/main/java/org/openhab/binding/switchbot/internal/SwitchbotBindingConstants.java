/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.switchbot.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link SwitchbotBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Arjan Lamers - Initial contribution
 */
@NonNullByDefault
public class SwitchbotBindingConstants {

    private static final String BINDING_ID = "switchbot";

    // List of all Thing Type UIDs
    public static final ThingTypeUID BRIDGE_TYPE_SWITCHBOT_ACCOUNT = new ThingTypeUID(BINDING_ID, "switchbotAccount");
    public static final ThingTypeUID THING_TYPE_CURTAIN = new ThingTypeUID(BINDING_ID, "curtain");
    public static final ThingTypeUID THING_TYPE_HUB = new ThingTypeUID(BINDING_ID, "hub");
    public static final ThingTypeUID THING_TYPE_BOT = new ThingTypeUID(BINDING_ID, "bot");
    public static final ThingTypeUID THING_TYPE_PLUG = new ThingTypeUID(BINDING_ID, "plug");
    public static final ThingTypeUID THING_TYPE_METER = new ThingTypeUID(BINDING_ID, "meter");
    public static final ThingTypeUID THING_TYPE_HUMIDIFIER = new ThingTypeUID(BINDING_ID, "humidifier");
    public static final ThingTypeUID THING_TYPE_SMARTFAN = new ThingTypeUID(BINDING_ID, "smartfan");
    public static final ThingTypeUID THING_TYPE_MOTION_SENSOR = new ThingTypeUID(BINDING_ID, "motionsensor");
    public static final ThingTypeUID THING_TYPE_CONTACT_SENSOR = new ThingTypeUID(BINDING_ID, "contactsensor");
    public static final ThingTypeUID THING_TYPE_COLOR_BULB = new ThingTypeUID(BINDING_ID, "colorbulb");

    public static final String COMMAND_TURN_ON = "turnOn";
    public static final String COMMAND_TURN_OFF = "turnOff";
    public static final String COMMAND_OPEN = "open";
    public static final String COMMAND_CLOSE = "close";
    public static final String COMMAND_PRESS = "press";

    public static final String CONFIG_DEVICE_ID = "deviceId";
    public static final String CONFIG_GROUP = "group";

    // generic channels
    public static final String CHANNEL_COMMAND = "command";

    // curtain channels
    public static final String CHANNEL_CALIBRATE = "calibrate";
    public static final String CHANNEL_MOVING = "moving";
    public static final String CHANNEL_GROUP = "group";
    public static final String CHANNEL_SLIDE_POSITION = "slide-position";

    // bot channels
    public static final String CHANNEL_POWER = "power";

    // humidifier channels
    public static final String CHANNEL_MODE = "mode";

    // fan channels
    public static final String CHANNEL_FAN_STATUS = "fan-status";

    // bulb channels
    public static final String CHANNEL_BRIGHTNESS = "brightness";
    public static final String CHANNEL_COLOR = "color";
    public static final String CHANNEL_COLOR_TEMPERATURE = "color-temperature";
}
