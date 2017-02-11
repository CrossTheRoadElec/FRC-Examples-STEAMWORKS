#include "ExampleSubsystem.h"

#include "../RobotMap.h"

ExampleSubsystem::ExampleSubsystem() :
		frc::Subsystem("ExampleSubsystem") {

}
void ExampleSubsystem::InitDefaultCommand() {
	// Set the default command for a subsystem here.
	// SetDefaultCommand(new MySpecialCommand());
}

// Put methods for controlling this subsystem
// here. Call these from Commands.


/**
 * [CTRE]
 * To ensure hardware objects are constructed after WPILIB starts up,
 * This initialization routine should be called immedietely after
 * entering RobotInit.
 */
void ExampleSubsystem::InitHardware()
{
	_talon = new CANTalon(MY_TALON_SRX_DEVICEID);
}
/**
 * [CTRE]
 * Example setter for some mechanism.
 */
void ExampleSubsystem::SetOutputOfSomeKind(double output)
{
	// additionally could null check _talon if need be.
	// for example:  if(_talon == 0) { return; }
	_talon->Set(output);
}


