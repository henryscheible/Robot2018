package org.usfirst.frc.team2239.robot;

import com.kauailabs.navx.frc.AHRS;

public class RotationAccelerationHelper {
	TechnoDrive driveTrain;
	AHRS navSensor;
	double maxVelocity; //the values we're driving with i.e. tankDrive(-maxVelocity, maxVelocity)
	double curVelocity = 0; //init to 0; we shouldn't be moving when we initiate
	double accelerate = .005; //how quickly @param velocity will change //should be .005
	double velocity;
	double offset = .001; //the constant to help aid the proportional control. The "b" in mx+b //should be .001
	double tolerance; //How close to the final orientation should you get before stopping (should not be 0. Perfection is impossible.)
	double topSpeed = 5; //max radians per second //should be 5
	double turnAngle; //how much to turn (in radians, positive means clockwise)
	double targetAngle; //the angle we aspire to be at. This can be greater than 2Pi
	//short for "proportional control". How fast we go should be proportional to how far we have to go. The "m" in mx+b
	double propControl = 1; //should be 1
	boolean clockwise; //whether or not the turnAngle is a clockwise angle
	
	
	/*
	 * 
	 * @params
	 * theRobot: the robot object so that has a tankDrive() method (so we can drive the robot)
	 * startTime: the time the timer is at when we start running acceleration
	 * runTime: how long you want the robot to run for
	 * maxVelocity: the biggest value you want passed into tankDrive (this should be negative if going backwards)
	 * acceleration: how quickly the robot should accelerate. (this should be negative if going backwards)
	 * emergencyAcceleration: if we've gone over the time to stop, use this acceleration to stop really quickly
	 */
	public RotationAccelerationHelper (TechnoDrive driveTrain, AHRS navSensor, double turnAngle, double maxVelocity) {
		this.clockwise = (turnAngle>0);
		this.driveTrain = driveTrain;
		this.navSensor = navSensor;
		this.turnAngle = turnAngle;
		this.targetAngle = getAngle()+turnAngle;
		this.maxVelocity = maxVelocity;
	}
	
	public boolean accelerate()
	{
		double curAngle = getAngle();
		double targetVelocity = propControl*(targetAngle-curAngle)+offset;
		
		if (targetAngle-tolerance < curAngle && curAngle < targetAngle+tolerance) { //we did it!
			return true;
		}
		
		if (clockwise) {
			if (targetVelocity > curVelocity+accelerate) { //if I'm going slower than I should, ramp up to it
				curVelocity = curVelocity+accelerate;
			} else {
				curVelocity = targetVelocity; //this handles deceleration with the proportionality stuff
			}
			curVelocity = Math.min(curVelocity, maxVelocity);
		} else {
			if (targetVelocity < curVelocity-accelerate) { //if I'm going slower than I should, ramp up to it
				curVelocity = curVelocity-accelerate;
			} else {
				curVelocity = targetVelocity; //this handles deceleration with the proportionality stuff
			}
			curVelocity = Math.max(curVelocity, maxVelocity);
		}
		driveTrain.tankDrive(curVelocity, -curVelocity); //actually drive
		return false;
	}
	
	public double getAngle() {
		return Math.toRadians(navSensor.getAngle());
	}
}