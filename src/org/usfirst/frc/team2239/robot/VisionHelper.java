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
	
	/*
	 * @params
	 * dx inches parallel to peg base to get to peg
	 * dy inches perpendicular to peg base to get to peg
	 * returns the distance to the peg
	 */
	static public double getDistanceToGoal(double dx, double dy)	{
		return Math.sqrt(dx*dx + dy*dy); //Calculate the hypotenuse of triangle with non-hypotenuse side lengths of dx and dy.
	}
	
	//TODO reorder parameters, make sure constants are defined.
	/*
	 * @params
	 * dx inches parallel to peg base to get to peg
	 * dy inches perpendicular to peg base to get to peg
	 * middleXPixel the pixel position of the peg on the screen (the middle of the two pieces of tape). Always positive because screen coordinates are always positive
	 * pixelScreenWidth the width of the screen in pixels
	 * halfXFov half the field of view for the x (radians)
	 * away how far away (in inches) from the peg our target is in the y (perpendicular to peg base) direction
	 * returns double[3] ans
	 * ans[0] the angle to turn to point towards the target (radians)
	 * ans[1] the distance to travel to hit the target (inches)
	 * ans[2] the angle to turn to point towards the peg (radians)
	 */
	
	static public double[] getValuesToPeg(double dx, double dy, double middleXPixel, double pixelScreenWidth, double halfXFov, double away) {
		//All angles in here have a direction. Positive is clockwise.
		double dc = getDistanceToGoal(dx, dy); //compute the distance to the peg in inches
		double theta = Math.acos(dx/dc); //angle from peg to robot to the horizontal line (parallel to peg base) the robot is on
		double pixelsOffCenter = middleXPixel-.5*pixelScreenWidth; //how many pixels we are are looking from the center of the tape
		//Same as (halfXFov*pixelsOffCenter)/(.5*pixelScreenWidth)
		double alpha = (2.0*halfXFov*pixelsOffCenter)/pixelScreenWidth; //the angle in radians to turn to the peg
		System.out.println("Angle to turn to point towards the peg: "+alpha);
		double b2t = -Math.atan((dy-away)/dx); //angle from the horizontal line to the robot to the target
		double a1 = alpha+theta+b2t; //angle the robot needs to turn to point at the target
		//The reason for the sign "(b2t/Math.abs(b2t))" term is because a2 always needs to be in the direction of b2t
		double a2 = (b2t/Math.abs(b2t))*(Math.PI/2)-b2t; //angle the robot should turn after it hits the target to point towards the peg
		return new double[] {a1, dx/Math.cos(b2t), a2};
	} 
	
	
}