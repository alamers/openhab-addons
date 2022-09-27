# Sonnen Binding

The binding for sonnen communicates with a sonnen battery.
More information about the sonnen battery can be found [here](https://sonnen.de/).

## Supported Things

| Thing Type    | Description                    |
|---------------|--------------------------------|
| sonnenbattery | Monitoring of a sonnen battery |


## Thing Configuration

Only the parameter `hostIP` is required; this is the IP address of the sonnen battery in your local network.


## Channels

The following channels are yet supported:


| Channel | Type  | Access| Description|
|---------|-------|-------|------------|
|batteryChargingState|Switch|read|Indicates if the Battery is charging at that moment|
|batteryCharging|Number:Energy|read|Indicates the actual current charging the Battery. Otherwise 0.|
|batteryDischargingState|Switch|read|Indicates if the Battery is discharging at that moment|
|batteryDischarging|Number:Energy|read|Indicates the actual current discharging the Battery. Otherwise 0.|
|batteryFeedIn|Number:Energy|read|Indicates the actual charging current of the Battery in watt|
|batteryDischarging|Number:Energy|read|Indicates the actual current discharging the Battery in watt|
|consumption|Number:Energy|read|Indicates the actual consumption of the consumer in watt|
|gridFeedIn|Number:Energy|read|Indicates the actual current feeding to the Grid in watt.0 if nothing is feeded|
|gridConsumption|Number:Energy|read|Indicates the actual current consumption from the Grid in watt.0 if nothing is received|
|solarProduction|Number:Energy|read|Indicates the actual production of the Solar system in watt|
|batteryLevel|Number|read|Indicates the actual Battery Level in % from 0 - 100|
|flowConsumptionBatteryState|Switch|read|Indicates if there is a current flow from Battery towards Consumption|
|flowConsumptionGridState|Switch|read|Indicates if there is a current flow from Grid towards Consumption|
|flowConsumptionProductionState|Switch|read|Indicates if there is a current flow from Solar Production towards Consumption|
|flowGridBatteryState|Switch|read|Indicates if there is a current flow from Grid towards Battery|
|flowProductionBatteryState|Switch|read|Indicates if there is a current flow from Production towards Battery|
|flowProductionGridState|Switch|read|Indicates if there is a current flow from Production towards Grid|

## Full Example

example.things:

```
Thing sonnen:sonnenbattery:myBattery "Sonnen Battery"  [ hostIP="192.168.0.10"]
```

example.items:

```
Number:Energy Consumption { channel="sonnen:sonnenbattery:myBattery:consumption" }
Number:Energy GridFeeding { channel="sonnen:sonnenbattery:myBattery:gridFeedIn" }
Number BatteryLevel { channel="sonnen:sonnenbattery:myBattery:batteryLevel" }
Switch FlowConsumptionBattery { channel="sonnen:sonnenbattery:myBattery:flowConsumptionBattery" }
```

## Tested Hardware

The binding was successfully tested with the following sonnen battery:

- sonnnen eco 8.0 SW Version: 1.6.10.1221979
