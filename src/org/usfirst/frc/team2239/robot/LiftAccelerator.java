package org.usfirst.frc.team2239.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Timer;

public class LiftAccelerator implements Action {
	private Timer timer = new Timer();
	private WPI_TalonSRX motor;
	private double speed;
	private double timeToRun;
	
	LiftAccelerator (double time, double speed, WPI_TalonSRX motor) {
		this.motor = motor;
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
			motor.set(speed);
		} else {
			motor.set(0);
			return true;
		}
		timer.reset();
		
		
		return false;
	}
	 
}
