package org.usfirst.frc.team2239.robot;

public class VisionHelper { //create a class to do the math

	/*
	 * @param halfFov is half the field of view in radians 
	 * 
	 */
	static public double getDistanceToTape(double pixelTapeHeight, double pixelScreenHeight, double realTapeHeight, double halfFov) {
		double realScreenHeight = (realTapeHeight*pixelScreenHeight)/pixelTapeHeight;
		return realScreenHeight/(2*Math.tan(halfFov));
	}
	
	/*
	 * height is probably in inches, which would mean our distance is in inches as well //TODO check what unit height is
	 * @param left robot's distance to the left piece of tape
	 * @param right robot's distance to the right piece of tape
	 * @param spread the real distance between the two pieces of tape.
	 */
	static public double[] getPositionToGoal(double left, double right, double spread) {
		//Calculate the parallel (to the peg's wall) distance between the robot and the closer tape
		double x1 = (left*left - right*right - spread*spread) / 2*spread;
		double dy = Math.sqrt(spread*spread - x1*x1); //Calculate perpendicular distance between the robot and the peg
		double dx = x1 + spread/2; //Same as x1 but with the peg instead of tape
		double[] ans = {dx, dy}; //Create an array to send the answers back to the program
		return ans; //Send answer back to the main program
	}
	
	static public double getDistanceToGoal(double dx, double dy)	{
		return Math.sqrt(dx*dx + dy*dy); //Calculate the hypotenuse, l, of triangle dx,dy,l
	}
	
	//TODO reorder parameters, make sure constants are defined.
	static public double angleToGoal(double dx, double dy, double l, double away, double pixelScreenWidth, double middleXPixel, double halfFov) {
		double beta = Math.acos(dx/l);
		double gamma = Math.atan((dy-away)/dx);
		double theta = (1-(2*middleXPixel/pixelScreenWidth))*halfFov;
		//how far we need to turn counterclockwise assuming that out if our calc gives negative dx when we are right of the target
		double alpha1 = theta+gamma-beta; 
		double driveDistance = Math.sqrt((Math.pow(dx, 2)+Math.pow(dy-away, 2)));
		double alpha2 = Math.toRadians(90) - gamma;
	}
	
	
}