package org.usfirst.frc.team2239.robot;

public class AccelerationHelper {
	TechnoDrive theRobot;
	double maxVelocity;
	double acceleration;
	double emergencyAcceleration;
	double middleTime;
	double endTime;
	double curVelocity;
	boolean forwards;
	
<<<<<<< Upstream, based on branch 'master' of https://github.com/Technocrats2239/Robot2017.git
	public AccelerationHelper (TechnoDrive theRobot, double startTime, double runTime, double maxVelocity) {
		//TODO calculate acceleration from these values and use it
		this.startTime = startTime;
		this.runTime = runTime;
		this.acceleration = acceleration;
		this.maxVelocity = maxVelocity;
		this.curVelocity = 0; //init to  0; we shouldn't be moving when we initiate
=======
	
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
	public AccelerationHelper (TechnoDrive theRobot, double startTime, double runTime, double maxVelocity, double acceleration, double emergencyAcceleration) {
		//set defaults
		if (maxVelocity==-1) maxVelocity = .5;
		if (acceleration == -1) acceleration = .005;
		if (emergencyAcceleration==-1) emergencyAcceleration = .01;
		
>>>>>>> c106c96 accelerationHelper made but still needs to deal with accelerating early
		this.theRobot = theRobot;
		this.maxVelocity = maxVelocity;
		this.acceleration = acceleration;
		this.emergencyAcceleration = emergencyAcceleration;
		this.curVelocity = 0; //init to 0; we shouldn't be moving when we initiate
		this.middleTime = startTime+runTime/2.0;
		this.endTime = startTime+runTime;
		this.forwards = acceleration>0 ? true : false; //if acceleration is greater than 0, set this.forwards to true, otherwise set it to false
	}
	
	//make a new function called accelerate which takes
	//double curTime (the current time gotten from the timer)
	//
	//check to see if the time is less than the middle time
	//if so, increase curVelocity by acceleration
	//if not, decrease by acceleration
	//Either way, make sure we don't go below 0 or over maxVelocity
	//Drive by curVelocity
	public void accelerate(double curTime)
	{
		if (curTime < middleTime) //if in the first half of the trip
		{
			if (forwards) {
				curVelocity = Math.min(maxVelocity, curVelocity + acceleration); //accelerate up to max
			} else {
				curVelocity = Math.max(maxVelocity, curVelocity + acceleration); //accelerate up to max
			}
			
		} else if (curTime < endTime) { //if in the second half of the trip
			if (forwards) {
				curVelocity = Math.max(0, curVelocity - acceleration); //decelerate to 0
			} else {
				curVelocity = Math.min(0, curVelocity - acceleration); //decelerate to 0
			}
		} else { //past the time that we should have stopped
			if (forwards) {
			curVelocity = Math.max(0, curVelocity - emergencyAcceleration); //decelerate super quickly to 0 using emergencyAcceleration
			} else {
				curVelocity = Math.min(0, curVelocity - emergencyAcceleration); //decelerate super quickly to 0 using emergencyAcceleration
			}
		}
		
		theRobot.tankDrive(curVelocity, curVelocity); //drive the bot
	}
	
}
