/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.io.homekit.internal;

import static org.openhab.io.homekit.internal.HomekitAccessoryType.DUMMY;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps an Item with data derived from supported tags defined.
 *
 * @author Andy Lintner - Initial contribution
 */
@NonNullByDefault
public class HomekitTaggedItem {
    private final Logger logger = LoggerFactory.getLogger(HomekitTaggedItem.class);

    /** configuration keywords at items level **/
    public final static String MIN_VALUE = "minValue";
    public final static String MAX_VALUE = "maxValue";
    public final static String STEP = "step";
    public final static String DIMMER_MODE = "dimmerMode";
    public final static String DELAY = "commandDelay";
    public final static String INVERTED = "inverted";
    public final static String PRIMARY_SERVICE = "primary";
    public final static String INSTANCE = "instance";

    private static final Map<Integer, String> CREATED_ACCESSORY_IDS = new ConcurrentHashMap<>();

    // proxy item used to group commands for complex item types like Color or Dimmer
    private final HomekitOHItemProxy proxyItem;

    // type of HomeKit accessory/service, e.g. TemperatureSensor
    private final HomekitAccessoryType homekitAccessoryType;

    // type of HomeKit characteristic, e.g. CurrentTemperature
    private HomekitCharacteristicType homekitCharacteristicType;

    // configuration attached to the openHAB Item, e.g. minValue, maxValue, valveType
    private final @Nullable Map<String, Object> configuration;

    // link to the groupItem if item is part of a group
    private @Nullable GroupItem parentGroupItem;

    // HomeKit accessory id (aid) which is generated from item name
    private final int id;

    public HomekitTaggedItem(HomekitOHItemProxy item, HomekitAccessoryType homekitAccessoryType,
            @Nullable Map<String, Object> configuration) {
        this.proxyItem = item;
        this.parentGroupItem = null;
        this.configuration = configuration;
        this.homekitAccessoryType = homekitAccessoryType;
        this.homekitCharacteristicType = HomekitCharacteristicType.EMPTY;
        if (homekitAccessoryType != DUMMY) {
            this.id = calculateId(item.getItem());
        } else {
            this.id = 0;
        }
        parseConfiguration();
    }

    public HomekitTaggedItem(HomekitOHItemProxy item, HomekitAccessoryType homekitAccessoryType,
            HomekitCharacteristicType homekitCharacteristicType, @Nullable Map<String, Object> configuration) {
        this(item, homekitAccessoryType, configuration);
        this.homekitCharacteristicType = homekitCharacteristicType;
    }

    public HomekitTaggedItem(HomekitOHItemProxy item, HomekitAccessoryType homekitAccessoryType,
            HomekitCharacteristicType homekitCharacteristicType, @Nullable GroupItem parentGroup,
            @Nullable Map<String, Object> configuration) {
        this(item, homekitAccessoryType, homekitCharacteristicType, configuration);
        this.parentGroupItem = parentGroup;
    }

    public boolean isGroup() {
        return (isAccessory() && (proxyItem.getItem() instanceof GroupItem)
                && ((GroupItem) proxyItem.getItem()).getBaseItem() == null);
    }

    public HomekitAccessoryType getAccessoryType() {
        return homekitAccessoryType;
    }

    public HomekitCharacteristicType getCharacteristicType() {
        return homekitCharacteristicType;
    }

    public @Nullable Map<String, Object> getConfiguration() {
        return configuration;
    }

    /**
     * Returns whether or not this item refers to an item that fully specifies a HomeKit accessory. Mutually
     * exclusive
     * to isCharacteristic(). Primary devices must belong to a root accessory group.
     */
    public boolean isAccessory() {
        return homekitAccessoryType != DUMMY;
    }

    /**
     * Returns whether or not this item is in a group that specifies a HomeKit accessory. It is not possible to be a
     * characteristic and an accessory. Further, all characteristics belong to a
     * root deviceGroup.
     */
    public boolean isCharacteristic() {
        return homekitCharacteristicType != HomekitCharacteristicType.EMPTY;
    }

    /**
     * return openHAB item responsible for the HomeKit item
     * 
     * @return openHAB item
     */
    public Item getItem() {
        return proxyItem.getItem();
    }

    /**
     * return the base item for a group, or the item itself, in order to do type checks
     */
    public Item getBaseItem() {
        return HomekitOHItemProxy.getBaseItem(proxyItem.getItem());
    }

    /**
     * return proxy item which is used to group commands.
     * 
     * @return proxy item
     */
    public HomekitOHItemProxy getProxyItem() {
        return proxyItem;
    }

    /**
     * Send DecimalType command to a NumberItem (or a Group:Number)
     * 
     * @param command
     */
    public void send(DecimalType command) {
        if (getItem() instanceof GroupItem && getBaseItem() instanceof NumberItem) {
            ((GroupItem) getItem()).send(command);
            return;
        } else if (getItem() instanceof NumberItem) {
            ((NumberItem) getItem()).send(command);
            return;
        }
        logger.warn("Received DecimalType command for item {} that doesn't support it. This is probably a bug.",
                getName());
    }

    /**
     * Send OnOffType command to a SwitchItem (or a Group:Switch)
     * 
     * @param command
     */
    public void send(OnOffType command) {
        if (getItem() instanceof GroupItem && getBaseItem() instanceof SwitchItem) {
            ((GroupItem) getItem()).send(command);
            return;
        } else if (getItem() instanceof SwitchItem) {
            ((SwitchItem) getItem()).send(command);
            return;
        }
        logger.warn("Received OnOffType command for item {} that doesn't support it. This is probably a bug.",
                getName());
    }

    /**
     * Send PercentType command to a DimmerItem or RollershutterItem (or a Group:Dimmer/Group:Rollershutter)
     * 
     * @param command
     */
    public void send(PercentType command) {
        if (getItem() instanceof GroupItem
                && (getBaseItem() instanceof DimmerItem || getBaseItem() instanceof RollershutterItem)) {
            ((GroupItem) getItem()).send(command);
            return;
        } else if (getItem() instanceof DimmerItem) {
            ((DimmerItem) getItem()).send(command);
            return;
        } else if (getItem() instanceof RollershutterItem) {
            ((RollershutterItem) getItem()).send(command);
            return;
        }
        logger.warn("Received PercentType command for item {} that doesn't support it. This is probably a bug.",
                getName());
    }

    /**
     * Send StringType command to a StringItem (or a Group:String)
     * 
     * @param command
     */
    public void send(StringType command) {
        if (getItem() instanceof GroupItem && getBaseItem() instanceof StringItem) {
            ((GroupItem) getItem()).send(command);
            return;
        } else if (getItem() instanceof StringItem) {
            ((StringItem) getItem()).send(command);
            return;
        }
        logger.warn("Received StringType command for item {} that doesn't support it. This is probably a bug.",
                getName());
    }

    /**
     * send openHAB item command via proxy item, which allows to group commands.
     * e.g. sendCommandProxy(hue), sendCommandProxy(brightness) would lead to one openHAB command that updates hue and
     * brightness at once
     *
     * @param commandType type of the command, e.g. HomekitCommandType.HUE_COMMAND
     * @param command command/state
     */
    public void sendCommandProxy(HomekitCommandType commandType, State command) {
        proxyItem.sendCommandProxy(commandType, command);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return proxyItem.getItem().getName();
    }

    /**
     * Returns the RootDevice GroupItem to which this item belongs.
     * Returns null if not in a group.
     */
    public @Nullable GroupItem getRootDeviceGroupItem() {
        return parentGroupItem;
    }

    /**
     * Returns whether or not this item belongs to a HomeKit accessory group.
     *
     * Characteristic devices must belong to a HomeKit accessory group.
     */
    public boolean isMemberOfAccessoryGroup() {
        return parentGroupItem != null;
    }

    /**
     * return object from item configuration for given key or default if not found
     * 
     * @param key configuration key
     * @param defaultValue default value
     * @param <T> expected class
     * @return value
     */
    @SuppressWarnings({ "null", "unchecked" })
    public <T> T getConfiguration(String key, T defaultValue) {
        if (configuration != null) {
            final @Nullable Object value = configuration.get(key);
            if (value != null) {
                if (value.getClass().equals(defaultValue.getClass())) {
                    return (T) value;
                }
                // fix for different handling of numbers via .items and via mainUI, see #1904
                if ((value instanceof BigDecimal) && (defaultValue instanceof Double)) {
                    return (T) Double.valueOf(((BigDecimal) value).doubleValue());
                }
                if ((value instanceof Double) && (defaultValue instanceof BigDecimal)) {
                    return (T) BigDecimal.valueOf(((Double) value).doubleValue());
                }
            }

        }
        return defaultValue;
    }

    /**
     * Returns configuration value as boolean if its exists otherwise returns defaultValue
     *
     * @param key configuration key
     * @param defaultValue default value
     * @return configuration value as boolean
     */
    public boolean getConfigurationAsBoolean(String key, boolean defaultValue) {
        if (configuration == null) {
            return defaultValue;
        }
        final @Nullable Object value = configuration.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            final String valueString = (String) value;
            return valueString.equalsIgnoreCase("yes") || valueString.equalsIgnoreCase("true");
        }
        return defaultValue;
    }

    /**
     * returns true if inverted flag is set, i.e. item has the configuration "inverted=true"
     * 
     * @return true if inverted flag is set to true
     */
    public boolean isInverted() {
        return getConfigurationAsBoolean(HomekitTaggedItem.INVERTED, false);
    }

    /**
     * return configuration as int if exists otherwise return defaultValue
     *
     * @param key configuration key
     * @param defaultValue default value
     * @return value
     */
    public int getConfigurationAsInt(String key, int defaultValue) {
        return getConfiguration(key, BigDecimal.valueOf(defaultValue)).intValue();
    }

    /**
     * return configuration as double if exists otherwise return defaultValue
     * 
     * @param key configuration key
     * @param defaultValue default value
     * @return value
     */
    public double getConfigurationAsDouble(String key, double defaultValue) {
        return getConfiguration(key, BigDecimal.valueOf(defaultValue)).doubleValue();
    }

    /**
     * parse and apply item configuration.
     */
    private void parseConfiguration() {
        if (configuration != null) {
            final @Nullable Object dimmerModeConfig = configuration.get(DIMMER_MODE);
            if (dimmerModeConfig instanceof String) {
                HomekitDimmerMode.valueOfTag((String) dimmerModeConfig).ifPresent(proxyItem::setDimmerMode);
            }
            final @Nullable Object delayConfig = configuration.get(DELAY);
            if (delayConfig instanceof Number) {
                proxyItem.setDelay(((Number) delayConfig).intValue());
            }
        }
    }

    private int calculateId(Item item) {
        // magic number 629 is the legacy from apache HashCodeBuilder (17*37)
        int id = 629 + item.getName().hashCode();
        if (id < 0) {
            id += Integer.MAX_VALUE;
        }
        if (id < 2) {
            id = 2; // 0 and 1 are reserved
        }
        if (CREATED_ACCESSORY_IDS.containsKey(id)) {
            if (!CREATED_ACCESSORY_IDS.get(id).equals(item.getName())) {
                logger.warn(
                        "Could not create HomeKit accessory {} because its hash conflicts with {}. This is a 1:1,000,000 chance occurrence. Change one of the names and consider playing the lottery. See https://github.com/openhab/openhab-addons/issues/257#issuecomment-125886562",
                        item.getName(), CREATED_ACCESSORY_IDS.get(id));
                return 0;
            }
        } else {
            CREATED_ACCESSORY_IDS.put(id, item.getName());
        }
        return id;
    }

    public String toString() {
        return "Item:" + proxyItem.getItem() + "  HomeKit type: '" + homekitAccessoryType.getTag()
                + "' characteristic: '" + homekitCharacteristicType.getTag() + "'";
    }
}
