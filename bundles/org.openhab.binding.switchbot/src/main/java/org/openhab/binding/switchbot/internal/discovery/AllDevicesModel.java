package org.openhab.binding.switchbot.internal.discovery;

import java.util.List;

/**
 * Represents the JSON response of the GET /devices call.
 *
 * @author Arjan Lamers - Initial contribution
 */
public class AllDevicesModel {

    /*
     * Example body from https://github.com/OpenWonderLabs/SwitchBotAPI
     *
     * {
     * "statusCode": 100,
     * "body": {
     * "deviceList": [
     * {
     * "deviceId": "500291B269BE",
     * "deviceName": "Living Room Humidifier",
     * "deviceType": "Humidifier",
     * "enableCloudService": true,
     * "hubDeviceId": "000000000000"
     * }
     * ],
     * "infraredRemoteList": [
     * {
     * "deviceId": "02-202008110034-13",
     * "deviceName": "Living Room TV",
     * "remoteType": "TV",
     * "hubDeviceId": "FA7310762361"
     * }
     * ]
     * },
     * "message": "success"
     * }
     *
     */

    private int statusCode;
    private String message;
    private Body body;

    public static class Body {
        private List<Device> deviceList;
        private List<InfraredRemote> infraredRemoteList;

        public List<Device> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<Device> deviceList) {
            this.deviceList = deviceList;
        }

        public List<InfraredRemote> getInfraredRemoteList() {
            return infraredRemoteList;
        }

        public void setInfraredRemoteList(List<InfraredRemote> infraredRemoteList) {
            this.infraredRemoteList = infraredRemoteList;
        }
    };

    public static class Device {
        private String deviceId;
        private String deviceName;
        private String deviceType;
        private boolean enableCloudService;
        private String hubDeviceId;
        private List<String> curtainDevicesIds;
        private boolean calibrate;
        private boolean group;
        private boolean master;
        private String openDirection;

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

        public boolean isMaster() {
            return master;
        }

        public void setMaster(boolean master) {
            this.master = master;
        }

        public String getOpenDirection() {
            return openDirection;
        }

        public void setOpenDirection(String openDirection) {
            this.openDirection = openDirection;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public boolean isEnableCloudService() {
            return enableCloudService;
        }

        public void setEnableCloudService(boolean enableCloudService) {
            this.enableCloudService = enableCloudService;
        }

        public String getHubDeviceId() {
            return hubDeviceId;
        }

        public void setHubDeviceId(String hubDeviceId) {
            this.hubDeviceId = hubDeviceId;
        }

        public List<String> getCurtainDevicesIds() {
            return curtainDevicesIds;
        }

        public void setCurtainDevicesIds(List<String> curtainDevicesIds) {
            this.curtainDevicesIds = curtainDevicesIds;
        }
    }

    public static class InfraredRemote {
        private String deviceId;
        private String deviceName;
        private String remoteType;
        private String hubDeviceId;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getRemoteType() {
            return remoteType;
        }

        public void setRemoteType(String remoteType) {
            this.remoteType = remoteType;
        }

        public String getHubDeviceId() {
            return hubDeviceId;
        }

        public void setHubDeviceId(String hubDeviceId) {
            this.hubDeviceId = hubDeviceId;
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
