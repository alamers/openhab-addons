package org.openhab.binding.switchbot.internal.handler;

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.*;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.Command;

public class CommandModelAdapter {

    public static CommandModel toCommandModel(@NonNull ChannelUID channelUID, @NonNull Command command) {

        switch (channelUID.getId()) {
            case CHANNEL_COMMAND:
                return fromCommand(command.toString());
            case CHANNEL_SLIDE_POSITION:
                return fromSlidePosition(command);
            case CHANNEL_MODE:
                return fromStringValue("mode", command);
            case CHANNEL_FAN_STATUS:
                return fromStringValue("setAllStatus", command);
            case CHANNEL_BRIGHTNESS:
                return fromDecimalValue("setBrightness", command);
            case CHANNEL_COLOR:
                return fromStringValue("setColor", command);
            case CHANNEL_COLOR_TEMPERATURE:
                return fromStringValue("setColorTemperature", command);

        }
        return null;
    }

    private static CommandModel fromStringValue(String command, @NonNull Command value) {
        return new CommandModel(command, value.toString());
    }

    private static CommandModel fromDecimalValue(String command, @NonNull Command value) {
        return new CommandModel(command, value.toString());
    }

    private static CommandModel fromSlidePosition(@NonNull Command command) {
        String index = "0"; // what does "index" mean? is that master/slave index?
        String mode = "0"; // performance mode
        DecimalType position;
        if (command instanceof DecimalType) {
            position = (DecimalType) command;
        } else {
            throw new RuntimeException("Channel " + CHANNEL_SLIDE_POSITION + " only supports DecimalType");
        }
        return new CommandModel("setPosition", index + "," + mode + "," + position.shortValue());
    }

    private static CommandModel fromCommand(@NonNull String command) {
        switch (command) {
            case COMMAND_TURN_OFF:
            case COMMAND_CLOSE:
                return CommandModel.TURN_OFF;
            case COMMAND_TURN_ON:
            case COMMAND_OPEN:
                return CommandModel.TURN_ON;
            case COMMAND_PRESS:
                return CommandModel.PRESS;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
    }
}
