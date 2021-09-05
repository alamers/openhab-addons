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

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.CHANNEL_POWER;

import org.openhab.binding.switchbot.internal.config.BotConfig;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BotHandler} is responsible for handling commands, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class BotHandler extends SwitchbotHandler {

    private Logger logger = LoggerFactory.getLogger(BotHandler.class);

    public BotHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Will boot up Switchbot Bot binding");

        BotConfig config = getThing().getConfiguration().as(BotConfig.class);

        logger.debug("Bot Config: {}", config);

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
    protected void updateState(SwitchbotApiStatusModel state) {
        if (state != null) {
            updateStatus(ThingStatus.ONLINE);
            publishChannels(state);
        } else {
            logger.warn("Bot {} not cloud-enabled, check app settings", apiProxy.getDeviceId());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Bot not cloud-enabled, check app settings");
        }
    }

    private void publishChannels(SwitchbotApiStatusModel state) {
        if (state == null) {
            updateState(CHANNEL_POWER, OnOffType.OFF);
            return;
        }

        boolean power = state.getBody().getPower() == null ? false : state.getBody().getPower().equalsIgnoreCase("on");
        updateState(CHANNEL_POWER, power ? OnOffType.ON : OnOffType.OFF);
    }
}
