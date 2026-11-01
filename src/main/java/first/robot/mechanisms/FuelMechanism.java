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

public class FuelMechanism implements Mechanism {
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
      while (true) {
        intakeLauncherRoller.setVoltage(FuelConstants.INTAKING_INTAKE_VOLTAGE);
        feederRoller.setVoltage(FuelConstants.INTAKING_FEEDER_VOLTAGE);
        Coroutine.yield();
      }
    }).whenCanceled(this::stop).named("intake");
  }

  public Command eject() {
    return run(Coroutine -> {
      while (true) {
        feederRoller.setVoltage(FuelConstants.INTAKING_FEEDER_VOLTAGE * -1);
        intakeLauncherRoller.setVoltage(FuelConstants.INTAKING_INTAKE_VOLTAGE * -1);
        Coroutine.yield();
      }
    }).whenCanceled(this::stop).named("eject");
  }

  public Command launchVoltage() {
    return run(Coroutine -> {
      while (true) {
        feederRoller.setVoltage(FuelConstants.LAUNCHING_FEEDER_VOLTAGE);
        intakeLauncherRoller.setVoltage(FuelConstants.LAUNCHING_LAUNCHER_VOLTAGE);
        Coroutine.yield();
      }
    }).named("set launch voltages");
  }

  public Command spinUpVoltage() {
    return run(Coroutine -> {
      while (true) {
        feederRoller.setVoltage(FuelConstants.SPIN_UP_FEEDER_VOLTAGE);
        intakeLauncherRoller.setVoltage(FuelConstants.LAUNCHING_LAUNCHER_VOLTAGE);
        Coroutine.yield();
      }
    }).named("spin up voltages");
  }

  public Command launch() {
    return run(Coroutine -> {
      Coroutine.fork(spinUpVoltage());
      Coroutine.wait(Seconds.of(5));
      Coroutine.await(launchVoltage());
    }).whenCanceled(this::stop).named("launch fuel");
  }

  private void stop() {
    feederRoller.setVoltage(0);
    intakeLauncherRoller.setVoltage(0);
  }
}
