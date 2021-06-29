package org.openhab.binding.switchbot.internal.discovery;

public class StatusModel {

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
        private boolean calibrate;
        private boolean group;
        private boolean moving;
        private int slidePosition;

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
