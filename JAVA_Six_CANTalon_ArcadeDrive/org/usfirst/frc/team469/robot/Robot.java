
package org.usfirst.frc.team469.robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import com.ctre.CANTalon.TalonControlMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	/* talons for arcade drive */
	CANTalon _frontLeftMotor = new CANTalon(11); 		/* device IDs here (1 of 2) */
	CANTalon _rearLeftMotor = new CANTalon(13);
	CANTalon _frontRightMotor = new CANTalon(14);
	CANTalon _rearRightMotor = new CANTalon(15);

	/* extra talons for six motor drives */
	CANTalon _leftSlave = new CANTalon(16);
	CANTalon _rightSlave = new CANTalon(17);
	
	RobotDrive _drive = new RobotDrive(_frontLeftMotor, _rearLeftMotor, _frontRightMotor, _rearRightMotor);
	
	Joystick _joy = new Joystick(0);
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	/* take our extra talons and just have them follow the Talons updated in arcadeDrive */
    	_leftSlave.changeControlMode(TalonControlMode.Follower);
    	_rightSlave.changeControlMode(TalonControlMode.Follower);
    	_leftSlave.set(11); 							/* device IDs here (2 of 2) */
    	_rightSlave.set(14);
    	
    	/* the Talons on the left-side of my robot needs to drive reverse(red) to move robot forward.
    	 * Since _leftSlave just follows frontLeftMotor, no need to invert it anywhere. */
    	_drive.setInvertedMotor(MotorType.kFrontLeft, true);
    	_drive.setInvertedMotor(MotorType.kRearLeft, true);
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	double forward = _joy.getRawAxis(1); // logitech gampad left X, positive is forward
    	double turn = _joy.getRawAxis(2); //logitech gampad right X, positive means turn right
    	_drive.arcadeDrive(forward, turn);
    }
}
