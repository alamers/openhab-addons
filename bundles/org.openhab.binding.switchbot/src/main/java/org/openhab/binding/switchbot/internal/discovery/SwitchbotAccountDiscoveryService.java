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
package org.openhab.binding.switchbot.internal.discovery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openhab.binding.switchbot.internal.SwitchbotBindingConstants;
import org.openhab.binding.switchbot.internal.SwitchbotHandlerFactory;
import org.openhab.binding.switchbot.internal.handler.SwitchbotAccountHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SwitchbotAccountDiscoveryService} is responsible for starting the discovery procedure
 * connecting to SwitchBotAPI and discovering all devices.
 *
 * @link https://github.com/OpenWonderLabs/SwitchBotAPI
 * @author Arjan Lamers - initial contribution
 */
public class SwitchbotAccountDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(SwitchbotAccountDiscoveryService.class);

    private static final int TIMEOUT = 15;

    private final SwitchbotAccountHandler handler;
    private final ThingUID bridgeUID;

    private ScheduledFuture<?> scanTask;

    public SwitchbotAccountDiscoveryService(SwitchbotAccountHandler handler) {
        super(SwitchbotHandlerFactory.DISCOVERABLE_THING_TYPE_UIDS, TIMEOUT);
        this.handler = handler;
        this.bridgeUID = handler.getThing().getUID();
    }

    private void getAllDevices() {
        List<SwitchbotDevice> devices = handler.getAllDevices();

        for (SwitchbotDevice device : devices) {
            addThing(device);
        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        getAllDevices();
    }

    @Override
    protected void startScan() {
        if (this.scanTask != null) {
            scanTask.cancel(true);
        }
        this.scanTask = scheduler.schedule(this::getAllDevices, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void stopScan() {
        super.stopScan();

        if (this.scanTask != null) {
            this.scanTask.cancel(true);
            this.scanTask = null;
        }
    }

    private void addThing(SwitchbotDevice device) {
        if (device == null || !device.discoveryInformationPresent()) {
            return;
        }

        logger.debug("addThing(): Adding new Switchbot device ({}) to the inbox", device.getName());

        Map<String, Object> properties = new HashMap<>();
        ThingUID thingUID = new ThingUID(SwitchbotBindingConstants.THING_TYPE_CURTAIN, bridgeUID, device.getDeviceId());

        properties.put(SwitchbotBindingConstants.CONFIG_DEVICE_ID, device.getDeviceId());

        thingDiscovered(DiscoveryResultBuilder.create(thingUID).withLabel(device.getName()).withBridge(bridgeUID)
                .withProperties(properties).build());
    }
}
