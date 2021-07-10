package org.openhab.binding.switchbot.internal.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.switchbot.internal.config.HubMiniConfig;
import org.openhab.core.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Forms a proxy for a curtain device, a digital twin.
 */
public class HubMiniProxy {
    private Logger logger = LoggerFactory.getLogger(HubMiniProxy.class);

    private HubMiniConfig config;

    private String authorizationOpenToken;

    public HubMiniConfig getConfig() {
        return config;
    }

    public HubMiniProxy(HubMiniConfig config, String authorizationOpenToken) {
        this.config = config;
        this.authorizationOpenToken = authorizationOpenToken;
    }

    public void sendCommand(@NonNull String command) throws IOException {
        Properties headers = new Properties();
        headers.setProperty("Authorization", authorizationOpenToken);

        CommandModel commandModel;
        switch (command) {
            case "turnOff":
                commandModel = CommandModel.TURN_OFF;
                break;
            case "turnOn":
                commandModel = CommandModel.TURN_ON;
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
        Gson gson = new Gson();
        String commandJson = gson.toJson(commandModel);
        InputStream stream = new ByteArrayInputStream(commandJson.getBytes(StandardCharsets.UTF_8));

        String resultString = HttpUtil.executeUrl("POST",
                "https://api.switch-bot.com/v1.0/devices/" + config.getDeviceId() + "/commands", headers, stream,
                "application/json", 20000);

        logger.debug("Result from WS call to get /v1.0/devices/{}/command: {}", config.getDeviceId(), resultString);

        return;
    }
}
