package org.openhab.binding.switchbot.internal.handler;

public class CommandModel {

    private String command;
    private String parameter;
    private String commandType;

    public static CommandModel TURN_OFF = new CommandModel("turnOff", "default", "command");
    public static CommandModel TURN_ON = new CommandModel("turnOn", "default", "command");

    public CommandModel(String command, String parameter, String commandType) {
        this.command = command;
        this.parameter = parameter;
        this.commandType = commandType;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
}
