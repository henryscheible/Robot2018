package org.usfirst.frc.team2239.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DriverStation;
import com.kauailabs.navx.frc.AHRS;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	NetworkTable table;
	final double halfFOV = Math.toRadians(30); //half the field of vision (radians)
	final double realTapeHeight = 5; //height of the strip of tape (inches)
	
	public Robot() {
		table = NetworkTable.getTable("GRIP/myContoursReport");
	}
	
	public void robotInit() {
		
	}
	
	public void autonomousInit() {
		
	}
	
	public void autonomousPeriodic() {
		/*
		double[] defaultValue = new double[0];
		SmartDashboard.putNumber("Running", (int )(Math.random() * 100 + 1));
		double[] areas = table.getNumberArray("area", defaultValue);
		SmartDashboard.putNumber("Areas table length", areas.length);
		System.out.print("areas:");
		for (double area : areas) {
			System.out.print(" area: ");
			System.out.print(area);
			SmartDashboard.putNumber("Area", area);
		}
		System.out.println();
		*/
		
		//TODO test getting rid of area
		String[] valuesToGet = new String[] {"x", "y", "width", "height", "area"};
		Contour[] contours;
		int contourAmount = 0;
		double[] defaultValue = new double[0];
		double[][] allPropertyArrays = new double[valuesToGet.length][];
		for (int i=0; i<valuesToGet.length; i++) {
			String property = valuesToGet[i];
			double[] propertyArray = table.getNumberArray(property, defaultValue);
			allPropertyArrays[i] = propertyArray;
			if (i==0) { //just got the first array, so we now know how many contours we have
				contourAmount = propertyArray.length;
			}
		}
		
		//set up the contours array
		contours = new Contour[contourAmount];
		for (int i=0; i<contourAmount; i++) { //for each contour
			//grab its properties, create it, and add it to contours
			
			
			contours[i] = new Contour(); //add a blank contour to the contours array
		}
		
		
		double[] tapeHeightsDefault = new double[0];
		double[] tapeHeights = table.getNumberArray("height", tapeHeightsDefault);
		if (tapeHeights.length==2) {
			System.out.println("Found the tape!");
			//TODO make sure it knows the difference between the right tape and the left tape
			//double distToRightTape
		} else {
			System.out.println("Did not find 2 countours. Instead, I found " + tapeHeights.length);
		}
		
		
		
		//Timer.delay(1); //TODO delete
	}
	
	public void teleopInit() {
		
	}
	
	public void teleopPeriodic() {
		
	}
}