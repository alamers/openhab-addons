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
package org.openhab.binding.nikohomecontrol.internal.protocol;

import java.net.InetAddress;
import java.time.ZoneId;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link NhcControllerEvent} interface is used to get configuration information and to pass alarm or notice events
 * received from the Niko Home Control controller to the consuming client. It is designed to pass events to openHAB
 * handlers that implement this interface. Because of the design, the
 * org.openhab.binding.nikohomecontrol.internal.protocol package can be extracted and used independent of openHAB.
 *
 * @author Mark Herwege - Initial Contribution
 */
@NonNullByDefault
public interface NhcControllerEvent {

    /**
     * Get the IP-address of the Niko Home Control IP-interface.
     *
     * @return the addr
     */
    public default @Nullable InetAddress getAddr() {
        return null;
    }

    /**
     * Get the listening port of the Niko Home Control IP-interface.
     *
     * @return the port
     */
    public default int getPort() {
        return 0;
    }

    /**
     * Get the profile for the hobby API in the Niko Home Control II system.
     *
     * @return the profile
     */
    public default String getProfile() {
        return "";
    }

    /**
     * Get the JWT Token of the Niko Home Control II system.
     *
     * @return the token
     */
    public default String getToken() {
        return "";
    }

    /**
     * Get the time zone ID of the Niko Home Control system.
     *
     * @return the zone ID
     */
    public ZoneId getTimeZone();

    /**
     * Called to indicate the connection with the Niko Home Control Controller is offline.
     *
     * @param message
     */
    public void controllerOffline(String message);

    /**
     * Called to indicate the connection with the Niko Home Control Controller is online.
     *
     */
    public void controllerOnline();

    /**
     * This method is called when an alarm event is received from the Niko Home Control controller.
     *
     * @param alarmText
     */
    public void alarmEvent(String alarmText);

    /**
     * This method is called when a notice event is received from the Niko Home Control controller.
     *
     * @param alarmText
     */
    public void noticeEvent(String noticeText);

    /**
     * This method is called when properties are updated from the Niko Home Control controller.
     */
    public void updatePropertiesEvent();
}
