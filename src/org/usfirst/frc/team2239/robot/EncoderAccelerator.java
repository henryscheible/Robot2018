package org.usfirst.frc.team2239.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice; //TODO delete if not needed

//TODO finish documentation and changing this from rotationAccelerator into the EncoderAccelerator
//TODO upgrade (this is just something that doesn't need to be done, but would make the program better.) Make a Accelerator class or framework that both of these are just versions of.

public class EncoderAccelerator {
	TechnoDrive driveTrain;
	CANTalon[] valueMotors;
	//the biggest values we're driving with i.e. tankDrive(-maxVelocity, maxVelocity).
	//Must be in between -1 and 1. Negative if going counter-clockwise
	double maxVelocity;
	double curVelocity = 0; //init to 0; we shouldn't be moving when we initiate //positive if moving forwards
	double rollPastDecrease = .1; //how much we decrease maxVelocity by if we overshoot.
	double accelerate = .05; //how quickly @param velocity will change //should be .005
	double offset = .5; //the lowest power the motors should ever be at //always positive
	double tolerance = 3; //How close to the final destination should you get before stopping (should not be 0. Perfection is impossible.)
	double turnAngle; //how much to move, in inches (positive means forwards)
	double targetDistance; //the encoder value we aspire to be at when done.
	double maxVelocityAngle = 30; //The distance travelled at which we start to decrease velocity at //always positive
	boolean forward; //whether or not the turnAngle is a clockwise angle
	
	
	
	public EncoderAccelerator (TechnoDrive driveTrain, CANTalon[] motorsToLookAt, double distance, double maxVelocity) {
		this.forward = (distance>0);
		this.driveTrain = driveTrain;
		this.valueMotors = motorsToLookAt;
		this.turnAngle = distance;
		this.targetDistance = getEncoderValue()+distance;
		this.maxVelocity = maxVelocity;
	}
	
	
	//returns true if the rotation is complete
	//returns false if the rotation is not complete
	public boolean accelerate()
	{
		System.out.println("Im actually rotating!");
		double curValue = getEncoderValue();
		double offAngle = (targetDistance-curValue);
		System.out.println("I'm this far off: "+offAngle);
		if (targetDistance-tolerance < curValue && curValue < targetDistance+tolerance) { //we did it!
			driveTrain.tankDrive(0, 0); //stop driving
			return true;
		}
		
		boolean shouldBeClockwise = offAngle>0;
		if (shouldBeClockwise!=forward) {
			maxVelocity = Math.max(maxVelocity - rollPastDecrease, offset);
			curVelocity = 0; //stop it from swinging past
			System.out.println("Swung past the target!");
		}
		forward = shouldBeClockwise;
		
		double targetVelocity;
		if (forward) {
			targetVelocity = Math.min(((maxVelocity-offset)/maxVelocityAngle)*offAngle+offset, maxVelocity);
		} else {
			targetVelocity = Math.max(((maxVelocity-offset)/maxVelocityAngle)*offAngle-offset, -maxVelocity);
		}
		
		System.out.println("Target velocity before setting is: "+targetVelocity);
		System.out.println("cutVelocity before setting is: "+curVelocity);
		if (forward) {
			System.out.println("We're going clockwise");
			if (targetVelocity > curVelocity+accelerate) { //if I'm going slower than I should, ramp up to it
				curVelocity = curVelocity+accelerate;
			} else {
				curVelocity = targetVelocity; //this handles deceleration with the proportionality stuff
			}
			curVelocity = Math.min(curVelocity, maxVelocity);
		} else {
			System.out.println("We're going counterclockwise");
			if (targetVelocity < curVelocity-accelerate) { //if I'm going slower than I should, ramp up to it
				curVelocity = curVelocity-accelerate;
			} else {
				curVelocity = targetVelocity; //this handles deceleration with the proportionality stuff
			}
			curVelocity = Math.max(curVelocity, -maxVelocity);
		}
		System.out.println("Target velocity is: "+targetVelocity);
		System.out.println("Actually driving at: " + curVelocity);
		driveTrain.tankDrive(curVelocity, -curVelocity); //actually drive
		return false;
	}
	
	public double getEncoderValue() {
		//TODO don't just average - also check to see if any of the encoders are way off or may be broken and ignore those ones.
		double sum = 0;
		for (CANTalon motor : this.valueMotors) {
			sum+=motor.getEncPosition();
		}
		double avg = sum/this.valueMotors.length; //compute the average encoder value
		System.out.println("the average encoder values from sensors: " + avg);
		return avg;
		 
	}
}
