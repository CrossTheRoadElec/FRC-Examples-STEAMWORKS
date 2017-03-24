/**
 * @brief A quick example on higher resolution plotting a sensor for testing.
 * 
 * Simple example for plotting sensor
 * Sensor is a CTRE Magnetic Encoder plugged into a Talon SRX via Gadgeteer Ribbon Cable.
 * Robot should be propped up on blocks so that the wheels spin free (if testing a drive train sensor).
 * 
 * Talon SRX ...
 * http://www.ctr-electronics.com/talon-srx.html
 * Magnetic Encoder...
 * http://www.ctr-electronics.com/srx-magnetic-encoder.html
 * Cables...
 * http://www.ctr-electronics.com/talon-srx-data-cable-4-pack.html#product_tabs_related_tabbed
 * http://www.ctr-electronics.com/talon-srx-data-cable-kit-new-product.html
 * 
 * SmartDashboard (SD) setup.
 * [1] Open Smartdashboard (I typically (re)select the Dashboard Type in DriverStation if the SD doesn't pop up).
 * [2] Deploy software and enable.
 * [3] Find the text entry in the SD for "spd".  
 * [4] View =>Editable should be checked.
 * [5] Right-click on "spd" label and "Change to..." the Line Plot.  
 * 
 * A few details regarding Smartdashboard in general...
 * [1] Constant data does not render new plot points. So if the signal being measured doesn't change value, the plot stops.
 * Once the signal changes again the plot resumes but the time gap between is truncated in the plot.
 * [2] Changing the window of samples is done by View=>Editable=>Check, then right click-properties on the plot.
 * 		Then change "Buffer Size" in the popup.   I find myself changing this often as I learn more about the signal I am viewing.
 * [3] Zoom features will cause the plot to stop updating and I haven't found a quick way to get the plot to resume plotting.  So 
 * 		I've been avoiding Zoom-In/Zoom-Out for now.
 * [4] Right-click properties on the plot does different things depending on if View=>Editable is checked.
 *  
 * @author Ozrien
 */
package org.usfirst.frc.team3539.robot; // bulldogs!

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
	CANTalon _tal1 = new CANTalon(1); //!< Just a follower
	CANTalon _tal3 = new CANTalon(3);	
	PlotThread _plotThread;
	
	public void teleopInit() {
		/* Tal1 will follow Tal3 */
		_tal1.changeControlMode(TalonControlMode.Follower);
		_tal1.set(3);
		
		/* new frame every 1ms, since this is a test project use up as 
		 * much bandwidth as possible for the purpose of this test. */
		_tal3.setStatusFrameRateMs(StatusFrameRate.Feedback, 1); 
		_tal3.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		/* fire the plotter */
		_plotThread = new PlotThread(this);
		new Thread(_plotThread).start();
	}

	public void teleopPeriodic() {
		/* Shooting for ~200RPM, which is ~300ms per rotation.
		 *   
		 * If there is mechanical deflection, eccentricity, or damage in the sensor
		 * it should be revealed in the plot.  
		 * 
		 * For example, an optical encoder with a partially damaged ring will reveal a 
		 * periodic dip in the sensed velocity synchronous with each rotation.
		 * 
		 *  This can also be wired to a gamepad to test velocity sweeping.
		 * */
		_tal3.set(0.4);		
	}
	
	/** quick and dirty threaded plotter */
	class PlotThread implements Runnable {
		 Robot robot;
		 public PlotThread(Robot robot) { this.robot = robot; }

		 public void run() {
			/* speed up network tables, this is a test project so eat up all 
			 * of the network possible for the purpose of this test.
			 */
			NetworkTable.setUpdateRate(0.010); /* this suggests each time unit is 10ms in the plot */
			while (true) {
				/* yield for a ms or so - this is not meant to be accurate */
				try { Thread.sleep(1); } catch (Exception e) { }
				/* grab the last signal update from our 1ms frame update */
				double speed = this.robot._tal3.getSpeed();
				SmartDashboard.putNumber("spd", speed);
			}
		}
	}
}

