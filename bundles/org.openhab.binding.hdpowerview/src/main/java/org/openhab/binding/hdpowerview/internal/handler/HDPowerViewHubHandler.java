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
package org.openhab.binding.hdpowerview.internal.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ProcessingException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.hdpowerview.internal.HDPowerViewBindingConstants;
import org.openhab.binding.hdpowerview.internal.HDPowerViewWebTargets;
import org.openhab.binding.hdpowerview.internal.HubMaintenanceException;
import org.openhab.binding.hdpowerview.internal.HubProcessingException;
import org.openhab.binding.hdpowerview.internal.api.responses.Scenes;
import org.openhab.binding.hdpowerview.internal.api.responses.Scenes.Scene;
import org.openhab.binding.hdpowerview.internal.api.responses.Shades;
import org.openhab.binding.hdpowerview.internal.api.responses.Shades.ShadeData;
import org.openhab.binding.hdpowerview.internal.config.HDPowerViewHubConfiguration;
import org.openhab.binding.hdpowerview.internal.config.HDPowerViewShadeConfiguration;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParseException;

/**
 * The {@link HDPowerViewHubHandler} is responsible for handling commands, which
 * are sent to one of the channels.
 *
 * @author Andy Lintner - Initial contribution
 * @author Andrew Fiddian-Green - Added support for secondary rail positions
 */
@NonNullByDefault
public class HDPowerViewHubHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(HDPowerViewHubHandler.class);
    private final HttpClient httpClient;

    private long refreshInterval;
    private long hardRefreshPositionInterval;
    private long hardRefreshBatteryLevelInterval;

    private @Nullable HDPowerViewWebTargets webTargets;
    private @Nullable ScheduledFuture<?> pollFuture;
    private @Nullable ScheduledFuture<?> hardRefreshPositionFuture;
    private @Nullable ScheduledFuture<?> hardRefreshBatteryLevelFuture;

    private final ChannelTypeUID sceneChannelTypeUID = new ChannelTypeUID(HDPowerViewBindingConstants.BINDING_ID,
            HDPowerViewBindingConstants.CHANNELTYPE_SCENE_ACTIVATE);

    public HDPowerViewHubHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        this.httpClient = httpClient;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (RefreshType.REFRESH.equals(command)) {
            requestRefreshShadePositions();
            return;
        }

        Channel channel = getThing().getChannel(channelUID.getId());
        if (channel != null && sceneChannelTypeUID.equals(channel.getChannelTypeUID())) {
            if (OnOffType.ON.equals(command)) {
                try {
                    HDPowerViewWebTargets webTargets = this.webTargets;
                    if (webTargets == null) {
                        throw new ProcessingException("Web targets not initialized");
                    }
                    webTargets.activateScene(Integer.parseInt(channelUID.getId()));
                } catch (HubMaintenanceException e) {
                    // exceptions are logged in HDPowerViewWebTargets
                } catch (NumberFormatException | HubProcessingException e) {
                    logger.debug("Unexpected error {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void initialize() {
        logger.debug("Initializing hub");
        HDPowerViewHubConfiguration config = getConfigAs(HDPowerViewHubConfiguration.class);
        String host = config.host;

        if (host == null || host.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Host address must be set");
            return;
        }

        webTargets = new HDPowerViewWebTargets(httpClient, host);
        refreshInterval = config.refresh;
        hardRefreshPositionInterval = config.hardRefresh;
        hardRefreshBatteryLevelInterval = config.hardRefreshBatteryLevel;
        schedulePoll();
    }

    public @Nullable HDPowerViewWebTargets getWebTargets() {
        return webTargets;
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        stopPoll();
    }

    @Override
    public void dispose() {
        super.dispose();
        stopPoll();
    }

    private void schedulePoll() {
        ScheduledFuture<?> future = this.pollFuture;
        if (future != null) {
            future.cancel(false);
        }
        logger.debug("Scheduling poll for 5000ms out, then every {}ms", refreshInterval);
        this.pollFuture = scheduler.scheduleWithFixedDelay(this::poll, 5000, refreshInterval, TimeUnit.MILLISECONDS);

        future = this.hardRefreshPositionFuture;
        if (future != null) {
            future.cancel(false);
        }
        if (hardRefreshPositionInterval > 0) {
            logger.debug("Scheduling hard position refresh every {} minutes", hardRefreshPositionInterval);
            this.hardRefreshPositionFuture = scheduler.scheduleWithFixedDelay(this::requestRefreshShadePositions, 1,
                    hardRefreshPositionInterval, TimeUnit.MINUTES);
        }

        future = this.hardRefreshBatteryLevelFuture;
        if (future != null) {
            future.cancel(false);
        }
        if (hardRefreshBatteryLevelInterval > 0) {
            logger.debug("Scheduling hard battery level refresh every {} hours", hardRefreshBatteryLevelInterval);
            this.hardRefreshBatteryLevelFuture = scheduler.scheduleWithFixedDelay(
                    this::requestRefreshShadeBatteryLevels, 1, hardRefreshBatteryLevelInterval, TimeUnit.HOURS);
        }
    }

    private synchronized void stopPoll() {
        ScheduledFuture<?> future = this.pollFuture;
        if (future != null) {
            future.cancel(true);
        }
        this.pollFuture = null;

        future = this.hardRefreshPositionFuture;
        if (future != null) {
            future.cancel(true);
        }
        this.hardRefreshPositionFuture = null;

        future = this.hardRefreshBatteryLevelFuture;
        if (future != null) {
            future.cancel(true);
        }
        this.hardRefreshBatteryLevelFuture = null;
    }

    private synchronized void poll() {
        try {
            logger.debug("Polling for state");
            pollShades();
            pollScenes();
        } catch (JsonParseException e) {
            logger.warn("Bridge returned a bad JSON response: {}", e.getMessage());
        } catch (HubProcessingException e) {
            logger.warn("Error connecting to bridge: {}", e.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, e.getMessage());
        } catch (HubMaintenanceException e) {
            // exceptions are logged in HDPowerViewWebTargets
        }
    }

    private void pollShades() throws JsonParseException, HubProcessingException, HubMaintenanceException {
        HDPowerViewWebTargets webTargets = this.webTargets;
        if (webTargets == null) {
            throw new ProcessingException("Web targets not initialized");
        }

        Shades shades = webTargets.getShades();
        if (shades == null) {
            throw new JsonParseException("Missing 'shades' element");
        }

        List<ShadeData> shadesData = shades.shadeData;
        if (shadesData == null) {
            throw new JsonParseException("Missing 'shades.shadeData' element");
        }

        updateStatus(ThingStatus.ONLINE);
        logger.debug("Received data for {} shades", shadesData.size());

        Map<String, ShadeData> idShadeDataMap = getIdShadeDataMap(shadesData);
        Map<Thing, String> thingIdMap = getThingIdMap();
        for (Entry<Thing, String> item : thingIdMap.entrySet()) {
            Thing thing = item.getKey();
            String shadeId = item.getValue();
            ShadeData shadeData = idShadeDataMap.get(shadeId);
            updateShadeThing(shadeId, thing, shadeData);
        }
    }

    private void updateShadeThing(String shadeId, Thing thing, @Nullable ShadeData shadeData) {
        HDPowerViewShadeHandler thingHandler = ((HDPowerViewShadeHandler) thing.getHandler());
        if (thingHandler == null) {
            logger.debug("Shade '{}' handler not initialized", shadeId);
            return;
        }
        if (shadeData == null) {
            logger.debug("Shade '{}' has no data in hub", shadeId);
        } else {
            logger.debug("Updating shade '{}'", shadeId);
        }
        thingHandler.onReceiveUpdate(shadeData);
    }

    private void pollScenes() throws JsonParseException, HubProcessingException, HubMaintenanceException {
        HDPowerViewWebTargets webTargets = this.webTargets;
        if (webTargets == null) {
            throw new ProcessingException("Web targets not initialized");
        }

        Scenes scenes = webTargets.getScenes();
        if (scenes == null) {
            throw new JsonParseException("Missing 'scenes' element");
        }

        List<Scene> sceneData = scenes.sceneData;
        if (sceneData == null) {
            throw new JsonParseException("Missing 'scenes.sceneData' element");
        }
        logger.debug("Received data for {} scenes", sceneData.size());

        Map<String, Channel> idChannelMap = getIdChannelMap();
        for (Scene scene : sceneData) {
            // remove existing scene channel from the map
            String sceneId = Integer.toString(scene.id);
            if (idChannelMap.containsKey(sceneId)) {
                idChannelMap.remove(sceneId);
                logger.debug("Keeping channel for existing scene '{}'", sceneId);
            } else {
                // create a new scene channel
                ChannelUID channelUID = new ChannelUID(getThing().getUID(), sceneId);
                Channel channel = ChannelBuilder.create(channelUID, "Switch").withType(sceneChannelTypeUID)
                        .withLabel(scene.getName()).withDescription("Activates the scene " + scene.getName()).build();
                updateThing(editThing().withChannel(channel).build());
                logger.debug("Creating new channel for scene '{}'", sceneId);
            }
        }

        // remove any previously created channels that no longer exist
        if (!idChannelMap.isEmpty()) {
            logger.debug("Removing {} orphan scene channels", idChannelMap.size());
            List<Channel> allChannels = new ArrayList<>(getThing().getChannels());
            allChannels.removeAll(idChannelMap.values());
            updateThing(editThing().withChannels(allChannels).build());
        }
    }

    private Map<Thing, String> getThingIdMap() {
        Map<Thing, String> ret = new HashMap<>();
        for (Thing thing : getThing().getThings()) {
            String id = thing.getConfiguration().as(HDPowerViewShadeConfiguration.class).id;
            if (id != null && !id.isEmpty()) {
                ret.put(thing, id);
            }
        }
        return ret;
    }

    private Map<String, ShadeData> getIdShadeDataMap(List<ShadeData> shadeData) {
        Map<String, ShadeData> ret = new HashMap<>();
        for (ShadeData shade : shadeData) {
            if (shade.id != 0) {
                ret.put(Integer.toString(shade.id), shade);
            }
        }
        return ret;
    }

    private Map<String, Channel> getIdChannelMap() {
        Map<String, Channel> ret = new HashMap<>();
        for (Channel channel : getThing().getChannels()) {
            if (sceneChannelTypeUID.equals(channel.getChannelTypeUID())) {
                ret.put(channel.getUID().getId(), channel);
            }
        }
        return ret;
    }

    private void requestRefreshShadePositions() {
        Map<Thing, String> thingIdMap = getThingIdMap();
        for (Entry<Thing, String> item : thingIdMap.entrySet()) {
            Thing thing = item.getKey();
            ThingHandler handler = thing.getHandler();
            if (handler instanceof HDPowerViewShadeHandler) {
                ((HDPowerViewShadeHandler) handler).requestRefreshShadePosition();
            } else {
                String shadeId = item.getValue();
                logger.debug("Shade '{}' handler not initialized", shadeId);
            }
        }
    }

    private void requestRefreshShadeBatteryLevels() {
        Map<Thing, String> thingIdMap = getThingIdMap();
        for (Entry<Thing, String> item : thingIdMap.entrySet()) {
            Thing thing = item.getKey();
            ThingHandler handler = thing.getHandler();
            if (handler instanceof HDPowerViewShadeHandler) {
                ((HDPowerViewShadeHandler) handler).requestRefreshShadeBatteryLevel();
            } else {
                String shadeId = item.getValue();
                logger.debug("Shade '{}' handler not initialized", shadeId);
            }
        }
    }
}
