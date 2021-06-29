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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.switchbot.internal.config.SwitchbotAccountConfig;
import org.openhab.binding.switchbot.internal.discovery.AllDevices;
import org.openhab.binding.switchbot.internal.discovery.SwitchbotDevice;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Bridge handler to manage Switchbot Cloud Account.
 *
 * @link https://github.com/OpenWonderLabs/SwitchBotAPI.
 * @author Arjan Lamers - initial contribution
 */
public class SwitchbotAccountHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(SwitchbotAccountHandler.class);

    public SwitchbotAccountHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    private List<SwitchbotDevice> sendGetDevices(String authorizationOpenToken) {
        Properties headers = new Properties();
        // headers.setProperty("Accept", "application/vnd.neato.nucleo.v1");
        headers.setProperty("Authorization", authorizationOpenToken);

        try {
            String resultString = HttpUtil.executeUrl("GET", "https://api.switch-bot.com/devices", headers, null,
                    "application/json", 20000);

            Gson gson = new Gson();
            AllDevices allDevices = gson.fromJson(resultString, AllDevices.class);

            logger.debug("Result from WS call to get /devices: {}", resultString);

            return toSwitchbotDevices(allDevices);
        } catch (IOException e) {
            logger.debug("Error attempting to find robots registered to account", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Error attempting to find robots registered to account");
            return new ArrayList<>();
        }
    }

    private List<SwitchbotDevice> toSwitchbotDevices(AllDevices allDevices) {
        return new ArrayList<>();
    }

    public @NonNull List<SwitchbotDevice> getAllDevices() {
        logger.debug("Attempting to find robots tied to account");
        SwitchbotAccountConfig accountConfig = getConfigAs(SwitchbotAccountConfig.class);
        String authorizationOpenToken = accountConfig.getAuthorizationOpenToken();

        if (authorizationOpenToken != null) {
            return sendGetDevices(authorizationOpenToken);
        }

        return new ArrayList<>();
    }

}
