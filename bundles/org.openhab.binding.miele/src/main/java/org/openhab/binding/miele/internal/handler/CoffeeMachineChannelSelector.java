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
package org.openhab.binding.miele.internal.handler;

import static org.openhab.binding.miele.internal.MieleBindingConstants.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.openhab.binding.miele.internal.handler.MieleBridgeHandler.DeviceMetaData;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

/**
 * The {@link ApplianceChannelSelector} for coffee machines
 *
 * @author Stephan Esch - Initial contribution
 * @author Jacob Laursen - Added raw channels
 */
public enum CoffeeMachineChannelSelector implements ApplianceChannelSelector {

    PRODUCT_TYPE("productTypeId", "productType", StringType.class, true),
    DEVICE_TYPE("mieleDeviceType", "deviceType", StringType.class, true),
    BRAND_ID("brandId", "brandId", StringType.class, true),
    COMPANY_ID("companyId", "companyId", StringType.class, true),
    STATE_TEXT(STATE_PROPERTY_NAME, STATE_TEXT_CHANNEL_ID, StringType.class, false),
    STATE(null, STATE_CHANNEL_ID, DecimalType.class, false),
    PROGRAM_TEXT(PROGRAM_ID_PROPERTY_NAME, PROGRAM_TEXT_CHANNEL_ID, StringType.class, false) {
        @Override
        public State getState(String s, DeviceMetaData dmd) {
            State state = getTextState(s, dmd, programs, MISSING_PROGRAM_TEXT_PREFIX);
            if (state != null) {
                return state;
            }
            return super.getState(s, dmd);
        }
    },
    PROGRAM(null, PROGRAM_CHANNEL_ID, DecimalType.class, false),
    PROGRAMTYPE("programType", "type", StringType.class, false),
    PROGRAM_PHASE_TEXT(PHASE_PROPERTY_NAME, PHASE_TEXT_CHANNEL_ID, StringType.class, false) {
        @Override
        public State getState(String s, DeviceMetaData dmd) {
            State state = getTextState(s, dmd, phases, MISSING_PHASE_TEXT_PREFIX);
            if (state != null) {
                return state;
            }
            return super.getState(s, dmd);
        }
    },
    PROGRAM_PHASE(RAW_PHASE_PROPERTY_NAME, PHASE_CHANNEL_ID, DecimalType.class, false),
    // lightingStatus signalFailure signalInfo
    DOOR("signalDoor", "door", OpenClosedType.class, false) {
        @Override
        public State getState(String s, DeviceMetaData dmd) {
            if ("true".equals(s)) {
                return getState("OPEN");
            }

            if ("false".equals(s)) {
                return getState("CLOSED");
            }

            return UnDefType.UNDEF;
        }
    },
    SWITCH(null, "switch", OnOffType.class, false);

    private final Logger logger = LoggerFactory.getLogger(CoffeeMachineChannelSelector.class);

    private final static Map<String, String> programs = Collections.<String, String> emptyMap();

    private final static Map<String, String> phases = Collections.<String, String> emptyMap();

    private final String mieleID;
    private final String channelID;
    private final Class<? extends Type> typeClass;
    private final boolean isProperty;

    CoffeeMachineChannelSelector(String propertyID, String channelID, Class<? extends Type> typeClass,
            boolean isProperty) {
        this.mieleID = propertyID;
        this.channelID = channelID;
        this.typeClass = typeClass;
        this.isProperty = isProperty;
    }

    @Override
    public String toString() {
        return mieleID;
    }

    @Override
    public String getMieleID() {
        return mieleID;
    }

    @Override
    public String getChannelID() {
        return channelID;
    }

    @Override
    public boolean isProperty() {
        return isProperty;
    }

    @Override
    public boolean isExtendedState() {
        return false;
    }

    @Override
    public State getState(String s, DeviceMetaData dmd) {
        if (dmd != null) {
            String localizedValue = getMieleEnum(s, dmd);
            if (localizedValue == null) {
                localizedValue = dmd.LocalizedValue;
            }
            if (localizedValue == null) {
                localizedValue = s;
            }

            return getState(localizedValue);
        } else {
            return getState(s);
        }
    }

    public State getState(String s) {
        try {
            Method valueOf = typeClass.getMethod("valueOf", String.class);
            State state = (State) valueOf.invoke(typeClass, s);
            if (state != null) {
                return state;
            }
        } catch (Exception e) {
            logger.error("An exception occurred while converting '{}' into a State", s);
        }

        return null;
    }

    public State getTextState(String s, DeviceMetaData dmd, Map<String, String> valueMap, String prefix) {
        if ("0".equals(s)) {
            return UnDefType.UNDEF;
        }

        if (dmd == null || dmd.LocalizedValue == null || dmd.LocalizedValue.startsWith(prefix)) {
            String text = valueMap.get(s);
            if (text != null) {
                return getState(text);
            }
            if (dmd == null || dmd.LocalizedValue == null) {
                return getState(prefix + s);
            }
        }

        return null;
    }

    public String getMieleEnum(String s, DeviceMetaData dmd) {
        if (dmd.MieleEnum != null) {
            for (Entry<String, JsonElement> enumEntry : dmd.MieleEnum.entrySet()) {
                if (enumEntry.getValue().getAsString().trim().equals(s.trim())) {
                    return enumEntry.getKey();
                }
            }
        }

        return null;
    }
}
