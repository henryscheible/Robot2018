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
	final double halfFov = Math.toRadians(20); //half the field of vision (radians)
	final double realTapeHeight = 5; //height of the strip of tape (inches)
	final double spread = 8.25; //distance between the centers of the strips of tape (inches)
	final double pixelScreenHeight = 480; //height of screen (pixels)
	final double pixelScreenWidth = 640; //width of screen (pixels)
	final double 
	
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
		
		String[] propertiesToGet;
		
		//TODO test getting rid of area
		//get the values we can from contours
		propertiesToGet = new String[] {"width", "area"};
		double[][] contourPropertyArrays = new double[propertiesToGet.length][];
		for (int i=0; i<propertiesToGet.length; i++) {
			String property = propertiesToGet[i];
			double[] propertyArray = contoursTable.getNumberArray(property, defaultValue);
			System.out.print("Got a propertyArray "+property+": ");
			for (double value : propertyArray) {
				System.out.print(" value: ");
				System.out.print(value);
			}
			contourPropertyArrays[i] = propertyArray;
			System.out.println();
		}
		
		//get the values we can from blobs
		propertiesToGet = new String[] {"x", "y"};
		double[][] blobPropertyArrays = new double[propertiesToGet.length][];
		for (int i=0; i<propertiesToGet.length; i++) {
			String property = propertiesToGet[i];
			double[] propertyArray = blobsTable.getNumberArray(property, defaultValue);
			System.out.print("Got a propertyArray "+property+": ");
			for (double value : propertyArray) {
				System.out.print(" value: ");
				System.out.print(value);
			}
			blobPropertyArrays[i] = propertyArray;
			System.out.println();
		}
		
		
		//The class/final result is called "Contours" even though it has some info from blobsTable and some from contoursTable
		Contour[] contours = new Contour[contourPropertyArrays[0].length];
		for (int i=0; i<contours.length; i++) { //for each contour
			Contour contour = new Contour(); //create default Contour with all values at default value
			contour.w = contourPropertyArrays[0][i];
			contour.area = contourPropertyArrays[1][i];
			contour.x = blobPropertyArrays[0][i];
			contour.y = blobPropertyArrays[1][i];
			contour.h = contour.area/contour.w;
			contours[i] = contour; //make the new contour and add it
		}
		
		
		//set up the contours array
		System.out.println("contourAmount is: "+contours.length);
		
		if (contours.length==2) {
			System.out.println("Found the tape!");
			System.out.println(contours[0]);
			System.out.println(contours[1]);
			
			//Sort contours so that the one on the left comes first
			Arrays.sort(contours, new Comparator<Contour>() {
			    public int compare(Contour c1, Contour c2) {
			        return Double.compare(c1.x, c2.x);
			    }
			});
			System.out.println("x value of contour on left: "+contours[0].x);
			System.out.println("x value of contour on right: "+contours[1].x);
			
			double[] distanceToContours = new double[2];
			
			for (int i=0; i<2; i++) {
				Contour contour = contours[i];
				//getDistanceToTape(double pixelTapeHeight, double pixelScreenHeight, double realTapeHeight, double halfFov)
				double distanceToContour = VisionHelper.getDistanceToTape(contour.h, pixelScreenHeight, realTapeHeight, halfFov);
				System.out.println("Distance to contour "+distanceToContour);
				distanceToContours[i] = distanceToContour;
			}
			
			double distanceToGoal = getPositionToGoal()

		} else {
			System.out.println("Did not find 2 countours. Instead, I found " + contours.length);
		}
		
		
		Timer.delay(1); //TODO delete
	}
	
	public void teleopInit() {
		
	}
	
	public void teleopPeriodic() {
		
	}
}