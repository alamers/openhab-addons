/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.alarm.internal.config;

/**
 * The alarm zone configuration.
 *
 * @author Gerhard Riegler - Initial contribution
 */
public class AlarmZoneConfig {
    private String type;
    private String closedMapping;

    /**
     * Returns the alarm zone type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the alarm zone type.
     */
    public void setType(String type) {
        this.type = type.toUpperCase();
    }

    /**
     * Returns the closed mapping strings.
     */
    public String getClosedMapping() {
        return closedMapping;
    }

    /**
     * Sets the closed mapping strings.
     */
    public void setClosedMapping(String closedMapping) {
        this.closedMapping = closedMapping;
    }
}
