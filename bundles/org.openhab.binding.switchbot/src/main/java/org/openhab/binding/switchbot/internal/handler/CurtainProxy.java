package org.openhab.binding.switchbot.internal.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.switchbot.internal.config.CurtainConfig;
import org.openhab.binding.switchbot.internal.discovery.CurtainStatusModel;
import org.openhab.core.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Forms a proxy for a curtain device, a digital twin.
 */
public class CurtainProxy {
    private Logger logger = LoggerFactory.getLogger(CurtainProxy.class);

    private CurtainConfig config;

    private String authorizationOpenToken;

    public CurtainConfig getConfig() {
        return config;
    }

    /**
     * Device id of a group is a concatenation of device ids. For communication, we need the master deviceId (the
     * first).
     *
     * @return the master device id (if a group) or the device id if not in a group
     */
    private String getCommunicationDeviceId() {
        if (config.isGroup()) {
            return config.getDeviceId().split("-")[0];
        } else {
            return config.getDeviceId();
        }
    }

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

    public CurtainProxy(CurtainConfig config, String authorizationOpenToken) {
        this.config = config;
        this.authorizationOpenToken = authorizationOpenToken;
    }

    public void sendCommand(@NonNull String command) throws IOException {
        // curl -H "Authorization: ..." https://api.switch-bot.com/v1.0/devices/.../status

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
                "https://api.switch-bot.com/v1.0/devices/" + getCommunicationDeviceId() + "/commands", headers, stream,
                "application/json", 20000);

        logger.debug("Result from WS call to get /v1.0/devices/{}/command: {}", getCommunicationDeviceId(),
                resultString);

        return;
    }

    public CurtainState getDeviceStatus() throws IOException {
        // curl -H "Authorization: ..." https://api.switch-bot.com/v1.0/devices/.../status

        Properties headers = new Properties();
        headers.setProperty("Authorization", authorizationOpenToken);

        String resultString = HttpUtil.executeUrl("GET",
                "https://api.switch-bot.com/v1.0/devices/" + getCommunicationDeviceId() + "/status", headers, null,
                "application/json", 20000);

        Gson gson = new Gson();
        CurtainStatusModel status = gson.fromJson(resultString, CurtainStatusModel.class);

        logger.debug("Result from WS call to get /v1.0/devices/{}/status: {}", getCommunicationDeviceId(),
                resultString);

        return toCurtainState(status);
    }

    private CurtainState toCurtainState(CurtainStatusModel status) {

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
}
