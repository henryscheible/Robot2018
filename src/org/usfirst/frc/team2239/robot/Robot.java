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
import edu.wpi.first.wpilibj.GenericHID;

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
	final double halfYFov = Math.toRadians(40)/2.0; //half the vertical field of vision (radians) //fiddleable (you can change this value for calibration)
	final double halfXFov = Math.toRadians(60)/2.0; //half the horizontal field of vision (radians) //fiddleable
	final double realTapeHeight = 5; //height of the strip of tape (inches)
	final double spread = 8.25; //distance between the centers of the strips of tape (inches)
	final double pixelScreenHeight = 480; //height of screen (pixels)
	final double pixelScreenWidth = 640; //width of screen (pixels)
	final double away = 12; //how many inches away from the peg you want the robot to move to before its charge. Determines target.
	XboxController controller;
	TechnoDrive drive;
	public Robot() {
		contoursTable = NetworkTable.getTable("GRIP/myContoursReport");
		blobsTable = NetworkTable.getTable("GRIP/myBlobsReport");
	}
	
	public void robotInit() {
		controller = new XboxController(0);
		drive = new TechnoDrive(4,1,3,2); //small bot	
	}
	
	public void autonomousInit() {
		
	}
	
	public void autonomousPeriodic() {
		
		/*
			Thinking for autonomous program
			if (camera is working) {
				run the camera auto chosen
			} else {
				run the non-camera auto chosen
			}
			
			I should have 1 program that assumes
			*the robot is in a position where it is looking at the tape
		 */
		
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
		
		
		
				
		double[][] contourPropertyArrays = getDataFromGRIPContours(new String[] {"width", "area"});
		double[][] blobPropertyArrays = getDataFromGRIPBlobs(new String[] {"x", "y"});
		
		//Get the contour objects 
		Contour[] contours = getContours(contourPropertyArrays, blobPropertyArrays);
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
				double distanceToContour = VisionHelper.getDistanceToTape(contour.h, pixelScreenHeight, realTapeHeight, halfYFov);
				System.out.println("Distance to contour "+distanceToContour);
				distanceToContours[i] = distanceToContour;
			}
			
			
			//static public double[] getPositionToGoal(double left, double right, double spread)
			double[] positionToGoal = VisionHelper.getPositionToGoal(distanceToContours[0], distanceToContours[1], spread);
			
			double middleXPixel = contours[0].x+((contours[1].x-contours[0].x)/2); //the pixel that the peg should be at in the photo
			
			//static public double[] getValuesToPeg(double dx, double dy, double middleXPixel, double pixelScreenWidth, double halfXFov, double away)
			double[] valuesToPeg  = VisionHelper.getValuesToPeg(positionToGoal[0], positionToGoal[1], middleXPixel, pixelScreenWidth, halfXFov, away);
			 /* ans[0] the angle to turn to point towards the target (radians)
			 * ans[1] the distance to travel to hit the target (inches)
			 * ans[2] the angle to turn to point towards the peg (radians)
			 */ 
			System.out.println("Angle to point towards the target: "+valuesToPeg[0]);
			System.out.println("distance to travel to hit the target: "+valuesToPeg[1]);
			System.out.println("the angle to turn to point towards the peg: "+valuesToPeg[2]);
			
			//double distanceToGoal = getPositionToGoal()

		} else {
			System.out.println("Did not find 2 countours. Instead, I found " + contours.length);
		}
		double leftVal = -controller.getY(XboxController.Hand.kLeft);
		double rightVal = -controller.getY(XboxController.Hand.kRight);
		System.out.println("l and r vals: "+leftVal+""+rightVal);
		drive.tankDrive(controller);
		
		//Timer.delay(.01); //TODO delete
	}
	
	public void teleopInit() {
		
	}
	
	public void teleopPeriodic() {
		double leftVal = -controller.getY(XboxController.Hand.kLeft);
		double rightVal = -controller.getY(XboxController.Hand.kRight);
		System.out.println("l and r vals: "+leftVal+""+rightVal);
		drive.tankDrive(controller);
		//drive.tankDrive(-controller.getY(XboxController.Hand.kLeft), -controller.getY(XboxController.Hand.kRight));
	}
	
	//get the requested values from the contours table posted by GRIP
	public double[][] getDataFromGRIPContours(String[] propertiesToGet) {
		double[][] ans = new double[propertiesToGet.length][];
		for (int i=0; i<propertiesToGet.length; i++) {
			String property = propertiesToGet[i];
			double[] propertyArray = contoursTable.getNumberArray(property, new double[0]);
			System.out.print("Got a propertyArray "+property+": ");
			for (double value : propertyArray) {
				System.out.print(" value: ");
				System.out.print(value);
			}
			ans[i] = propertyArray;
			System.out.println();
		}
		return ans;
	}
	
	//get the values we can from blobs
	//This is a workaround since GRIP wasn't giving us all the values we wanted
	public double[][] getDataFromGRIPBlobs(String[] propertiesToGet) {
		double[][] ans = new double[propertiesToGet.length][];
		for (int i=0; i<propertiesToGet.length; i++) {
			String property = propertiesToGet[i];
			double[] propertyArray = blobsTable.getNumberArray(property, new double[0]);
			System.out.print("Got a propertyArray "+property+": ");
			for (double value : propertyArray) {
				System.out.print(" value: ");
				System.out.print(value);
			}
			ans[i] = propertyArray;
			System.out.println();
		}
		return ans;
	}
	
	//Take all the data from GRIP and return the tape contours.
	//This is where finding the tape (and ignoring other things, including the peg covering the tape) takes place.
	public Contour[] getContours(double[][] contourPropertyArrays, double[][] blobPropertyArrays) {
		//The class/final result is called "Contours" even though it has some info from blobsTable and some from contoursTable
		Contour[] contours = new Contour[contourPropertyArrays[0].length];
		try {
			for (int i=0; i<contours.length; i++) { //for each contour
				Contour contour = new Contour(); //create default Contour with all values at default value
				contour.w = contourPropertyArrays[0][i];
				contour.area = contourPropertyArrays[1][i];
				contour.x = blobPropertyArrays[0][i];
				contour.y = blobPropertyArrays[1][i];
				contour.h = contour.area/contour.w;
				contours[i] = contour; //make the new contour and add it
			}
			
			//2 inches wide
			//5 inches tall
			//So, ratio should be 5/2
			double targetRatio = 5.0/2.0;
			double scores[] = new double[contours.length];
			for (int i=0; i<contours.length; i++) {
				Contour contour = contours[i];
				double ratio = contour.h/contour.w;
				double score = Math.abs(ratio-targetRatio);
				scores[i] = score;
			}
			
			int[] indexes = getIndexesOfLargestTwoNums(scores);
			contours = new Contour[] {contours[indexes[0]], contours[indexes[1]]};
			
			return contours;
		}
			
			
		catch (ArrayIndexOutOfBoundsException exc) {
			System.out.println("Caught the killer error! About to return.");
			return new Contour[0];
		}
	}
	
	public double getLargestNum(double[] myArr) {
	    double largest = myArr[0];
	    for(double num : myArr) {
	        if(largest < num) {
	            largest = num;
	        }
	    }
	    return largest;
	}
	
	public int[] getIndexesOfLargestTwoNums(double[] myArr) {
		if (myArr.length<2) { //if myArr has less than two doubles in it, just return an empty array.
			return new int[2];
		}
		
		int index1 = 0;
		int index2 = 1;
		double bigNum1 = myArr[0];
		double bigNum2 = myArr[1];
		
		for (int i=2; i<myArr.length; i++) {
			double num = myArr[i];
			if (num>bigNum1 || num>bigNum2) {
				if (bigNum1>bigNum2) { //bigNum2 is smaller so replace it
					bigNum2 = num;
					index2 = i;
				} else { //replace bigNum1
					bigNum1 = num;
					index1 = i;
				}
			}
		}
		return new int[] {index1, index2};
	}
	
}