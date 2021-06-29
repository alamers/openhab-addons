# Switchbot Binding

This binding provides access to Switchbot devices via a Switchbot Account bridge. (An alternative route via direct bluetooth access could be implemented but is not yet done).


## Supported Things

This supports Switchbot Curtain and Switchbot Hub Mini (as that is all I can test). Other devices should be easy to add but I have no way of testing it.


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

_Describe what is needed to manually configure a thing, either through the UI or via a thing-file. This should be mainly about its mandatory and optional configuration parameters. A short example entry for a thing file can help!_

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

| channel  | type   | description                  |
|----------|--------|------------------------------|
| control  | Switch | This is the control channel  |

## Full Example

_Provide a full usage example based on textual configuration files (*.things, *.items, *.sitemap)._

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
