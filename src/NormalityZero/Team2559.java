/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package NormalityZero;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;

public class Team2559 extends IterativeRobot {

    // Joysticks
    Joystick joystick1 = new Joystick(1); // Drive Stick
    Joystick joystick2 = new Joystick(2); // Shoot Stick

    // Motors
    Jaguar jaguar1 = new Jaguar(1); // Left Drive Side
    Jaguar jaguar2 = new Jaguar(2); // Right Drive Side
    Jaguar jaguar3 = new Jaguar(3); // Bottom Shooter Wheel
    Jaguar jaguar4 = new Jaguar(4); // Top Shooter Wheel

    // Drive
    RobotDrive RobotDrive = new RobotDrive(jaguar1, jaguar2);

    // Relays
    Relay spike1 = new Relay(1); // Left Belt Pickup Motor
    Relay spike2 = new Relay(2); // Right Belt Pickup Motor

    // Solenoid
    Solenoid solenoid1 = new Solenoid(1); // Ball Pusher
    Solenoid solenoid2 = new Solenoid(2); // Arm
    Solenoid solenoid4 = new Solenoid(4); // Ball Pop

    // Timer
    Timer systimer = new Timer();

    // Compressor
    Compressor Compressor = new Compressor(10, 8); // Digtial I/O,Relay


    int maxShootCount;
    int currShootCount;
    boolean trigger_pressed = false;
    boolean belts_stopped = true;
    AxisCamera camera; // Axis camera object (connected to network switch)

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        Compressor.start();
        camera = AxisCamera.getInstance(); // Get an instance of the camera
    }

    public void autonomousInit() {
        maxShootCount = 2;
        currShootCount = 0;
        systimer.start();
        systimer.reset();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Watchdog.getInstance().feed(); // Feed Watchdog Nibbles & Bits
        while (isAutonomous() && isEnabled()) {
            double currentTime = systimer.get();
            if (currentTime < 6) {
                jaguar3.set(0.6);
                jaguar4.set(-0.6);
                if (currShootCount < maxShootCount) {
                    Timer.delay(1.5);
                    solenoid4.set(true);
                    Timer.delay(0.5);
                    solenoid4.set(false);
                }
                currShootCount++;
            } else if (currentTime >= 5 && currentTime < 7) {
                jaguar3.set(1.0);
                jaguar4.set(1.0);
                jaguar1.set(-1.0);
                jaguar2.set(1.0);
            } else if (currentTime >= 7 && currentTime < 8) {
                jaguar1.set(-1.0);
                jaguar2.set(-1.0);
            } else if (currentTime >= 8 && currentTime < 10) {
                jaguar1.set(-1.0);
                jaguar2.set(1.0);
            } else if (currentTime >= 10 && currentTime < 11) {
                jaguar1.set(-1.0);
                jaguar2.set(-1.0);
            } else if (currentTime >= 11 && currentTime < 15) {
                jaguar1.set(-1.0);
                jaguar2.set(1.0);
            }
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        // Joystick 1
        RobotDrive.arcadeDrive(joystick1); // Drive With Joystick
        if (joystick1.getRawButton(1) && !trigger_pressed) {
            if (belts_stopped) {
                belts_stopped = false;
            } else {
                belts_stopped = true;
            }
            trigger_pressed = true;
        } else if (!joystick1.getRawButton(1) && trigger_pressed) {
            trigger_pressed = false;
        }
        if (!belts_stopped) {
            if (joystick1.getRawButton(2)) {
                spike2.set(Relay.Value.kForward);
                spike1.set(Relay.Value.kReverse);
            } else {
                spike2.set(Relay.Value.kReverse);
                spike1.set(Relay.Value.kForward);
            }
        } else if (belts_stopped) {
            spike2.set(Relay.Value.kOff);
            spike1.set(Relay.Value.kOff);
        }

        if (joystick1.getRawButton(3)) { // Arm
            solenoid2.set(true);
        } else {
            solenoid2.set(false);
        }

        if (joystick1.getRawButton(4)) { // Release Kicker
            solenoid1.set(true);
        } else if (joystick1.getRawButton(5)) { // Deploy Kicker
            solenoid1.set(false);
        } else { //DEFAULT
            solenoid1.set(true);
        }

        // Joystick 2
        if (joystick2.getRawButton(1) == true) { // Ball Pop
            solenoid4.set(true);
        } else {
            solenoid4.set(false);
        }

        if (joystick2.getRawButton(3)) { // Motors Running @ 100%
            jaguar3.set(1);
            jaguar4.set(-1);
        } else if (joystick2.getRawButton(5)) { // Motors Running @ 075%
            jaguar3.set(0.75);
            jaguar4.set(-0.75);
        } else if (joystick2.getRawButton(2)) { // Motors Running @ 050%
            jaguar3.set(0.5);
            jaguar4.set(-0.5);
        } else if (joystick2.getRawButton(4)) { // Motors Running @ 025%
            jaguar3.set(0.25);
            jaguar4.set(-0.25);
        } else { // Motors Off
            jaguar3.set(0);
            jaguar4.set(0);
        }
    }
}
