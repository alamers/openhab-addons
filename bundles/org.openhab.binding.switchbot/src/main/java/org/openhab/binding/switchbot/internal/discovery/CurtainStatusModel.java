package org.openhab.binding.switchbot.internal.discovery;

/**
 * Represents the json model of the status call.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class CurtainStatusModel {

    /*
     * Example json body from https://github.com/OpenWonderLabs/SwitchBotAPI
     * {
     * "statusCode": 100,
     * "body": {
     * "deviceId": "E2F6032048AB",
     * "deviceType": "Curtain",
     * "hubDeviceId": "FA7310762361",
     * "calibrate": true,
     * "group": false,
     * "moving": false,
     * "slidePosition": 0
     * },
     * "message": "success"
     * }
     */

    private int statusCode;
    private String message;
    private Body body;

    public static class Body {
        private String deviceId;
        private String deviceType;
        private String hubDeviceId;
        private Boolean calibrate;
        private Boolean group;
        private Boolean moving;
        private Integer slidePosition;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getHubDeviceId() {
            return hubDeviceId;
        }

        public void setHubDeviceId(String hubDeviceId) {
            this.hubDeviceId = hubDeviceId;
        }

        public Boolean getCalibrate() {
            return calibrate;
        }

        public void setCalibrate(Boolean calibrate) {
            this.calibrate = calibrate;
        }

        public Boolean getGroup() {
            return group;
        }

        public void setGroup(Boolean group) {
            this.group = group;
        }

        public Boolean getMoving() {
            return moving;
        }

        public void setMoving(Boolean moving) {
            this.moving = moving;
        }

        public Integer getSlidePosition() {
            return slidePosition;
        }

        public void setSlidePosition(Integer slidePosition) {
            this.slidePosition = slidePosition;
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
