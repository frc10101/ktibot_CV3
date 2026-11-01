// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.command3.Scheduler;
import org.wpilib.command3.button.CommandGamepad;
import org.wpilib.framework.OpModeRobot;

import first.robot.constants.Constants;
import first.robot.constants.DriverConstants;
import first.robot.mechanisms.DriveMechanism;
import first.robot.mechanisms.ExampleMechanism;
import first.robot.mechanisms.FuelMechanism;

public class Robot extends OpModeRobot {
  final ExampleMechanism exampleMechanism = new ExampleMechanism();
  final DriveMechanism drive = new DriveMechanism();
  final FuelMechanism fuel = new FuelMechanism();
  final CommandGamepad exampleController = new CommandGamepad(DriverConstants.DRIVER_CONTROLLER_PORT);
  final CommandGamepad driveController = new CommandGamepad(Constants.OperatorConstants.DRIVER_CONTROLLER_PORT);

  public Robot() {
    drive.setDefaultCommand(drive.driveArcade(driveController::getLeftY, driveController::getRightX));
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getDefault().run();
  }
}
