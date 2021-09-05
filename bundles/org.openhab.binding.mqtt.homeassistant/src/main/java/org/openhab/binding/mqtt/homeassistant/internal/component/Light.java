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
package org.openhab.binding.mqtt.homeassistant.internal.component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.generic.ChannelStateUpdateListener;
import org.openhab.binding.mqtt.generic.mapping.ColorMode;
import org.openhab.binding.mqtt.generic.values.ColorValue;
import org.openhab.binding.mqtt.homeassistant.internal.ComponentChannel;
import org.openhab.binding.mqtt.homeassistant.internal.config.dto.AbstractChannelConfiguration;
import org.openhab.core.io.transport.mqtt.MqttBrokerConnection;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * A MQTT light, following the https://www.home-assistant.io/components/light.mqtt/ specification.
 *
 * This class condenses the three state/command topics (for ON/OFF, Brightness, Color) to one
 * color channel.
 *
 * @author David Graeff - Initial contribution
 */
@NonNullByDefault
public class Light extends AbstractComponent<Light.ChannelConfiguration> implements ChannelStateUpdateListener {
    public static final String switchChannelID = "light"; // Randomly chosen channel "ID"
    public static final String brightnessChannelID = "brightness"; // Randomly chosen channel "ID"
    public static final String colorChannelID = "color"; // Randomly chosen channel "ID"

    /**
     * Configuration class for MQTT component
     */
    static class ChannelConfiguration extends AbstractChannelConfiguration {
        ChannelConfiguration() {
            super("MQTT Light");
        }

        protected int brightness_scale = 255;
        protected boolean optimistic = false;
        protected @Nullable List<String> effect_list;

        // Defines when on the payload_on is sent. Using last (the default) will send any style (brightness, color, etc)
        // topics first and then a payload_on to the command_topic. Using first will send the payload_on and then any
        // style topics. Using brightness will only send brightness commands instead of the payload_on to turn the light
        // on.
        protected String on_command_type = "last";

        protected @Nullable String state_topic;
        protected @Nullable String command_topic;
        protected @Nullable String state_value_template;

        protected @Nullable String brightness_state_topic;
        protected @Nullable String brightness_command_topic;
        protected @Nullable String brightness_value_template;

        protected @Nullable String color_temp_state_topic;
        protected @Nullable String color_temp_command_topic;
        protected @Nullable String color_temp_value_template;

        protected @Nullable String effect_command_topic;
        protected @Nullable String effect_state_topic;
        protected @Nullable String effect_value_template;

        protected @Nullable String rgb_command_topic;
        protected @Nullable String rgb_state_topic;
        protected @Nullable String rgb_value_template;
        protected @Nullable String rgb_command_template;

        protected @Nullable String white_value_command_topic;
        protected @Nullable String white_value_state_topic;
        protected @Nullable String white_value_template;

        protected @Nullable String xy_command_topic;
        protected @Nullable String xy_state_topic;
        protected @Nullable String xy_value_template;

        protected String payload_on = "ON";
        protected String payload_off = "OFF";
    }

    protected ComponentChannel colorChannel;
    protected ComponentChannel switchChannel;
    protected ComponentChannel brightnessChannel;
    private final @Nullable ChannelStateUpdateListener channelStateUpdateListener;

    public Light(ComponentFactory.ComponentConfiguration builder) {
        super(builder, ChannelConfiguration.class);
        this.channelStateUpdateListener = builder.getUpdateListener();
        ColorValue value = new ColorValue(ColorMode.RGB, channelConfiguration.payload_on,
                channelConfiguration.payload_off, 100);

        // Create three MQTT subscriptions and use this class object as update listener
        switchChannel = buildChannel(switchChannelID, value, channelConfiguration.getName(), this)
                .stateTopic(channelConfiguration.state_topic, channelConfiguration.state_value_template,
                        channelConfiguration.getValueTemplate())
                .commandTopic(channelConfiguration.command_topic, channelConfiguration.isRetain(),
                        channelConfiguration.getQos())
                .build(false);

        colorChannel = buildChannel(colorChannelID, value, channelConfiguration.getName(), this)
                .stateTopic(channelConfiguration.rgb_state_topic, channelConfiguration.rgb_value_template)
                .commandTopic(channelConfiguration.rgb_command_topic, channelConfiguration.isRetain(),
                        channelConfiguration.getQos())
                .build(false);

        brightnessChannel = buildChannel(brightnessChannelID, value, channelConfiguration.getName(), this)
                .stateTopic(channelConfiguration.brightness_state_topic, channelConfiguration.brightness_value_template)
                .commandTopic(channelConfiguration.brightness_command_topic, channelConfiguration.isRetain(),
                        channelConfiguration.getQos())
                .build(false);

        channels.put(colorChannelID, colorChannel);
    }

    @Override
    public CompletableFuture<@Nullable Void> start(MqttBrokerConnection connection, ScheduledExecutorService scheduler,
            int timeout) {
        return Stream.of(switchChannel, brightnessChannel, colorChannel) //
                .map(v -> v.start(connection, scheduler, timeout)) //
                .reduce(CompletableFuture.completedFuture(null), (f, v) -> f.thenCompose(b -> v));
    }

    @Override
    public CompletableFuture<@Nullable Void> stop() {
        return Stream.of(switchChannel, brightnessChannel, colorChannel) //
                .map(v -> v.stop()) //
                .reduce(CompletableFuture.completedFuture(null), (f, v) -> f.thenCompose(b -> v));
    }

    /**
     * Proxy method to condense all three MQTT subscriptions to one channel
     */
    @Override
    public void updateChannelState(ChannelUID channelUID, State value) {
        ChannelStateUpdateListener listener = channelStateUpdateListener;
        if (listener != null) {
            listener.updateChannelState(colorChannel.getChannelUID(), value);
        }
    }

    /**
     * Proxy method to condense all three MQTT subscriptions to one channel
     */
    @Override
    public void postChannelCommand(ChannelUID channelUID, Command value) {
        ChannelStateUpdateListener listener = channelStateUpdateListener;
        if (listener != null) {
            listener.postChannelCommand(colorChannel.getChannelUID(), value);
        }
    }

    /**
     * Proxy method to condense all three MQTT subscriptions to one channel
     */
    @Override
    public void triggerChannel(ChannelUID channelUID, String eventPayload) {
        ChannelStateUpdateListener listener = channelStateUpdateListener;
        if (listener != null) {
            listener.triggerChannel(colorChannel.getChannelUID(), eventPayload);
        }
    }
}
