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

import org.openhab.binding.switchbot.internal.config.HubConfig;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HubHandler} is responsible for handling commands for the Hub Mini / Hub Plus / Hub, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class HubHandler extends SwitchbotHandler {

    private Logger logger = LoggerFactory.getLogger(HubHandler.class);

    public HubHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Will boot up Switchbot Hub binding");

        HubConfig config = getThing().getConfiguration().as(HubConfig.class);

        logger.debug("Hub Config: {}", config);

        apiProxy = new SwitchbotApiProxy(config.getDeviceId(), authorizationOpenToken);
    }

    @Override
    protected void updateState(SwitchbotApiStatusModel status) {
    }
}
