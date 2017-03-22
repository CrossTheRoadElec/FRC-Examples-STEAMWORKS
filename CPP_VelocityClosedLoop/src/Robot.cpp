/**
 * Example demonstrating the velocity closed-loop servo.
 * Tested with Logitech F350 USB Gamepad inserted into Driver Station]
 *
 * Be sure to select the correct feedback sensor using SetFeedbackDevice() below.
 *
 * After deploying/debugging this to your RIO, first use the left Y-stick
 * to throttle the Talon manually.  This will confirm your hardware setup.
 * Be sure to confirm that when the Talon is driving forward (green) the
 * position sensor is moving in a positive direction.  If this is not the cause
 * flip the boolean input to the SetSensorDirection() call below.
 *
 * Once you've ensured your feedback device is in-phase with the motor,
 * use the button shortcuts to servo to target velocity.
 *
 * Tweak the PID gains accordingly.
 */
#include "WPILib.h"
#include "CANTalon.h" /* necessary as of FRC2017, comment out for earler seasons */

class Robot: public IterativeRobot {
private:
	CANTalon * _talon = new CANTalon(0);
	Joystick * _joy = new Joystick(0);
	std::string _sb;
	int _loops = 0;

	void RobotInit() {
        /* first choose the sensor */
		_talon->SetFeedbackDevice(CANTalon::CtreMagEncoder_Relative);
		_talon->SetSensorDirection(false);
		//_talon->ConfigEncoderCodesPerRev(XXX), // if using FeedbackDevice.QuadEncoder
		//_talon->ConfigPotentiometerTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPot

		/* set the peak and nominal outputs, 12V means full */
		_talon->ConfigNominalOutputVoltage(+0.0f, -0.0f);
		_talon->ConfigPeakOutputVoltage(+12.0f, -12.0f);
		/* set closed loop gains in slot0 */
		_talon->SelectProfileSlot(0);
		_talon->SetF(0.1097);
		_talon->SetP(0.22);
		_talon->SetI(0.0);
		_talon->SetD(0.0);
	}
	/**
	 * This function is called periodically during operator control
	 */
	void TeleopPeriodic() {
		/* get gamepad axis */
		double leftYstick = _joy->GetAxis(Joystick::kYAxis);
		double motorOutput = _talon->GetOutputVoltage() / _talon->GetBusVoltage();
		/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(std::to_string(motorOutput));
		_sb.append("\tspd:");
		_sb.append(std::to_string(_talon->GetSpeed()));
		/* while button1 is held down, closed-loop on target velocity */
		if (_joy->GetRawButton(1)) {
        	/* Speed mode */
			double targetSpeed = leftYstick * 1500.0; /* 1500 RPM in either direction */
			_talon->SetControlMode(CANSpeedController::kSpeed);
        	_talon->Set(targetSpeed); /* 1500 RPM in either direction */

			/* append more signals to print when in speed mode. */
			_sb.append("\terrNative:");
			_sb.append(std::to_string(_talon->GetClosedLoopError()));
			_sb.append("\ttrg:");
			_sb.append(std::to_string(targetSpeed));
        } else {
			/* Percent voltage mode */
			_talon->SetControlMode(CANSpeedController::kPercentVbus);
			_talon->Set(leftYstick);
		}
		/* print every ten loops, printing too much too fast is generally bad for performance */
		if (++_loops >= 10) {
			_loops = 0;
			printf("%s\n",_sb.c_str());
		}
		_sb.clear();
	}
};

START_ROBOT_CLASS(Robot)
