/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.binding.evcc.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.ChannelTypeUID;

/**
 * The {@link EvccBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Florian Hotze - Initial contribution
 */
@NonNullByDefault
public class EvccBindingConstants {

    private static final String BINDING_ID = "evcc";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_DEVICE = new ThingTypeUID(BINDING_ID, "device");

    // List of all Channel Type UIDs
    public static final ChannelTypeUID CHANNEL_TYPE_UID_BATTERY_POWER = new ChannelTypeUID(BINDING_ID, "batteryPower");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_BATTERY_SOC = new ChannelTypeUID(BINDING_ID, "batterySoC");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_BATTERY_PRIORITY_SOC = new ChannelTypeUID(BINDING_ID,
            "batteryPrioritySoC");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_GRID_POWER = new ChannelTypeUID(BINDING_ID, "gridPower");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_HOME_POWER = new ChannelTypeUID(BINDING_ID, "homePower");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_PV_POWER = new ChannelTypeUID(BINDING_ID, "pvPower");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_ACTIVE_PHASES = new ChannelTypeUID(BINDING_ID,
            "activePhases");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGE_CURRENT = new ChannelTypeUID(BINDING_ID,
            "chargeCurrent");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGE_DURATION = new ChannelTypeUID(BINDING_ID,
            "chargeDuration");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGE_POWER = new ChannelTypeUID(BINDING_ID,
            "chargePower");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGE_REMAINING_DURATION = new ChannelTypeUID(
            BINDING_ID, "chargeRemainingDuration");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGE_REMAINING_ENERGY = new ChannelTypeUID(
            BINDING_ID, "chargeRemainingEnergy");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGED_ENERGY = new ChannelTypeUID(BINDING_ID,
            "chargedEnergy");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CHARGING = new ChannelTypeUID(BINDING_ID, "charging");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CONNECTED = new ChannelTypeUID(BINDING_ID,
            "vehicleConnected");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_CONNECTED_DURATION = new ChannelTypeUID(BINDING_ID,
            "vehicleConnectedDuration");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_ENABLED = new ChannelTypeUID(BINDING_ID, "enabled");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_HAS_VEHICLE = new ChannelTypeUID(BINDING_ID,
            "hasVehicle");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_MAX_CURRENT = new ChannelTypeUID(BINDING_ID,
            "maxCurrent");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_MIN_CURRENT = new ChannelTypeUID(BINDING_ID,
            "minCurrent");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_MIN_SOC = new ChannelTypeUID(BINDING_ID, "minSoC");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_MODE = new ChannelTypeUID(BINDING_ID, "mode");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_PHASES = new ChannelTypeUID(BINDING_ID, "phases");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_TARGET_SOC = new ChannelTypeUID(BINDING_ID,
            "targetSoC");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_TARGET_TIME = new ChannelTypeUID(BINDING_ID,
            "targetTime");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_TARGET_TIME_ENABLED = new ChannelTypeUID(BINDING_ID,
            "targetTimeEnabled");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_TITLE = new ChannelTypeUID(BINDING_ID, "title");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_VEHICLE_CAPACITY = new ChannelTypeUID(BINDING_ID,
            "vehicleCapacity");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_VEHICLE_ODOMETER = new ChannelTypeUID(BINDING_ID,
            "vehicleOdometer");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_VEHICLE_PRESENT = new ChannelTypeUID(BINDING_ID,
            "vehiclePresent");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_VEHICLE_RANGE = new ChannelTypeUID(BINDING_ID,
            "vehicleRange");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_VEHICLE_SOC = new ChannelTypeUID(BINDING_ID,
            "vehicleSoC");
    public static final ChannelTypeUID CHANNEL_TYPE_UID_LOADPOINT_VEHICLE_TITLE = new ChannelTypeUID(BINDING_ID,
            "vehicleTitle");

    // List of all Channel ids
    public static final String CHANNEL_BATTERY_POWER = "batteryPower";
    public static final String CHANNEL_BATTERY_SOC = "batterySoC";
    public static final String CHANNEL_BATTERY_PRIORITY_SOC = "batteryPrioritySoC";
    public static final String CHANNEL_GRID_POWER = "gridPower";
    public static final String CHANNEL_HOME_POWER = "homePower";
    public static final String CHANNEL_PV_POWER = "pvPower";
    public static final String CHANNEL_LOADPOINT_ACTIVE_PHASES = "activePhases";
    public static final String CHANNEL_LOADPOINT_CHARGE_CURRENT = "chargeCurrent";
    public static final String CHANNEL_LOADPOINT_CHARGE_DURATION = "chargeDuration";
    public static final String CHANNEL_LOADPOINT_CHARGE_POWER = "chargePower";
    public static final String CHANNEL_LOADPOINT_CHARGE_REMAINING_DURATION = "chargeRemainingDuration";
    public static final String CHANNEL_LOADPOINT_CHARGE_REMAINING_ENERGY = "chargeRemainingEnergy";
    public static final String CHANNEL_LOADPOINT_CHARGED_ENERGY = "chargedEnergy";
    public static final String CHANNEL_LOADPOINT_CHARGING = "charging";
    public static final String CHANNEL_LOADPOINT_CONNECTED = "vehicleConnected";
    public static final String CHANNEL_LOADPOINT_CONNECTED_DURATION = "vehicleConnectedDuration";
    public static final String CHANNEL_LOADPOINT_ENABLED = "enabled";
    public static final String CHANNEL_LOADPOINT_HAS_VEHICLE = "hasVehicle";
    public static final String CHANNEL_LOADPOINT_MAX_CURRENT = "maxCurrent";
    public static final String CHANNEL_LOADPOINT_MIN_CURRENT = "minCurrent";
    public static final String CHANNEL_LOADPOINT_MIN_SOC = "minSoC";
    public static final String CHANNEL_LOADPOINT_MODE = "mode";
    public static final String CHANNEL_LOADPOINT_PHASES = "phases";
    public static final String CHANNEL_LOADPOINT_TARGET_SOC = "targetSoC";
    public static final String CHANNEL_LOADPOINT_TARGET_TIME = "targetTime";
    /**
     * Whether a target time is set on loadpoint.
     */
    public static final String CHANNEL_LOADPOINT_TARGET_TIME_ENABLED = "targetTimeEnabled";
    public static final String CHANNEL_LOADPOINT_TITLE = "title";
    public static final String CHANNEL_LOADPOINT_VEHICLE_CAPACITY = "vehicleCapacity";
    public static final String CHANNEL_LOADPOINT_VEHICLE_ODOMETER = "vehicleOdometer";
    public static final String CHANNEL_LOADPOINT_VEHICLE_PRESENT = "vehiclePresent";
    public static final String CHANNEL_LOADPOINT_VEHICLE_RANGE = "vehicleRange";
    public static final String CHANNEL_LOADPOINT_VEHICLE_SOC = "vehicleSoC";
    public static final String CHANNEL_LOADPOINT_VEHICLE_TITLE = "vehicleTitle";

    public static final int CONNECTION_TIMEOUT_MILLISEC = 5000;
    public static final int LONG_CONNECTION_TIMEOUT_MILLISEC = 60000;
    public static final String EVCC_REST_API = "/api/";
}
