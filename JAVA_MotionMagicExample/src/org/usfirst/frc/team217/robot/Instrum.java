package org.usfirst.frc.team217.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

public class Instrum {

	private static int _loops = 0;
	
	public static void Process(CANTalon tal, StringBuilder sb)
	{
		/* smart dash plots */
    	SmartDashboard.putNumber("RPM", tal.getSpeed());
    	SmartDashboard.putNumber("Pos",  tal.getPosition());
    	SmartDashboard.putNumber("AppliedThrottle", (tal.getOutputVoltage()/tal.getBusVoltage())*1023);
    	SmartDashboard.putNumber("ClosedLoopError", tal.getClosedLoopError());
    	if (tal.getControlMode() == TalonControlMode.MotionMagic) {
			//These API calls will be added in our next release.
    		//SmartDashboard.putNumber("ActTrajVelocity", tal.getMotionMagicActTrajVelocity());
    		//SmartDashboard.putNumber("ActTrajPosition", tal.getMotionMagicActTrajPosition());
    	}
    	/* periodically print to console */
        if(++_loops >= 10) {
        	_loops = 0;
        	System.out.println(sb.toString());
        }
        /* clear line cache */
        sb.setLength(0);
	}
}
