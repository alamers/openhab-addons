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

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.switchbot.internal.SwitchbotBindingConstants;
import org.openhab.binding.switchbot.internal.config.HubMiniConfig;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HubMiniHandler} is responsible for handling commands for the Hub Mini, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class HubMiniHandler extends SwitchbotHandler {

    private Logger logger = LoggerFactory.getLogger(HubMiniHandler.class);

    private HubMiniProxy hubMiniProxy;

    public HubMiniHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            // the hub mini has no state to refresh
        } else if (channelUID.getId().equals(SwitchbotBindingConstants.COMMAND)) {
            sendCommandToDevice(command);
        }
    }

    private void sendCommandToDevice(Command command) {
        logger.debug("Ok - will handle command for CHANNEL_COMMAND");

        try {
            hubMiniProxy.sendCommand(command.toString());
        } catch (IOException e) {
            logger.debug("Error while processing command from openHAB.", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Will boot up Switchbot Hub binding");

        HubMiniConfig config = getThing().getConfiguration().as(HubMiniConfig.class);

        logger.debug("Hub Mini Config: {}", config);

        hubMiniProxy = new HubMiniProxy(config, authorizationOpenToken);
    }
}
