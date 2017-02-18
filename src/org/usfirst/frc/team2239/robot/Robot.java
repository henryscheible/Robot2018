package org.usfirst.frc.team2239.robot;

import java.util.Comparator;
import java.util.Arrays;
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
	NetworkTable contoursTable;
	NetworkTable blobsTable; 
	final double halfFov = Math.toRadians(30); //half the field of vision (radians)
	final double realTapeHeight = 5; //height of the strip of tape (inches)
	final double pixelScreenHeight = 360;
	
	public Robot() {
		contoursTable = NetworkTable.getTable("GRIP/myContoursReport");
		blobsTable = NetworkTable.getTable("GRIP/myBlobsReport");
	}
	
	public void robotInit() {
		
	}
	
	public void autonomousInit() {
		
	}
	
	public void autonomousPeriodic() {
		
		double[] defaultValue = new double[0];
		
		
		SmartDashboard.putNumber("Running", (int )(Math.random() * 100 + 1));
		double[] areas = contoursTable.getNumberArray("area", defaultValue);
		SmartDashboard.putNumber("Areas table length", areas.length);
		System.out.print("areas:");
		for (double area : areas) {
			System.out.print(" area: ");
			System.out.print(area);
			SmartDashboard.putNumber("Area", area);
		}
		System.out.println();
		
		
		//TODO test getting rid of area
		String[] propertiesToGet = new String[] {"centerX", "centerY", "width", "height", "area"};
		Contour[] contours;
		int contourAmount = 0;
		double[][] allPropertyArrays = new double[propertiesToGet.length][];
		for (int i=0; i<propertiesToGet.length; i++) {
			String property = propertiesToGet[i];
			double[] propertyArray = contoursTable.getNumberArray(property, defaultValue);
			System.out.print("Got a propertyArray "+property+": ");
			for (double value : propertyArray) {
				System.out.print(" value: ");
				System.out.print(value);
			}
			allPropertyArrays[i] = propertyArray;
			if (i==0) { //just got the first array, so we now know how many contours we have
				contourAmount = propertyArray.length;
			}
			System.out.println();
		}
		
		propertiesToGet = new String[] {"x", "y"};
		allPropertyArrays = new double[propertiesToGet.length][];
		for (int i=0; i<propertiesToGet.length; i++) {
			String property = propertiesToGet[i];
			double[] propertyArray = blobsTable.getNumberArray(property, defaultValue);
			System.out.print("Got a propertyArray "+property+": ");
			for (double value : propertyArray) {
				System.out.print(" value: ");
				System.out.print(value);
			}
		}
		
		//set up the contours array
		System.out.println("contourAmount is: "+contourAmount);
		/*
		contours = new Contour[contourAmount];
		for (int i=0; i<contourAmount; i++) { //for each contour
			double[] contourProperties = new double[propertiesToGet.length]; //set up its property array
			//grab its properties, create it, and add it to contours
			for (int j=0; j<contourProperties.length; j++) {
				contourProperties[j] = allPropertyArrays[i][j]; //get the each property and add it to the array
			}
			contours[i] = new Contour(contourProperties); //make the new contour and add it
		}
		
		if (contours.length==2) {
			System.out.println("Found the tape!");
			//Sort contours so that the one on the left comes first
			Arrays.sort(contours, new Comparator<Contour>() {
			    public int compare(Contour c1, Contour c2) {
			        return Double.compare(c1.x, c2.x);
			    }
			});
			System.out.println("x value of contour on left: "+contours[0].x);
			System.out.println("x value of contour on right: "+contours[1].x);
			
			for (Contour contour : contours) {
				//getDistanceToTape(double pixelTapeHeight, double pixelScreenHeight, double realTapeHeight, double halfFov)
				double distanceToContour = VisionHelper.getDistanceToTape(contour.h, pixelScreenHeight, realTapeHeight, halfFov);
				System.out.println("Distance to contour "+distanceToContour);
			}

		} else {
			System.out.println("Did not find 2 countours. Instead, I found " + contours.length);
		}
		*/
		
		
		Timer.delay(1); //TODO delete
	}
	
	public void teleopInit() {
		
	}
	
	public void teleopPeriodic() {
		
	}
}