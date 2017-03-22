#include <iostream>
#include <memory>
#include <string>
#include "CANTalon.h"
#include "WPILib.h"
#include <unistd.h>
class Robot: public frc::IterativeRobot {
public:
	CANTalon _srx;
	Joystick _joy;
	std::stringstream _work;
	bool _btn1, _btn2, _btn3, _btn4;
	/** simple constructor */
	Robot() : _srx(9), _joy(0), _work(), _btn1(false), _btn2(false), _btn3(false), _btn4(false) 	{	}
	/* everytime we enter disable, reinit*/
	void DisabledInit() {
		_srx.SetFeedbackDevice(CANTalon::CtreMagEncoder_Relative); /* MagEncoder meets the requirements for Unit-Scaling */
		_srx.SetStatusFrameRateMs(CANTalon::StatusFrameRateFeedback, 5); /* Talon will send new frame every 5ms */
	}
	/* every loop */
	void DisabledPeriodic() {
		bool btn1 = _joy.GetRawButton(1);	/* get buttons */
		bool btn2 = _joy.GetRawButton(2);
		bool btn3 = _joy.GetRawButton(3);
		bool btn4 = _joy.GetRawButton(4);

		/* on button unpress => press, change pos register */
		if(!_btn1 && btn1) {			_srx.SetPosition(10.0);			_work << "set:10.0" << std::endl;		}
		if(!_btn2 && btn2) {			_srx.SetPosition(20.0);			_work << "set:20.0" << std::endl;		}
		if(!_btn3 && btn3) {			_srx.SetPosition(30.0);			_work << "set:30.0" << std::endl;		}
		if(!_btn4 && btn4) {			_srx.SetPosition(40.0);			_work << "set:40.0" << std::endl;		}

		/* remove this and at most we get one stale print (one loop) */
		usleep(10e3);

		/* call get and serialize what we get */
		double read = _srx.GetPosition();
		_work << "read:" << read<< std::endl;

		/* print any rendered strings, and clear work */
		printf(_work.str().c_str());
		_work.str("");

		_btn1 = btn1; /* save button states */
		_btn2 = btn2;
		_btn3 = btn3;
		_btn4 = btn4;
	}
};

START_ROBOT_CLASS(Robot)
