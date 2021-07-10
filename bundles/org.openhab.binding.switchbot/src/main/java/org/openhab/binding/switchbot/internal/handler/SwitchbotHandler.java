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
package org.openhab.binding.switchbot.internal.handler;

import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.BaseThingHandler;

/**
 * The {@link SwitchbotHandler} is responsible for handling commands, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * Allows the account handler to place an authorization token for any derived class.
 *
 * @author Arjan Lamers - Initial contribution
 */
abstract class SwitchbotHandler extends BaseThingHandler {

    protected String authorizationOpenToken;

    public SwitchbotHandler(Thing thing) {
        super(thing);
    }

    public void setAuthorizationOpenToken(String authorizationOpenToken) {
        this.authorizationOpenToken = authorizationOpenToken;
    }
}
