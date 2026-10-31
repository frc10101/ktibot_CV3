package first.robot.mechanisms;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.drive.DifferentialDrive;

import org.wpilib.hardware.bus.CANPort;

import first.robot.constants.Constants.DriveConstants;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

public class DriveMechanism implements Mechanism{
    private final SparkMax leftLeader;
    private final SparkMax leftFollower;
    private final SparkMax rightLeader;
    private final SparkMax rightFollower;

    private final DifferentialDrive drive;

    public DriveMechanism(){
        leftLeader = new SparkMax(CANPort.CAN_D0, DriveConstants.LEFT_LEADER_ID, MotorType.kBrushed);
        leftFollower = new SparkMax(CANPort.CAN_D0, DriveConstants.LEFT_FOLLOWER_ID, MotorType.kBrushed);
        rightLeader = new SparkMax(CANPort.CAN_D0, DriveConstants.RIGHT_LEADER_ID, MotorType.kBrushed);
        rightFollower = new SparkMax(CANPort.CAN_D0, DriveConstants.RIGHT_FOLLOWER_ID, MotorType.kBrushed);

        drive = new DifferentialDrive(leftLeader, rightLeader);

        leftLeader.setCANTimeout(250);
        rightLeader.setCANTimeout(250);
        leftFollower.setCANTimeout(250);
        rightFollower.setCANTimeout(250);

        //create motor configs,
        //voltage comp helps robot permorf similar on diff
        //batery voltages
        //current limit helps tripping brakers
        SparkMaxConfig config = new SparkMaxConfig();
        config.voltageCompensation(12);
        config.smartCurrentLimit(DriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);

        //set config to follow each leader then apply to follower
        //resetting in case a new contrller i sswapped
        config.follow(leftLeader);
        leftFollower.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        config.follow(rightLeader);
        rightFollower.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        //remove folling then apply config to right leader
        config.disableFollowerMode();
        rightLeader.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        //set config to inverted then appl to left side.
        config.inverted(true);
        leftLeader.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public Command driveArcade(Double xSpeed, Double zRotation){
        return runRepeatedly(() ->drive.arcadeDrive(xSpeed, zRotation)).named("arcade Drive");
    }

}
