/**
 * A bare-bones test project using Pigeon for "Go-Straight" servo-ing.
 * The goal is for a basic robot with left/right side drive
 * to automatically hold a heading while driver is holding the top-left
 * shoulder button (Logitech Gamepad).
 *
 * If Pigeon is present on CANbus, or ribbon-cabled to a CAN-Talon, the robot will use the IMU to servo.
 * If Pigeon is not present, robot will simply apply the same throttle to both sides.
 *
 * When developing robot applications with IMUs, it's important to design in what happens if
 * the IMU is disconnected or un-powered.
 */
#include "WPILib.h"
#include "PigeonImu.h"
class Robot: public IterativeRobot {
    /* robot peripherals */
    CANTalon * _leftFront;
    CANTalon * _rightFront;
    CANTalon * _leftRear;
    CANTalon * _rightRear;
    CANTalon * _spareTalon; /* spare talon, remove if not necessary, Pigeon can be placed on CANbus or plugged into a Talon. */
    PigeonImu * _pidgey;
    Joystick *_driveStick;          /* Joystick object on USB port 1 */
    /** state for tracking whats controlling the drivetrain */
    enum {
        GoStraightOff, GoStraightWithPidgeon, GoStraightSameThrottle
    } _goStraight = GoStraightOff;

    /* Some gains for heading servo,
     * these were tweaked by using the web-based config (CAN Talon) and
     * pressing gamepad button 6 to load them.
     */
    double kPgain = 0.04; /* percent throttle per degree of error */
    double kDgain = 0.0004; /* percent throttle per angular velocity dps */
    double kMaxCorrectionRatio = 0.30; /* cap corrective turning throttle to 30 percent of forward throttle */
    /** holds the current angle to servo to */
    double _targetAngle = 0;
    /** count loops to print every second or so */
    int _printLoops = 0;
public:
    /**
     * Constructor for this class.
     */
    Robot() {
        _leftFront = new CANTalon(6);
        _rightFront = new CANTalon(3);
        _leftRear = new CANTalon(4);
        _rightRear = new CANTalon(1);
        _spareTalon = new CANTalon(2);

        /* choose which cabling method for Pigeon */
        //_pidgey = new PigeonImu(0); /* Pigeon is on CANBus (powered from ~12V, and has a device ID of zero */
        _pidgey = new PigeonImu(_spareTalon); /* Pigeon is ribbon cabled to the specified CANTalon. */

        /* Define joystick being used at USB port #0 on the Drivers Station */
        _driveStick = new Joystick(0);
    }

    void TeleopInit() {
        _pidgey->SetFusedHeading(0.0); /* reset heading, angle measurement wraps at plus/minus 23,040 degrees (64 rotations) */
        _goStraight = GoStraightOff;
    }

    /**
     * Gets called once for each new packet from the DS.
     */
    void TeleopPeriodic() {
        /* some temps for Pigeon API */
        PigeonImu::GeneralStatus genStatus;
        double xyz_dps[3];
        /* grab some input data from Pigeon and gamepad*/
        _pidgey->GetGeneralStatus(genStatus);
        _pidgey->GetRawGyro(xyz_dps);
        double currentAngle = _pidgey->GetFusedHeading();
        bool angleIsGood = (_pidgey->GetState() == PigeonImu::Ready) ? true : false;
        double currentAngularRate = xyz_dps[2];
        /* get input from gamepad */
        bool userWantsGoStraight = _driveStick->GetRawButton(5); /* top left shoulder button */
        double forwardThrottle = _driveStick->GetAxis(Joystick::kYAxis) * -1.0; /* sign so that positive is forward */
        double turnThrottle = _driveStick->GetAxis(Joystick::kTwistAxis) * -1.0; /* sign so that positive means turn left */
        /* deadbands so centering joysticks always results in zero output */
        forwardThrottle = Db(forwardThrottle);
        turnThrottle = Db(turnThrottle);
        /* simple state machine to update our goStraight selection */
        switch (_goStraight) {

            /* go straight is off, better check gamepad to see if we should enable the feature */
            case GoStraightOff:
                if (userWantsGoStraight == false) {
                    /* nothing to do */
                } else if (angleIsGood == false) {
                    /* user wants to servo but Pigeon isn't connected? */
                    _goStraight = GoStraightSameThrottle; /* just apply same throttle to both sides */
                } else {
                    /* user wants to servo, save the current heading so we know where to servo to. */
                    _goStraight = GoStraightWithPidgeon;
                    _targetAngle = currentAngle;
                }
                break;

            /* we are servo-ing heading with Pigeon */
            case GoStraightWithPidgeon:
                if (userWantsGoStraight == false) {
                    _goStraight = GoStraightOff; /* user let go, turn off the feature */
                } else if (angleIsGood == false) {
                    _goStraight = GoStraightSameThrottle; /* we were servoing with pidgy, but we lost connection?  Check wiring and deviceID setup */
                } else {
                    /* user still wants to drive straight, keep doing it */
                }
                break;

            /* we are simply applying the same throttle to both sides, apparently Pigeon is not connected */
            case GoStraightSameThrottle:
                if (userWantsGoStraight == false) {
                    _goStraight = GoStraightOff; /* user let go, turn off the feature */
                } else {
                    /* user still wants to drive straight, keep doing it */
                }
                break;
        }

        /* if we can servo with IMU, do the math here */
        if (_goStraight == GoStraightWithPidgeon) {
            /* very simple Proportional and Derivative (PD) loop with a cap,
             * replace with favorite close loop strategy or leverage future Talon <=> Pigeon features. */
            turnThrottle = (_targetAngle - currentAngle) * kPgain - (currentAngularRate) * kDgain;
            /* the max correction is the forward throttle times a scalar,
             * This can be done a number of ways but basically only apply small turning correction when we are moving slow
             * and larger correction the faster we move.  Otherwise you may need stiffer pgain at higher velocities. */
            double maxThrot = MaxCorrection(forwardThrottle, kMaxCorrectionRatio);
            turnThrottle = Cap(turnThrottle, maxThrot);
        } else if (_goStraight == GoStraightSameThrottle) {
            /* clear the turn throttle, just apply same throttle to both sides */
            turnThrottle = 0;
        } else {
            /* do nothing */
        }

        /* positive turnThrottle means turn to the left, this can be replaced with ArcadeDrive object, or teams drivetrain object */
        float left = forwardThrottle - turnThrottle;
        float right = forwardThrottle + turnThrottle;
        left = Cap(left, 1.0);
        right = Cap(right, 1.0);

        /* my right side motors need to drive negative to move robot forward */
        _leftFront->Set(left);
        _leftRear->Set(left);
        _rightFront->Set(-1. * right);
        _rightRear->Set(-1. * right);

        /* some printing for easy debugging */
        if (++_printLoops > 50){
            _printLoops = 0;
            printf("------------------------------------------\n");
            printf("error: %f\n", _targetAngle - currentAngle);
            printf("angle: %f\n", currentAngle);
            printf("rate: %f\n", currentAngularRate);
            printf("noMotionBiasCount: %i\n", genStatus.noMotionBiasCount);
            printf("tempCompensationCount: %i\n", genStatus.tempCompensationCount);
            printf("%s\n", angleIsGood ? "Angle is good" : "Angle is NOT GOOD");
            printf("------------------------------------------\n");
        }

        /* press btn 6, top right shoulder, to apply gains from webdash.  This can
         * be replaced with your favorite means of changing gains. */
        if (_driveStick->GetRawButton(6)) {
            UpdatGains();
        }
    }

    /** @return 10% deadband */
    double Db(double axisVal) {
        if (axisVal < -0.10)
            return axisVal;
        if (axisVal > +0.10)
            return axisVal;
        return 0;
    }
    /** @param value to cap.
     * @param peak positive double representing the maximum (peak) value.
     * @return a capped value.
     */
    double Cap(double value, double peak) {
        if (value < -peak)
            return -peak;
        if (value > +peak)
            return +peak;
        return value;
    }
    /**
     * As a simple trick, lets take the spare talon and use the web-based
     * config to easily change the gains we use for the Pigeon servo.
     * The talon isn't being used for closed-loop, just use it as a convenient
     * storage for gains.
     */
    void UpdatGains() {
        kPgain = _spareTalon->GetP();
        kDgain = _spareTalon->GetD();
        kMaxCorrectionRatio = _spareTalon->GetF();
    }
    /**
     * Given the robot forward throttle and ratio, return the max
     * corrective turning throttle to adjust for heading.  This is
     * a simple method of avoiding using different gains for
     * low speed, high speed, and no-speed (zero turns).
     */
    double MaxCorrection(double forwardThrot, double scalor) {
        /* make it positive */
        if(forwardThrot < 0) {forwardThrot = -forwardThrot;}
        /* max correction is the current forward throttle scaled down */
        forwardThrot *= scalor;
        /* ensure caller is allowed at least 10% throttle,
         * regardless of forward throttle */
        if(forwardThrot < 0.10)
            return 0.10;
        return forwardThrot;
    }
};

START_ROBOT_CLASS(Robot)
