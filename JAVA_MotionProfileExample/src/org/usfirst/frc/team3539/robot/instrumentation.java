/**
 * Since this example focuses on Motion Control, lets print everything related to MP in a clean 
 * format.  Expect to see something like......
 * 
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * outputEnable    topBufferRem    topBufferCnt    btmBufferCnt    IsValid     HasUnderrun      IsUnderrun          IsLast         VelOnly         targPos         targVel
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * outputEnable    topBufferRem    topBufferCnt    btmBufferCnt    IsValid     HasUnderrun      IsUnderrun          IsLast         VelOnly         targPos         targVel
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * 
 * ...where the columns are reprinted occasionally so you know whats up.
 * 
 * 
 * 
 */
package org.usfirst.frc.team3539.robot;
import com.ctre.CANTalon;

public class instrumentation {

	static double timeout = 0;
	static int count = 0;

	private static final String []_table = {" Dis "," En  ","Hold "};
	
	public static void OnUnderrun() {
		System.out.format("%s\n", "UNDERRUN");
	}
	public static void OnNoProgress() {
		System.out.format("%s\n", "NOPROGRESS");
	}
	static private String StrOutputEnable(CANTalon.SetValueMotionProfile sv)
	{
		if(sv == null)
			return "null";
		if(sv.value > 3)
			return "Inval";
		return _table[sv.value];
	}
	/** round to six decimal places */
	static private double round(double toround)
	{
		long whole = (long)(toround * 1000000.0 + 0.5);
		return ((double)whole) * 0.000001;
	}
	public static void process(CANTalon.MotionProfileStatus status1) {
		double now = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();

		if((now-timeout) > 0.2){
			timeout = now;
			/* fire a loop every 200ms */

			if(--count <= 0){
				count = 8;
				/* every 8 loops, print our columns */
				
				System.out.format("%-9s\t", "topCnt");
				System.out.format("%-9s\t", "btmCnt");
				System.out.format("%-9s\t", "set val");
				System.out.format("%-9s\t", "HasUnder");
				System.out.format("%-9s\t", "IsUnder");
				System.out.format("%-9s\t", "IsValid");
				System.out.format("%-9s\t", "IsLast");
				System.out.format("%-9s\t", "VelOnly");
				System.out.format("%-9s\t", "Pos");
				System.out.format("%-9s\t", "Vel");

				System.out.format("\n");
			}
			/* every loop, print our values */
			System.out.format("%-9s\t", status1.topBufferCnt);
			System.out.format("%-9s\t", status1.btmBufferCnt);
			System.out.format("%-9s\t", StrOutputEnable(status1.outputEnable));
			System.out.format("%-9s\t", (status1.hasUnderrun ? "1" : ""));
			System.out.format("%-9s\t", (status1.isUnderrun ? "1" : ""));
			System.out.format("%-9s\t", (status1.activePointValid ? "1" : ""));
			System.out.format("%-9s\t", (status1.activePoint.isLastPoint ? "1" : ""));
			System.out.format("%-9s\t", (status1.activePoint.velocityOnly ? "1" : ""));
			System.out.format("%-9s\t", round(status1.activePoint.position));
			System.out.format("%-9s\t", round(status1.activePoint.velocity));

			System.out.format("\n");
		}
	}
}
