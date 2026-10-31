package first.robot;

import java.util.function.DoubleSupplier;

import org.wpilib.driverstation.XboxController;
import org.wpilib.driverstation.Gamepad.Axis;
import org.wpilib.opmode.OpMode;

import first.robot.mechanisms.DriveMechanism;

public class CompTeleop implements OpMode {
    XboxController driveController;
    XboxController opController;
    DriveMechanism drive;


    CompTeleop(Robot robot){
        driveController = robot.driveController;
        opController = robot.opController;
        drive = robot.drive;

    }

    @Override
    public void periodic() {
        drive.driveArcade(driveController.getLeftY(), driveController.getRightX());
    }
}
