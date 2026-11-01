package first.robot.mechanisms;

import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.hardware.bus.CANPort;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import first.robot.constants.Constants.FuelConstants;

class FuelMechanism implements Mechanism {
  SparkMax feederRoller;
  SparkMax intakeLauncherRoller;

  public FuelMechanism() {
    intakeLauncherRoller = new SparkMax(CANPort.CAN_D0, FuelConstants.INTAKE_LAUNCHER_MOTOR_ID, MotorType.kBrushless);
    feederRoller = new SparkMax(CANPort.CAN_D0, FuelConstants.FEEDER_MOTOR_ID, MotorType.kBrushless);

    SparkMaxConfig feederConfig = new SparkMaxConfig();
    feederConfig.smartCurrentLimit(FuelConstants.FEEDER_MOTOR_CURRENT_LIMIT);
    feederRoller.configure(feederConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig launcherConfig = new SparkMaxConfig();
    launcherConfig.inverted(true);
    launcherConfig.smartCurrentLimit(FuelConstants.LAUNCHER_MOTOR_CURRENT_LIMIT);
    intakeLauncherRoller.configure(launcherConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

  }

  public Command intake() {
    return run(Coroutine -> {
      intakeLauncherRoller.setVoltage(FuelConstants.INTAKING_INTAKE_VOLTAGE);
      feederRoller.setVoltage(FuelConstants.INTAKING_FEEDER_VOLTAGE);
      Coroutine.yield();
    }).named("intake");
  }

  public Command eject() {
    return run(Coroutine -> {
      feederRoller.setVoltage(FuelConstants.INTAKING_FEEDER_VOLTAGE * -1);
      intakeLauncherRoller.setVoltage(FuelConstants.INTAKING_INTAKE_VOLTAGE * -1);
      Coroutine.yield();
    }).named("eject");
  }

  public Command launchVoltage() {
    return run(Coroutine -> {
      feederRoller.setVoltage(FuelConstants.LAUNCHING_FEEDER_VOLTAGE);
      intakeLauncherRoller.setVoltage(FuelConstants.LAUNCHING_LAUNCHER_VOLTAGE);
      Coroutine.yield();
    }).named("set launch voltages");
  }

  public Command spinUpVoltage() {
    return run(Coroutine -> {
      feederRoller.setVoltage(FuelConstants.SPIN_UP_FEEDER_VOLTAGE);
      intakeLauncherRoller.setVoltage(FuelConstants.LAUNCHING_LAUNCHER_VOLTAGE);
    }).named("spin up voltages");
  }

  public Command launch() {
    return run(Coroutine -> {
      Coroutine.fork(spinUpVoltage());
      Coroutine.wait(Seconds.of(5));
      Coroutine.fork(launchVoltage());
    }).named("launch fuel");
  }
}
