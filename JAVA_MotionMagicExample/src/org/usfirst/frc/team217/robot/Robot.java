package org.usfirst.frc.team217.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.CANTalon;
import com.ctre.CANTalon.*;

public class Robot extends IterativeRobot {
	CANTalon _talon = new CANTalon(3);
	Joystick _joy = new Joystick(0);
	StringBuilder _sb = new StringBuilder();

	public void robotInit() {
		/* first choose the sensor */
		_talon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		_talon.reverseSensor(true);
		// _talon.configEncoderCodesPerRev(XXX), // if using
		// FeedbackDevice.QuadEncoder
		// _talon.configPotentiometerTurns(XXX), // if using
		// FeedbackDevice.AnalogEncoder or AnalogPot

		/* set the peak and nominal outputs, 12V means full */
		_talon.configNominalOutputVoltage(+0.0f, -0.0f);
		_talon.configPeakOutputVoltage(+12.0f, -12.0f);
		/* set closed loop gains in slot0 */
		_talon.setProfile(0);
		_talon.setF(0.2481);
		_talon.setP(0.0731);
		_talon.setI(0);
		_talon.setD(0);
		_talon.setMotionMagicCruiseVelocity(453); /* 453 RPM */
		_talon.setMotionMagicAcceleration(453);	 /* 453 RPM per second */
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		/* get gamepad axis - forward stick is positive */
		double leftYstick = -1.0 * _joy.getAxis(AxisType.kY);
		/* calculate the percent motor output */
		double motorOutput = _talon.getOutputVoltage() / _talon.getBusVoltage();
		/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(motorOutput);
		_sb.append("\tspd:");
		_sb.append(_talon.getSpeed());

		if (_joy.getRawButton(1)) {
			/* Motion Magic */
			double targetPos = leftYstick
					* 10.0; /* 10 Rotations in either direction */
			_talon.changeControlMode(TalonControlMode.MotionMagic);
			_talon.set(targetPos); 

			/* append more signals to print when in speed mode. */
			_sb.append("\terr:");
			_sb.append(_talon.getClosedLoopError());
			_sb.append("\ttrg:");
			_sb.append(targetPos);
		} else {
			/* Percent voltage mode */
			_talon.changeControlMode(TalonControlMode.PercentVbus);
			_talon.set(leftYstick);
		}
		/* instrumentation */
		Instrum.Process(_talon, _sb);
	}
}
