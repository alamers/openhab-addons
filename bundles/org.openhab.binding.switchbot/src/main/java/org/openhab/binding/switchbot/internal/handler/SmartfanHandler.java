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

import org.openhab.binding.switchbot.internal.config.SmartfanConfig;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SmartfanHandler} is responsible for handling commands, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class SmartfanHandler extends SwitchbotHandler {

    private Logger logger = LoggerFactory.getLogger(SmartfanHandler.class);

    public SmartfanHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Will boot up Switchbot Smartfan binding");

        SmartfanConfig config = getThing().getConfiguration().as(SmartfanConfig.class);

        logger.debug("Curtain Config: {}", config);

        refreshTime = config.getRefreshInterval();
        if (refreshTime < 30) {
            logger.warn(
                    "Refresh time [{}] is not valid. Refresh time must be at least 30 seconds.  Setting to minimum of 30 sec",
                    refreshTime);
            config.setRefreshInterval(30);
        }

        apiProxy = new SwitchbotApiProxy(config.getDeviceId(), authorizationOpenToken);
        startAutomaticRefresh();
    }

    @Override
    protected void updateState(SwitchbotApiStatusModel status) {
    }
}
