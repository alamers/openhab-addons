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
package org.openhab.binding.evcc.internal.api.dto;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents a loadpoint object of the status response (/api/state).
 * This DTO was written for evcc version 0.91.
 *
 * @author Florian Hotze - Initial contribution
 */
public class Loadpoint {
    // Data types from https://github.com/evcc-io/evcc/blob/master/api/api.go
    // and from https://docs.evcc.io/docs/reference/configuration/messaging/#msg

    @SerializedName("activePhases")
    private int activePhases;

    @SerializedName("chargeCurrent")
    private double chargeCurrent;

    @SerializedName("chargeDuration")
    private long chargeDuration;

    @SerializedName("chargePower")
    private double chargePower;

    @SerializedName("chargeRemainingDuration")
    private long chargeRemainingDuration;

    @SerializedName("chargeRemainingEnergy")
    private double chargeRemainingEnergy;

    @SerializedName("chargedEnergy")
    private double chargedEnergy;

    @SerializedName("charging")
    private boolean charging;

    @SerializedName("connected")
    private boolean connected;

    @SerializedName("connectedDuration")
    private long connectedDuration;

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("hasVehicle")
    private boolean hasVehicle;

    @SerializedName("loadpoint")
    private int loadpoint;

    @SerializedName("maxCurrent")
    private double maxCurrent;

    @SerializedName("minCurrent")
    private double minCurrent;

    @SerializedName("minSoC")
    private int minSoC;

    @SerializedName("mode")
    private String mode;

    @SerializedName("phases")
    private int phases;

    @SerializedName("pvAction")
    private String pvAction;

    @SerializedName("pvRemaining")
    private long pvRemaining;

    @SerializedName("targetSoC")
    private int targetSoC;

    @SerializedName("targetTime")
    private String targetTime;

    @SerializedName("title")
    private String title;

    @SerializedName("vehicleCapacity")
    private long vehicleCapacity;

    @SerializedName("vehicleOdometer")
    private double vehicleOdometer;

    @SerializedName("vehiclePresent")
    private boolean vehiclePresent;

    @SerializedName("vehicleRange")
    private long vehicleRange;

    @SerializedName("vehicleSoC")
    private int vehicleSoC;

    @SerializedName("vehicleTitle")
    private String vehicleTitle;

    /**
     * @return number of active phases
     */
    public int getActivePhases() {
        return activePhases;
    }

    /**
     * @return charge current
     */
    public double getChargeCurrent() {
        return chargeCurrent;
    }

    /**
     * @return charge duration
     */
    public long getChargeDuration() {
        return chargeDuration;
    }

    /**
     * @return charge power
     */
    public double getChargePower() {
        return chargePower;
    }

    /**
     * @return charge remaining duration until the target SoC is reached
     */
    public long getChargeRemainingDuration() {
        return chargeRemainingDuration;
    }

    /**
     * @return charge remaining energy until the target SoC is reached
     */
    public double getChargeRemainingEnergy() {
        return chargeRemainingEnergy;
    }

    /**
     * @return charged energy
     */
    public double getChargedEnergy() {
        return chargedEnergy;
    }

    /**
     * @return whether loadpoint is charging a vehicle
     */
    public boolean getCharging() {
        return charging;
    }

    /**
     * @return whether a vehicle is connected to the loadpoint
     */
    public boolean getConnected() {
        return connected;
    }

    /**
     * @return vehicle connected duration
     */
    public long getConnectedDuration() {
        return connectedDuration;
    }

    /**
     * @return whether loadpoint is enabled
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * @return whether vehicle is configured for loadpoint
     */
    public boolean getHasVehicle() {
        return hasVehicle;
    }

    /**
     * @return loadpoint id
     */
    public int getLoadpoint() {
        return loadpoint;
    }

    /**
     * @return maximum current
     */
    public double getMaxCurrent() {
        return maxCurrent;
    }

    /**
     * @return minimum current
     */
    public double getMinCurrent() {
        return minCurrent;
    }

    /**
     * @return minimum state of charge
     */
    public int getMinSoC() {
        return minSoC;
    }

    /**
     * @return charging mode: off, now, minpv, pv
     */
    public String getMode() {
        return mode;
    }

    /**
     * @return number of enabled phases
     */
    public int getPhases() {
        return phases;
    }

    /**
     * @return the pv action
     */
    public String getPvAction() {
        return pvAction;
    }

    /**
     * @return the pv remaining
     */
    public long getPvRemaining() {
        return pvRemaining;
    }

    /**
     * @return target state of charge (SoC)
     */
    public int getTargetSoC() {
        return targetSoC;
    }

    /**
     * @return target time for the target state of charge
     */
    public String getTargetTime() {
        return targetTime;
    }

    /**
     * @return loadpoint's title/name
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return vehicle's capacity
     */
    public double getVehicleCapacity() {
        return vehicleCapacity;
    }

    /**
     * @return vehicle's odometer
     */
    public double getVehicleOdometer() {
        return vehicleOdometer;
    }

    /**
     * @return whether evcc is able to get data from vehicle
     */
    public boolean getVehiclePresent() {
        return vehiclePresent;
    }

    /**
     * @return vehicle's range
     */
    public long getVehicleRange() {
        return vehicleRange;
    }

    /**
     * @return vehicle's state of charge (SoC)
     */
    public int getVehicleSoC() {
        return vehicleSoC;
    }

    /**
     * @return vehicle's title/name
     */
    public String getVehicleTitle() {
        return vehicleTitle;
    }
}
