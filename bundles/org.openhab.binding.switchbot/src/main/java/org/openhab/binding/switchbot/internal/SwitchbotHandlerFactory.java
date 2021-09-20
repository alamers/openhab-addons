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
package org.openhab.binding.switchbot.internal;

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.switchbot.internal.discovery.SwitchbotAccountDiscoveryService;
import org.openhab.binding.switchbot.internal.handler.BotHandler;
import org.openhab.binding.switchbot.internal.handler.ColorBulbHandler;
import org.openhab.binding.switchbot.internal.handler.ContactSensorHandler;
import org.openhab.binding.switchbot.internal.handler.CurtainHandler;
import org.openhab.binding.switchbot.internal.handler.HubHandler;
import org.openhab.binding.switchbot.internal.handler.HumidifierHandler;
import org.openhab.binding.switchbot.internal.handler.MeterHandler;
import org.openhab.binding.switchbot.internal.handler.MotionSensorHandler;
import org.openhab.binding.switchbot.internal.handler.PlugHandler;
import org.openhab.binding.switchbot.internal.handler.SmartfanHandler;
import org.openhab.binding.switchbot.internal.handler.SwitchbotAccountHandler;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link SwitchbotHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Arjan Lamers - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.switchbot", service = ThingHandlerFactory.class)
public class SwitchbotHandlerFactory extends BaseThingHandlerFactory {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPE_UIDS = Collections
            .unmodifiableSet(Stream
                    .of(BRIDGE_TYPE_SWITCHBOT_ACCOUNT, THING_TYPE_CURTAIN, THING_TYPE_HUB, THING_TYPE_BOT,
                            THING_TYPE_PLUG, THING_TYPE_METER, THING_TYPE_HUMIDIFIER, THING_TYPE_SMARTFAN,
                            THING_TYPE_CONTACT_SENSOR, THING_TYPE_MOTION_SENSOR, THING_TYPE_COLOR_BULB)
                    .collect(Collectors.toSet()));

    public static final Set<ThingTypeUID> DISCOVERABLE_THING_TYPE_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_CURTAIN, THING_TYPE_HUB, THING_TYPE_BOT, THING_TYPE_PLUG,
                    THING_TYPE_METER, THING_TYPE_HUMIDIFIER, THING_TYPE_SMARTFAN, THING_TYPE_CONTACT_SENSOR,
                    THING_TYPE_MOTION_SENSOR, THING_TYPE_COLOR_BULB).collect(Collectors.toSet()));

    private Map<ThingUID, ServiceRegistration<DiscoveryService>> discoveryServiceRegistrations = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPE_UIDS.contains(thingTypeUID);
    }

    @Override
    @Nullable
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_CURTAIN)) {
            return new CurtainHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_HUB)) {
            return new HubHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_BOT)) {
            return new BotHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_PLUG)) {
            return new PlugHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_METER)) {
            return new MeterHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_HUMIDIFIER)) {
            return new HumidifierHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_SMARTFAN)) {
            return new SmartfanHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_CONTACT_SENSOR)) {
            return new ContactSensorHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_MOTION_SENSOR)) {
            return new MotionSensorHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_COLOR_BULB)) {
            return new ColorBulbHandler(thing);
        } else if (thingTypeUID.equals(BRIDGE_TYPE_SWITCHBOT_ACCOUNT)) {
            SwitchbotAccountHandler handler = new SwitchbotAccountHandler((Bridge) thing);
            registerAccountDiscoveryService(handler);
            return handler;
        }

        return null;
    }

    @Override
    protected void removeHandler(@NonNull ThingHandler thingHandler) {
        ServiceRegistration<DiscoveryService> serviceRegistration = discoveryServiceRegistrations
                .get(thingHandler.getThing().getUID());

        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
    }

    private void registerAccountDiscoveryService(SwitchbotAccountHandler handler) {
        SwitchbotAccountDiscoveryService discoveryService = new SwitchbotAccountDiscoveryService(handler);

        ServiceRegistration<DiscoveryService> serviceRegistration = this.bundleContext
                .registerService(DiscoveryService.class, discoveryService, null);

        discoveryServiceRegistrations.put(handler.getThing().getUID(), serviceRegistration);
    }
}
