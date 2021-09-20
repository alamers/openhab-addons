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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
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
 * The {@link SwitchbotHandler} is responsible for handling commands, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * Allows the account handler to place an authorization token for any derived class.
 * Also provides basic refresh handling etc.
 *
 *
 * @author Arjan Lamers - Initial contribution
 */
abstract class SwitchbotHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(SwitchbotHandler.class);

    protected String authorizationOpenToken;

    protected SwitchbotApiProxy apiProxy;

    protected int refreshTime;
    protected ScheduledFuture<?> refreshTask;

    public SwitchbotHandler(Thing thing) {
        super(thing);
    }

    public void setAuthorizationOpenToken(String authorizationOpenToken) {
        this.authorizationOpenToken = authorizationOpenToken;
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            refreshStateAndUpdate();
        } else {
            CommandModel commandModel = CommandModelAdapter.toCommandModel(channelUID, command);
            sendCommandToDevice(commandModel);
        }
    }

    private void sendCommandToDevice(CommandModel commandModel) {
        try {
            apiProxy.sendCommand(commandModel);
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

    protected abstract void updateState(SwitchbotApiStatusModel status);

    public void refreshStateAndUpdate() {
        if (apiProxy != null) {
            try {
                SwitchbotApiStatusModel status = apiProxy.getDeviceStatus();
                updateState(status);
            } catch (IOException e) {
                logger.debug("Error when refreshing state, putting device offline.", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }
        }
    }

    protected void startAutomaticRefresh() {
        Runnable refresher = () -> refreshStateAndUpdate();

        this.refreshTask = scheduler.scheduleWithFixedDelay(refresher, 0, refreshTime, TimeUnit.SECONDS);
        logger.debug("Start automatic refresh at {} seconds", refreshTime);
    }
}
