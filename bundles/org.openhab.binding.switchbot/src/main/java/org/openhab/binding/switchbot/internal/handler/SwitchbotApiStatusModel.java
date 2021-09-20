package org.openhab.binding.switchbot.internal.handler;

/**
 * Represents the json model of the combination of all possible status call responses.
 * Documentation is verbatim from https://github.com/OpenWonderLabs/SwitchBotAPI#get-device-status
 *
 * @author Arjan Lamers - Initial contribution
 */
public class SwitchbotApiStatusModel {

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
        /** device ID */
        private String deviceId;
        /** device type */
        private String deviceType;
        /** device's parent Hub ID */
        private String hubDeviceId;
        /** only available for Bot/Plug/Humidifier devices. ON/OFF state */
        private String power;
        /** only available for Meter/Humidifier devices. humidity percentage */
        private Integer humidity;
        /** only available for Meter/Humidifier devices. temperature in celsius */
        private Float temperature;
        /** only available for Humidifier devices. atomization efficiency */
        private Integer nebulizationEfficiency;
        /** only available for Humidifier devices. determines if a Humidifier is in Auto Mode or not */
        private Boolean auto;
        /** only available for Humidifier devices. determines if a Humidifier's safety lock is on or not */
        private Boolean childLock;
        /** only available for Humidifier devices. determines if a Humidifier is muted or not */
        private Boolean sound;
        /** only available for Curtain devices. determines if a Curtain has been calibrated or not */
        private Boolean calibrate;
        /**
         * only available for Curtain devices. determines if a Curtain is paired with or grouped with another Curtain or
         * not
         */
        private Boolean group;
        /** only available for Curtain devices. determines if a Curtain is moving or not */
        private Boolean moving;
        /**
         * only available for Curtain devices. the percentage of the distance between the calibrated open position and
         * close position that a Curtain has moved to
         */
        private Integer slidePosition;
        /** only available for Smart Fan devices. the fan mode */
        private Integer mode;
        /** only available for Smart Fan devices. the fan speed */
        private Integer speed;
        /** only available for Smart Fan devices. determines if the fan is swinging or not */
        private Boolean shaking;
        /** only available for Smart Fan devices. the fan's swing direciton */
        private Integer shakeCenter;
        /** only available for Smart Fan devices. the fan's swing range, 0~120° */
        private Integer shakeRange;
        /** only available for Motion Sensor, Contact Sensor devices. determines if motion is detected */
        private Boolean moveDetected;
        /**
         * has two definitions:
         * only available for Motion Sensor, Contact Sensor devices. tell the ambient environment is bright or dim.
         * only available for Color Bulb devices: the brightness value, range from 1 to 100
         */
        private String brightness;
        // private Integer brightness;
        /** only available for Contact Sensor devices. open/close/timeOutNotClose */
        private String openState;
        /** only available for Color Bulb devices. the color value, RGB "255:255:255" */
        private String color;
        /** only available for Color Bulb devices. the color temperature value, range from 2700 to 6500 */
        private Integer colorTemperature;
        /** only available for Humidifier devices. determines if the water tank is empty or not */
        private Boolean lackWater;

        public Boolean getMoveDetected() {
            return moveDetected;
        }

        public void setMoveDetected(Boolean moveDetected) {
            this.moveDetected = moveDetected;
        }

        public String getBrightness() {
            return brightness;
        }

        public void setBrightness(String brightness) {
            this.brightness = brightness;
        }

        public String getOpenState() {
            return openState;
        }

        public void setOpenState(String openState) {
            this.openState = openState;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getColorTemperature() {
            return colorTemperature;
        }

        public void setColorTemperature(Integer colorTemperature) {
            this.colorTemperature = colorTemperature;
        }

        public Boolean getLackWater() {
            return lackWater;
        }

        public void setLackWater(Boolean lackWater) {
            this.lackWater = lackWater;
        }

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

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Float getTemperature() {
            return temperature;
        }

        public void setTemperature(Float temperature) {
            this.temperature = temperature;
        }

        public Integer getNebulizationEfficiency() {
            return nebulizationEfficiency;
        }

        public void setNebulizationEfficiency(Integer nebulizationEfficiency) {
            this.nebulizationEfficiency = nebulizationEfficiency;
        }

        public Boolean getAuto() {
            return auto;
        }

        public void setAuto(Boolean auto) {
            this.auto = auto;
        }

        public Boolean getChildLock() {
            return childLock;
        }

        public void setChildLock(Boolean childLock) {
            this.childLock = childLock;
        }

        public Boolean getSound() {
            return sound;
        }

        public void setSound(Boolean sound) {
            this.sound = sound;
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

        public Integer getMode() {
            return mode;
        }

        public void setMode(Integer mode) {
            this.mode = mode;
        }

        public Integer getSpeed() {
            return speed;
        }

        public void setSpeed(Integer speed) {
            this.speed = speed;
        }

        public Boolean getShaking() {
            return shaking;
        }

        public void setShaking(Boolean shaking) {
            this.shaking = shaking;
        }

        public Integer getShakeCenter() {
            return shakeCenter;
        }

        public void setShakeCenter(Integer shakeCenter) {
            this.shakeCenter = shakeCenter;
        }

        public Integer getShakeRange() {
            return shakeRange;
        }

        public void setShakeRange(Integer shakeRange) {
            this.shakeRange = shakeRange;
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
