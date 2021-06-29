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

    public static final String COMMAND = "command";

    public static final String CONFIG_DEVICE_ID = "deviceId";
}
