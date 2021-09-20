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

import org.openhab.binding.switchbot.internal.config.CurtainConfig;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CurtainHandler} is responsible for handling commands, which are
 * sent to one of the channels. It maps the OpenHAB world to the Switchbot world.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class CurtainHandler extends SwitchbotHandler {

    private Logger logger = LoggerFactory.getLogger(CurtainHandler.class);

    /** internal representation of the curtain state. */
    public static class CurtainState {
        boolean calibrate;
        boolean group;
        boolean moving;
        int slidePosition;

        public boolean isCalibrate() {
            return calibrate;
        }

        public void setCalibrate(boolean calibrate) {
            this.calibrate = calibrate;
        }

        public boolean isGroup() {
            return group;
        }

        public void setGroup(boolean group) {
            this.group = group;
        }

        public boolean isMoving() {
            return moving;
        }

        public void setMoving(boolean moving) {
            this.moving = moving;
        }

        public int getSlidePosition() {
            return slidePosition;
        }

        public void setSlidePosition(int slidePosition) {
            this.slidePosition = slidePosition;
        }
    }

    public CurtainHandler(Thing thing) {
        super(thing);
    }

    @Override
    protected void updateState(SwitchbotApiStatusModel status) {
        CurtainState curtainState = toCurtainState(status);
        if (curtainState != null) {
            updateStatus(ThingStatus.ONLINE);
            publishChannels(curtainState);
        } else {
            logger.warn("Curtain {} not cloud-enabled, check app settings", apiProxy.getDeviceId());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Curtain not cloud-enabled, check app settings");
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Will boot up Switchbot Curtain binding");

        CurtainConfig config = getThing().getConfiguration().as(CurtainConfig.class);

        logger.debug("Curtain Config: {}", config);

        refreshTime = config.getRefreshInterval();
        if (refreshTime < 1) {
            logger.warn(
                    "Refresh time [{}] is not valid. Refresh time must be at least 1 second.  Setting to minimum of 30 sec",
                    refreshTime);
            config.setRefreshInterval(1);
        }

        apiProxy = new SwitchbotApiProxy(getCommunicationDeviceId(config), authorizationOpenToken);
        startAutomaticRefresh();
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

    private CurtainState toCurtainState(SwitchbotApiStatusModel status) {

        if (status.getBody().getCalibrate() == null) {
            // probably not cloud enabled so no real curtain state available
            return null;
        }

        CurtainState curtainState = new CurtainState();
        curtainState.setCalibrate(status.getBody().getCalibrate());
        curtainState.setGroup(status.getBody().getGroup());
        curtainState.setMoving(status.getBody().getMoving());
        curtainState.setSlidePosition(status.getBody().getSlidePosition());

        return curtainState;
    }

    /**
     * Device id of a group is a concatenation of device ids. For communication, we need the master deviceId (the
     * first).
     *
     * @param config
     *
     * @return the master device id (if a group) or the device id if not in a group
     */
    private String getCommunicationDeviceId(CurtainConfig config) {
        if (config.isGroup()) {
            return config.getDeviceId().split("-")[0];
        } else {
            return config.getDeviceId();
        }
    }
}
