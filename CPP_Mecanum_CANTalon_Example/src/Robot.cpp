#include "WPILib.h"
#include "CANTalon.h"
class MecanumDefaultCode : public IterativeRobot
{
	CANTalon lf; /*left front */
	CANTalon lr;/*left rear */
	CANTalon rf; /*right front */
	CANTalon rr; /*right rear */
public:
	RobotDrive *m_robotDrive;		// RobotDrive object using PWM 1-4 for drive motors
	Joystick *m_driveStick;			// Joystick object on USB port 1 (mecanum drive)public:
	AnalogGyro gyro;
	/**
	 * Constructor for this "MecanumDefaultCode" Class.
	 */
	MecanumDefaultCode(void) : lf(3), lr(1), rf(4), rr(5), gyro(0)
	{
		/* Set every Talon to reset the motor safety timeout. */
		lf.Set(0);
		lr.Set(0);
		rf.Set(0);
		rr.Set(0);
		// Create a RobotDrive object using PWMS 1, 2, 3, and 4
		m_robotDrive = new RobotDrive(lf, lr, rf, rr);
		m_robotDrive->SetExpiration(0.5);
		m_robotDrive->SetSafetyEnabled(false);
		// Define joystick being used at USB port #0 on the Drivers Station
		m_driveStick = new Joystick(0);
	}
	void TeleopInit()
	{
		gyro.Reset();
	}
	/** @return 10% deadband */
	double Db(double axisVal)
	{
		if(axisVal < -0.10)
			return axisVal;
		if(axisVal > +0.10)
			return axisVal;
		return 0;
	}
	/**
	 * Gets called once for each new packet from the DS.
	 */
	void TeleopPeriodic(void)
	{
		float angle = gyro.GetAngle();
		//std::cout << "Angle : " << angle << std::endl;
		m_robotDrive->MecanumDrive_Cartesian(	Db(m_driveStick->GetX()),
												Db(m_driveStick->GetY()),
												Db(m_driveStick->GetZ()),
												angle);
		/* my right side motors need to drive negative to move robot forward */
		m_robotDrive->SetInvertedMotor(RobotDrive::kFrontRightMotor,true);
		m_robotDrive->SetInvertedMotor(RobotDrive::kRearRightMotor,true);
		/* on button 5, reset gyro angle to zero */
		if(m_driveStick->GetRawButton(5))
			gyro.Reset();
	}
};
START_ROBOT_CLASS(MecanumDefaultCode);
