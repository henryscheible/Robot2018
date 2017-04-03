package org.usfirst.frc.team2239.robot;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;

public class GearStateMachine {
	
	public Accelerator[] futureAccelerators; //TODO delete old comments about state
	public double attackAngle; //Angle (radians) the physical robot must be within from the peg in order to start charging
	public double chargeDist; //Distance (inches) the physical robot must be within from the peg in order to start charging
	public TechnoDrive driveTrain;
	public AHRS navSensor;
	public CANTalon[] motorsToLookAt;
	public double overChargeAmt = 0; //How many inches it should try to overcharge when placing a gear
	
	public GearStateMachine(TechnoDrive driveTrain, AHRS navSensor, CANTalon[] motorsToLookAt) {
		this.driveTrain = driveTrain;
		this.navSensor = navSensor;
		this.motorsToLookAt = motorsToLookAt;
	}
	
	/*
		Get a good view
		if (theta (the angle the between the robot, the peg and the wall the peg is on) (a.k.a. anti-attack angle) is big enough) {
			if (close enough) {
				point at the peg
				charge towards the peg
			} else {
				go halfway to the peg (or go to approach point)
				point towards peg
				restart
			}
			
		} else {
			if (too far away) {
				point towards the approach point (also called "away" or "away point")
				move halfway to the approach point (or go to approach point)
				point towards peg
				restart
			} else {
				move to the approach point (usually about 3 feet out from the point of the peg)
				point towards the peg again
				restart
			}
		}
	*/
	
	//RotationAccelerator (TechnoDrive driveTrain, AHRS navSensor, double turnAngle, double maxVelocity)
	//EncoderAccelerator (TechnoDrive driveTrain, CANTalon[] motorsToLookAt, double distance, double maxVelocity) {

	
	public Accelerator move(double theta, double distToPeg, double rotationToPeg) {	
		if (futureAccelerators.length==0) {
			if (((Math.PI/2)-theta)<=attackAngle) {
				if (distToPeg<chargeDist) {
					futureAccelerators = new Accelerator[] {new EncoderAccelerator(driveTrain, motorsToLookAt, distToPeg+overChargeAmt, .8)};
					return new RotationAccelerator(driveTrain, navSensor, rotationToPeg, 1);
				}
			}
		} else {
			Accelerator[] newFutureAccelerators = new Accelerator[futureAccelerators.length-1];
			for (int i=1; i<futureAccelerators.length; i++) {
				newFutureAccelerators[i-1] = futureAccelerators[i];
			}
			return futureAccelerators[0];
		}
		return null;
	}
}
