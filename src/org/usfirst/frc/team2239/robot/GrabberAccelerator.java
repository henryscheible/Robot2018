package org.usfirst.frc.team2239.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

public class GrabberAccelerator implements Action {
	private Timer timer = new Timer();
	private SpeedControllerGroup motorsToLookAt;
	private double speed;
	private double timeToRun;
	
	GrabberAccelerator (double time, double speed, SpeedControllerGroup motorsToLookAt) {
		this.motorsToLookAt = motorsToLookAt;
		this.speed = speed;
		this.timeToRun = time;
		return;
	}
	
	/**
	 * @see org.usfirst.frc.team2239.robot.Action#run()
	 * @return true if the rotation is complete or false if the rotation is not complete
	 */
	public boolean run()
	{
		
		timer.start(); 
		if (timer.get() <= timeToRun){
			motorsToLookAt.set(speed);
		} else {
			motorsToLookAt.set(0);
			return true;
		}
		timer.reset();
		
		
		return false;
	}
	 
}
