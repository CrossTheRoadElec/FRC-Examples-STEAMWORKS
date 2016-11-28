/**
 * Example demonstrating the Position closed-loop servo.
 * Tested with Logitech F350 USB Gamepad inserted into Driver Station]
 *
 * Be sure to select the correct feedback sensor using SetFeedbackDevice() below.
 *
 * After deploying/debugging this to your RIO, first use the left Y-stick
 * to throttle the Talon manually.  This will confirm your hardware setup.
 * Be sure to confirm that when the Talon is driving forward (green) the
 * position sensor is moving in a positive direction.  If this is not the cause
 * flip the boolean input to the reverseSensor() call below.
 *
 * Once you've ensured your feedback device is in-phase with the motor,
 * use the button shortcuts to servo to target position.
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
	bool _lastButton1 = false;
	/** save the target position to servo to */
	double targetPositionRotations;

	void RobotInit() {
		/* lets grab the 360 degree position of the MagEncoder's absolute position */
		int absolutePosition = _talon->GetPulseWidthPosition() & 0xFFF; /* mask out the bottom12 bits, we don't care about the wrap arounds */
		/* use the low level API to set the quad encoder signal */
		_talon->SetEncPosition(absolutePosition);

		/* choose the sensor and sensor direction */
		_talon->SetFeedbackDevice(CANTalon::CtreMagEncoder_Relative);
		_talon->SetSensorDirection(false);
		//_talon->ConfigEncoderCodesPerRev(XXX), // if using FeedbackDevice.QuadEncoder
		//_talon->ConfigPotentiometerTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPot

		/* set the peak and nominal outputs, 12V means full */
		_talon->ConfigNominalOutputVoltage(+0., -0.);
		_talon->ConfigPeakOutputVoltage(+12., -12.);
		/* set the allowable closed-loop error,
		 * Closed-Loop output will be neutral within this range.
		 * See Table in Section 17.2.1 for native units per rotation.
		 */
		_talon->SetAllowableClosedLoopErr(0); /* always servo */
		/* set closed loop gains in slot0 */
		_talon->SelectProfileSlot(0);
		_talon->SetF(0.0);
		_talon->SetP(0.1);
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
		bool button1 = _joy->GetRawButton(1);
		bool button2 = _joy->GetRawButton(2);
		/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(std::to_string(motorOutput));
		_sb.append("\tpos:");
		_sb.append(std::to_string(_talon->GetPosition()));
		/* on button1 press enter closed-loop mode on target position */
		if (!_lastButton1 && button1) {
			/* Position mode - button just pressed */
			targetPositionRotations = leftYstick * 50.0; /* 50 Rotations in either direction */
			_talon->SetControlMode(CANSpeedController::kPosition);
			_talon->Set(targetPositionRotations); /* 50 rotations in either direction */

		}
		/* on button2 just straight drive */
		if (button2) {
			/* Percent voltage mode */
			_talon->SetControlMode(CANSpeedController::kPercentVbus);
			_talon->Set(leftYstick);
		}
		/* if Talon is in position closed-loop, print some more info */
		if (_talon->GetControlMode() == CANSpeedController::kPosition) {
			/* append more signals to print when in speed mode. */
			_sb.append("\terrNative:");
			_sb.append(std::to_string(_talon->GetClosedLoopError()));
			_sb.append("\ttrg:");
			_sb.append(std::to_string(targetPositionRotations));
		}
		/* print every ten loops, printing too much too fast is generally bad for performance */
		if (++_loops >= 10) {
			_loops = 0;
			printf("%s\n",_sb.c_str());
		}
		_sb.clear();
		/* save button state for on press detect */
		_lastButton1 = button1;
	}

};

START_ROBOT_CLASS(Robot)
