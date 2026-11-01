package first.robot;

import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;

@Teleop
public class CompTeleop implements OpMode {
    public CompTeleop(Robot robot){
        robot.driveController.leftBumper().whileTrue(robot.fuel.intake());
        robot.driveController.rightBumper().whileTrue(robot.fuel.launch());
    }

    @Override
    public void start() {
    }
}
