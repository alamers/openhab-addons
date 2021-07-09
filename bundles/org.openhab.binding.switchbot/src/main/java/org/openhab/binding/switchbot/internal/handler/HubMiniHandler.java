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

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.*;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.switchbot.internal.SwitchbotBindingConstants;
import org.openhab.binding.switchbot.internal.config.CurtainConfig;
import org.openhab.binding.switchbot.internal.handler.CurtainProxy.CurtainState;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
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
public class HubMiniHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(HubMiniHandler.class);

    private CurtainProxy curtainProxy;

    private int refreshTime;
    private ScheduledFuture<?> refreshTask;

    private String authorizationOpenToken;

    public HubMiniHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            refreshStateAndUpdate();
        } else if (channelUID.getId().equals(SwitchbotBindingConstants.COMMAND)) {
            sendCommandToDevice(command);
        }
    }

    private void sendCommandToDevice(Command command) {
        logger.debug("Ok - will handle command for CHANNEL_COMMAND");

        try {
            curtainProxy.sendCommand(command.toString());
        } catch (IOException e) {
            logger.debug("Error while processing command from openHAB.", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
        this.refreshStateAndUpdate();
    }

    @Override
    public void dispose() {
        logger.debug("Running dispose()");
        if (this.refreshTask != null) {
            this.refreshTask.cancel(true);
            this.refreshTask = null;
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Will boot up Switchbot Curtain binding");

        CurtainConfig config = getThing().getConfiguration().as(CurtainConfig.class);

        logger.debug("Curtain Config: {}", config);

        refreshTime = config.getRefreshInterval();
        if (refreshTime < 30) {
            logger.warn(
                    "Refresh time [{}] is not valid. Refresh time must be at least 30 seconds.  Setting to minimum of 30 sec",
                    refreshTime);
            config.setRefreshInterval(30);
        }

        curtainProxy = new CurtainProxy(config, authorizationOpenToken);
        startAutomaticRefresh();
    }

    public void refreshStateAndUpdate() {
        if (curtainProxy != null) {
            try {
                CurtainState curtainState = curtainProxy.getDeviceStatus();
                if (curtainState != null) {
                    updateStatus(ThingStatus.ONLINE);
                    publishChannels(curtainState);
                } else {
                    logger.warn("Curtain {} not cloud-enabled, check app settings",
                            curtainProxy.getConfig().getDeviceId());
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                            "Curtain not cloud-enabled, check app settings");
                }
            } catch (IOException e) {
                logger.debug("Error when refreshing state, putting device offline.", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }
        }
    }

    private void startAutomaticRefresh() {
        Runnable refresher = () -> refreshStateAndUpdate();

        this.refreshTask = scheduler.scheduleWithFixedDelay(refresher, 0, refreshTime, TimeUnit.SECONDS);
        logger.debug("Start automatic refresh at {} seconds", refreshTime);
    }

    private void publishChannels(CurtainState curtainState) {
        if (curtainState == null) {
            updateState(CHANNEL_CALIBRATE, OnOffType.OFF);
            updateState(CHANNEL_GROUP, OnOffType.OFF);
            updateState(CHANNEL_MOVING, OnOffType.OFF);
            updateState(CHANNEL_SLIDE_POSITION, new DecimalType(42));
            return;
        }

        updateState(CHANNEL_CALIBRATE, curtainState.isCalibrate() ? OnOffType.ON : OnOffType.OFF);
        updateState(CHANNEL_GROUP, curtainState.isGroup() ? OnOffType.ON : OnOffType.OFF);
        updateState(CHANNEL_MOVING, curtainState.isMoving() ? OnOffType.ON : OnOffType.OFF);
        updateState(CHANNEL_SLIDE_POSITION, new DecimalType(curtainState.getSlidePosition()));
    }

    public void setAuthorizationOpenToken(String authorizationOpenToken) {
        this.authorizationOpenToken = authorizationOpenToken;
    }
}
