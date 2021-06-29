package org.openhab.binding.switchbot.internal.config;

/**
 * Contains the configuration required to access a Switchbot Account.
 */
public class SwitchbotAccountConfig {

    private String authorizationOpenToken;

    public String getAuthorizationOpenToken() {
        return authorizationOpenToken;
    }

    public void setAuthorizationOpenToken(String authorizationOpenToken) {
        this.authorizationOpenToken = authorizationOpenToken;
    }
}
