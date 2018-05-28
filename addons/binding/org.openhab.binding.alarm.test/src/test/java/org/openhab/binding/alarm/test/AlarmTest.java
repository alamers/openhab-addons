package org.openhab.binding.alarm.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openhab.binding.alarm.internal.AlarmController;
import org.openhab.binding.alarm.internal.AlarmException;
import org.openhab.binding.alarm.internal.AlarmListener;
import org.openhab.binding.alarm.internal.config.AlarmControllerConfig;
import org.openhab.binding.alarm.internal.model.AlarmCommand;
import org.openhab.binding.alarm.internal.model.AlarmStatus;
import org.openhab.binding.alarm.internal.model.AlarmZone;
import org.openhab.binding.alarm.internal.model.AlarmZoneType;

public class AlarmTest {
    private static String ID_ZONE_ACTIVE = "1";
    private static String ID_ZONE_ALWAYS = "2";
    private static String ID_ZONE_DISABLED = "3";
    private static String ID_ZONE_EXIT_ENTRY = "4";
    private static String ID_ZONE_IMMEDIATELY = "5";
    private static String ID_ZONE_INTERN_ACTIVE = "6";
    private static String ID_ZONE_SABOTAGE = "7";
    private static String ID_ZONE_MOTION = "8";
    private static String ID_ZONE_INTERN_MOTION = "9";

    private static AlarmController alarm;
    private static AlarmControllerConfig config;

    private static AlarmStatus status;
    private static int countdown;
    private static boolean isReadyToArmInternally;
    private static boolean isReadyToArmExternally;
    private static boolean isReadyToPassthrough;

    @BeforeClass
    public static void setup() {
        config = new AlarmControllerConfig();
        config.setAlarmDelay(1);
        config.setEntryTime(1);
        config.setExitTime(1);
        config.setPassthroughTime(1);
        alarm = new AlarmController(config, new AlarmListener() {

            @Override
            public void alarmStatusChanged(AlarmStatus status) {
                AlarmTest.status = status;
            }

            @Override
            public void alarmCountdownChanged(int value) {
                AlarmTest.countdown = value;
            }

            @Override
            public void readyToArmInternallyChanged(boolean isReady) {
                AlarmTest.isReadyToArmInternally = isReady;
            }

            @Override
            public void readyToArmExternallyChanged(boolean isReady) {
                AlarmTest.isReadyToArmExternally = isReady;
            }

            @Override
            public void readyToPassthroughChanged(boolean isReady) {
                AlarmTest.isReadyToPassthrough = isReady;
            }
        });

        alarm.addAlarmZone(new AlarmZone(ID_ZONE_ACTIVE, AlarmZoneType.ACTIVE));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_ALWAYS, AlarmZoneType.ALWAYS));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_DISABLED, AlarmZoneType.DISABLED));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_EXIT_ENTRY, AlarmZoneType.EXIT_ENTRY));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_IMMEDIATELY, AlarmZoneType.IMMEDIATELY));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_INTERN_ACTIVE, AlarmZoneType.INTERN_ACTIVE));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_SABOTAGE, AlarmZoneType.SABOTAGE));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_MOTION, AlarmZoneType.MOTION));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_INTERN_MOTION, AlarmZoneType.INTERN_MOTION));
    }

    private void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            fail();
        }
    }

    // ---------------------------------------------------------------------------

    @Test
    public void testArmIntern() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);

        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
    }

    @Test
    public void testArmInternOpenZone() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);

        try {
            alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, false);
            alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
            fail();
        } catch (AlarmException e) {
            // OK
        } finally {
            alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, true);
        }
        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
    }

    @Test
    public void testArmInternSabotage() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, false);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(AlarmStatus.PREALARM, status);
        alarm.doCommand(AlarmCommand.DISARM);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, true);
        assertEquals(true, isReadyToArmInternally);
    }

    @Test
    public void testArmInternExitEntry() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        assertEquals(false, isReadyToArmInternally);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
        assertEquals(true, isReadyToArmInternally);
    }

    @Test
    public void testArmInternDisable() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);

        alarm.alarmZoneChanged(ID_ZONE_DISABLED, false);
        assertEquals(true, isReadyToArmInternally);
        alarm.alarmZoneChanged(ID_ZONE_DISABLED, true);
        assertEquals(true, isReadyToArmInternally);
    }

    @Test
    public void testArmExtern() throws AlarmException {
        assertEquals(true, isReadyToArmExternally);
        assertEquals(true, isReadyToArmInternally);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);

        assertEquals(AlarmStatus.EXIT, status);
        assertEquals(false, isReadyToArmInternally);
        sleep(0.5);
        assertEquals(1, countdown);

        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);
        assertEquals(0, countdown);
        assertEquals(false, isReadyToArmInternally);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
    }

    @Test
    public void testArmExternOpenZone() throws AlarmException {
        assertEquals(true, isReadyToArmExternally);

        try {
            alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
            alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
            fail();
        } catch (AlarmException e) {
            // OK
        } finally {
            alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
        }
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
    }

    @Test
    public void testArmExternOpenExit() throws AlarmException {
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(2);

        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
    }

    @Test
    public void testArmExternSabotage() throws AlarmException {
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, false);
        assertEquals(false, isReadyToArmExternally);
        assertEquals(AlarmStatus.PREALARM, status);
        alarm.doCommand(AlarmCommand.DISARM);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, true);
        assertEquals(true, isReadyToArmExternally);
    }

    @Test
    public void testArmExternExitEntry() throws AlarmException {
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        assertEquals(true, isReadyToArmExternally);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
        assertEquals(true, isReadyToArmExternally);
    }

    @Test
    public void testPassthrough() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(false, isReadyToPassthrough);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);
        assertEquals(true, isReadyToPassthrough);

        alarm.doCommand(AlarmCommand.PASSTHROUGH);
        assertEquals(AlarmStatus.PASSTHROUGH, status);
        sleep(0.5);
        assertEquals(1, countdown);
        assertEquals(false, isReadyToPassthrough);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        sleep(1);
        assertEquals(0, countdown);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
    }

    @Test
    public void testAlarmZoneDisabled() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_DISABLED, false);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_DISABLED, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_DISABLED, false);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_DISABLED, true);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
    }

    @Test
    public void testAlarmZoneActive() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        assertEquals(AlarmStatus.EXIT, status);
        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
        assertEquals(AlarmStatus.PREALARM, status);
        sleep(2);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
    }

    @Test
    public void testAlarmZoneInternActive() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, false);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, false);

        assertEquals(AlarmStatus.PREALARM, status);
        sleep(2);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, true);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, false);

        assertEquals(AlarmStatus.PREALARM, status);
        sleep(2);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, true);
    }

    @Test
    public void testAlarmZoneExitEntry() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        assertEquals(AlarmStatus.PREALARM, status);
        sleep(2);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        assertEquals(AlarmStatus.ENTRY, status);
        sleep(2);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);

    }

    @Test
    public void testAlarmZoneImmediately() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_IMMEDIATELY, false);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_IMMEDIATELY, true);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_IMMEDIATELY, false);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_IMMEDIATELY, true);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_IMMEDIATELY, false);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_IMMEDIATELY, true);
    }

    @Test
    public void testAlarmZoneSabotage() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, false);
        assertEquals(AlarmStatus.PREALARM, status);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);
        sleep(2);
        assertEquals(AlarmStatus.SABOTAGE_ALARM, status);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, true);
        assertEquals(AlarmStatus.SABOTAGE_ALARM, status);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, false);
        assertEquals(AlarmStatus.PREALARM, status);
        sleep(2);
        assertEquals(AlarmStatus.SABOTAGE_ALARM, status);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);
        alarm.alarmZoneChanged(ID_ZONE_SABOTAGE, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);
    }

    @Test
    public void testAlarmZoneAlways() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_ALWAYS, false);
        assertEquals(AlarmStatus.PREALARM, status);
        sleep(2);
        assertEquals(AlarmStatus.ALARM, status);

        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        assertEquals(false, isReadyToArmInternally);
        assertEquals(false, isReadyToArmExternally);
        alarm.alarmZoneChanged(ID_ZONE_ALWAYS, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

    }

    @Test
    public void testAlarmOnExit() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.getConfig().setAlarmDelay(10);
        alarm.getConfig().setExitTime(10);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.EXIT, status);
        sleep(2);
        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
        alarm.getConfig().setAlarmDelay(1);
        alarm.getConfig().setExitTime(1);
    }

    @Test
    public void testAlarmOnEntryExternally() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.getConfig().setAlarmDelay(10);
        alarm.getConfig().setEntryTime(10);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(0.5);
        assertEquals(1, countdown);
        assertEquals(AlarmStatus.EXIT, status);
        sleep(2);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.ENTRY, status);

        sleep(1);
        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
        alarm.getConfig().setAlarmDelay(1);
        alarm.getConfig().setEntryTime(1);
    }

    @Test
    public void testAlarmOnEntryInternally() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.getConfig().setAlarmDelay(10);
        alarm.getConfig().setEntryTime(10);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
        alarm.getConfig().setAlarmDelay(1);
        alarm.getConfig().setEntryTime(1);
    }

    @Test
    public void testExitEntryOnPrealarm() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.getConfig().setAlarmDelay(10);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, false);
        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, true);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
        alarm.getConfig().setAlarmDelay(1);
    }

    @Test
    public void testAlarmOnPassthrough() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.getConfig().setPassthroughTime(10);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.doCommand(AlarmCommand.PASSTHROUGH);

        sleep(0.5);
        assertEquals(10, countdown);
        assertEquals(AlarmStatus.PASSTHROUGH, status);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        assertEquals(AlarmStatus.DISARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_ACTIVE, true);
        alarm.getConfig().setPassthroughTime(1);

    }

    @Test
    public void testArmingDifferentZones() {
        alarm.removeAlarmZone(ID_ZONE_ALWAYS);
        alarm.removeAlarmZone(ID_ZONE_DISABLED);
        alarm.removeAlarmZone(ID_ZONE_EXIT_ENTRY);
        alarm.removeAlarmZone(ID_ZONE_IMMEDIATELY);
        alarm.removeAlarmZone(ID_ZONE_INTERN_ACTIVE);
        alarm.removeAlarmZone(ID_ZONE_SABOTAGE);
        alarm.removeAlarmZone(ID_ZONE_MOTION);
        alarm.removeAlarmZone(ID_ZONE_INTERN_MOTION);

        assertEquals(true, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);

        alarm.removeAlarmZone(ID_ZONE_ACTIVE);
        assertEquals(false, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);

        alarm.addAlarmZone(new AlarmZone(ID_ZONE_INTERN_ACTIVE, AlarmZoneType.INTERN_ACTIVE));
        assertEquals(true, isReadyToArmExternally);
        assertEquals(true, isReadyToArmInternally);
        alarm.removeAlarmZone(ID_ZONE_INTERN_ACTIVE);
        assertEquals(false, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);

        alarm.addAlarmZone(new AlarmZone(ID_ZONE_ALWAYS, AlarmZoneType.ALWAYS));
        assertEquals(true, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);
        alarm.removeAlarmZone(ID_ZONE_ALWAYS);
        assertEquals(false, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);

        alarm.addAlarmZone(new AlarmZone(ID_ZONE_DISABLED, AlarmZoneType.DISABLED));
        assertEquals(false, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);
        alarm.removeAlarmZone(ID_ZONE_DISABLED);
        assertEquals(false, isReadyToArmExternally);
        assertEquals(false, isReadyToArmInternally);

        alarm.addAlarmZone(new AlarmZone(ID_ZONE_ACTIVE, AlarmZoneType.ACTIVE));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_ALWAYS, AlarmZoneType.ALWAYS));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_DISABLED, AlarmZoneType.DISABLED));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_EXIT_ENTRY, AlarmZoneType.EXIT_ENTRY));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_IMMEDIATELY, AlarmZoneType.IMMEDIATELY));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_INTERN_ACTIVE, AlarmZoneType.INTERN_ACTIVE));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_SABOTAGE, AlarmZoneType.SABOTAGE));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_MOTION, AlarmZoneType.MOTION));
        alarm.addAlarmZone(new AlarmZone(ID_ZONE_INTERN_MOTION, AlarmZoneType.INTERN_MOTION));
    }

    @Test
    public void testAlarmZoneMotion() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, false);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);
    }

    @Test
    public void testAlarmZoneInternMotion() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, false);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, true);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);
    }

    @Test
    public void testAlarmOnMotion() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(1.5);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        alarm.alarmZoneChanged(ID_ZONE_MOTION, true);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(1.5);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, true);
    }

    @Test
    public void testAlarmOnInternMotion() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_INTERNALLY);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, false);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);
        alarm.alarmZoneChanged(ID_ZONE_MOTION, true);
        assertEquals(AlarmStatus.INTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, true);
    }

    @Test
    public void testArmingIfMotion() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, false);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, false);
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(1.5);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, true);
        alarm.alarmZoneChanged(ID_ZONE_INTERN_MOTION, true);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_MOTION, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);

        alarm.doCommand(AlarmCommand.DISARM);
        alarm.alarmZoneChanged(ID_ZONE_MOTION, true);
    }

    @Test
    public void testEntryAfterAlarm() throws AlarmException {
        assertEquals(true, isReadyToArmInternally);
        assertEquals(true, isReadyToArmExternally);

        alarm.doCommand(AlarmCommand.ARM_EXTERNALLY);
        sleep(1.5);
        assertEquals(AlarmStatus.EXTERNALLY_ARMED, status);

        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, false);
        sleep(0.5);
        assertEquals(AlarmStatus.PREALARM, status);
        sleep(1);
        assertEquals(AlarmStatus.ALARM, status);

        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, false);
        sleep(0.5);
        assertEquals(AlarmStatus.ENTRY, status);

        alarm.doCommand(AlarmCommand.DISARM);
        alarm.alarmZoneChanged(ID_ZONE_ACTIVE, true);
        alarm.alarmZoneChanged(ID_ZONE_EXIT_ENTRY, true);
    }
}
