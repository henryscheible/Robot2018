package org.usfirst.frc.team2239.robot;

public class AccelerationHelper {
	double startTime;
	double runTime;
	double acceleration;
	double maxVelocity;
	double curVelocity;
	TechnoDrive theRobot;
	
	public AccelerationHelper (TechnoDrive theRobot, double startTime, double runTime, double maxVelocity) {
		//TODO calculate acceleration from these values and use it
		this.startTime = startTime;
		this.runTime = runTime;
		this.acceleration = acceleration;
		this.maxVelocity = maxVelocity;
		this.curVelocity = 0; //init to 0; we shouldn't be moving when we initiate
		this.theRobot = theRobot;
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
		if (curTime < runTime/2.0)
		{
			curVelocity = Math.min(maxVelocity, curVelocity + acceleration);
		}
		else
		{
			curVelocity = Math.min(0, curVelocity - acceleration);
		}
		theRobot.tankDrive(curVelocity, curVelocity);
		
	}
	//inside the function:
	
}
