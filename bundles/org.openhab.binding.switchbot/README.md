# Switchbot Binding

This binding provides access to Switchbot devices via a Switchbot Account bridge. 

(An alternative route via direct bluetooth access could be implemented but is not yet done).
The implementation is largely based on the Neato binding by Patrik Wimnell and Jeff Lauterbach, and adapted from there.


## Supported Things

This supports Switchbot Curtain and Switchbot Hub Mini (as that is all I can test). Other devices should be easy to add but I have no way of testing it.

Current state: 
* curtains are autodiscovered after adding a Switchbot Account bridge
* curtains support turnOff/turnOn (close/open) commands


## Discovery

You need an authentication token (see below) so the binding can access your Switchbot account. All devices will be auto-discovered after that.

## Binding Configuration

Per the documentation at [https://github.com/OpenWonderLabs/SwitchBotAPI](https://github.com/OpenWonderLabs/SwitchBotAPI):

Please follow these steps:

* Download the SwitchBot app on App Store or Google Play Store
* Register a SwitchBot account and log in into your account
* Generate an Open Token within the app 
    * Go to Profile > Preference 
    * Tap App Version 10 times. Developer Options will show up 
    * Tap Developer Options 
    * Tap Get Token

Then, add the `SwitchbotAccountBridge` and configure it with that token.


## Thing Configuration

If you configure a `SwitchbotAccountBridge`, all other devices are auto discovered.

## Channels

For now, only the curtain is implemented.

| channel  | type   | description                  |
|----------|--------|------------------------------|
| calibrate  | Switch | Indicates if the device is calibrated  |
| moving  | Switch | Indicates if the device is currently moving  |
| group  | Switch | Indicates if the device represents multiple devices (is in a group)  |
| slide-position  | Number | The current slide position  |
| command  | String | Sends a command to the curtain (`turnOff`/`open` to open, `turnOn`/`close` to close |


## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
