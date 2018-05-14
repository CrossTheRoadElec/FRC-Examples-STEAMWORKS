#include "ExampleCommand.h"

ExampleCommand::ExampleCommand() {
    // Use Requires() here to declare subsystem dependencies
    // eg. Requires(Robot::chassis.get());
}

// Called just before this Command runs the first time
void ExampleCommand::Initialize() {

}

// Called repeatedly when this Command is scheduled to run
void ExampleCommand::Execute() {

    /* [CTRE] Example use for this command, this command will fire in auton. */
    CommandBase::exampleSubsystem.get()->SetOutputOfSomeKind(-0.10);
}

// Make this return true when this Command no longer needs to run execute()
bool ExampleCommand::IsFinished() {
    return false;
}

// Called once after isFinished returns true
void ExampleCommand::End() {

}

// Called when another command which requires one or more of the same
// subsystems is scheduled to run
void ExampleCommand::Interrupted() {

}
