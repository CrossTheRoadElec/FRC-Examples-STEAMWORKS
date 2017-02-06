#ifndef EXAMPLE_SUBSYSTEM_H
#define EXAMPLE_SUBSYSTEM_H

#include <Commands/Subsystem.h>
#include "CANTalon.h" // [CTRE]

class ExampleSubsystem: public frc::Subsystem {
public:
	ExampleSubsystem();
	void InitDefaultCommand() override;

	/**
	 * [CTRE]
	 * To ensure hardware objects are constructed after WPILIB starts up,
	 * This initialization routine should be called immediately after
	 * entering RobotInit.
	 */
	void InitHardware();
	/**
	 * [CTRE]
	 * Example setter for some mechanism.
	 */
	void SetOutputOfSomeKind(double output);

private:
	// It's desirable that everything possible under private except
	// for methods that implement subsystem capabilities

	CANTalon * _talon = 0; // [CTRE] default to zero. @see InitHardware for constructor.
};

#endif  // EXAMPLE_SUBSYSTEM_H
