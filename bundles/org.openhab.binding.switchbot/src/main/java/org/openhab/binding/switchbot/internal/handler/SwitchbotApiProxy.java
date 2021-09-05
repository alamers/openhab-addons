package org.openhab.binding.switchbot.internal.handler;

import static org.openhab.binding.switchbot.internal.SwitchbotBindingConstants.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.core.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Forms a proxy for the Switchbot API, for any device.
 * Is stateful per device since it holds a device id.
 */
public class SwitchbotApiProxy {
    private Logger logger = LoggerFactory.getLogger(SwitchbotApiProxy.class);

    private final String authorizationOpenToken;
    private final String deviceId;

    public SwitchbotApiProxy(String deviceId, String authorizationOpenToken) {
        this.authorizationOpenToken = authorizationOpenToken;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void sendCommand(@NonNull String command) throws IOException {
        Properties headers = new Properties();
        headers.setProperty("Authorization", authorizationOpenToken);

        CommandModel commandModel;
        switch (command) {
            case COMMAND_TURN_OFF:
            case COMMAND_CLOSE:
                commandModel = CommandModel.TURN_OFF;
                break;
            case COMMAND_TURN_ON:
            case COMMAND_OPEN:
                commandModel = CommandModel.TURN_ON;
                break;
            case COMMAND_PRESS:
                commandModel = CommandModel.PRESS;
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
        Gson gson = new Gson();
        String commandJson = gson.toJson(commandModel);
        InputStream stream = new ByteArrayInputStream(commandJson.getBytes(StandardCharsets.UTF_8));

        String resultString = HttpUtil.executeUrl("POST",
                "https://api.switch-bot.com/v1.0/devices/" + deviceId + "/commands", headers, stream,
                "application/json", 20000);

        logger.debug("Result from WS call to get /v1.0/devices/{}/command: {}", deviceId, resultString);

        return;
    }

    public SwitchbotApiStatusModel getDeviceStatus() throws IOException {
        Properties headers = new Properties();
        headers.setProperty("Authorization", authorizationOpenToken);

        String resultString = HttpUtil.executeUrl("GET",
                "https://api.switch-bot.com/v1.0/devices/" + deviceId + "/status", headers, null, "application/json",
                20000);
        logger.debug("Result from WS call to get /v1.0/devices/{}/status: {}", deviceId, resultString);

        Gson gson = new Gson();
        return gson.fromJson(resultString, SwitchbotApiStatusModel.class);
    }
}
