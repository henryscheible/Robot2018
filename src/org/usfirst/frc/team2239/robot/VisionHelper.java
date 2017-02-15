package org.usfirst.frc.team2239.robot;

public class VisionHelper { //create a class to do the math

	static public double[] getPositionToGoal(double left, double right, double spread) {
		double x1 = (left*left - right*right - spread*spread) / 2*spread; //Calculate the distance between the robot and the closer tape
		double dy = Math.sqrt(spread*spread - x1*x1); //Calculate another distance between the robot and the peg
		double dx = x1 + spread/2; //Same as x1 but right with the peg
		double[] ans = {dx, dy}; //Create an array to send the answers back to the program
		return ans; //Send answer back to the main program
	}
	static public double[] getDistanceToGoal(double dx, double dy)	{
		double l = Math.sqrt(dx*dx + dy*dy); //Calculate the hypotenuse, l, of triangle dx,dy,l
	}
}