package org.usfirst.frc.team2239.robot;

/*
 * Jess controls:
 * Left on arrows should do 180 degree turn
 * Right on arrows should be cancel autonomous
 */

//TODO double check for double imports
//TODO check for unused imports (don't just delete them; think about if what was using them is missing)
import edu.wpi.first.wpilibj.IterativeRobot;
import java.util.Comparator;
import java.util.Arrays;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser; //default FRC code; leave for now
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DriverStation;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.PowerDistributionPanel;





/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	NetworkTable table;
	NetworkTable contoursTable;
	NetworkTable blobsTable; 
	final double halfYFov = Math.toRadians(40)/2.0; //half the vertical field of vision (radians) //fiddleable (you can change this value for calibration)
	final double halfXFov = Math.toRadians(60)/2.0; //half the horizontal field of vision (radians) //fiddleable
	final double realTapeHeight = 5; //height of the strip of tape (inches)
	final double spread = 8.25; //distance between the centers of the strips of tape (inches)
	final double pixelScreenHeight = 480; //height of screen (pixels)
	final double pixelScreenWidth = 640; //width of screen (pixels)
	final double away = 12; //how many inches away from the peg you want the robot to move to before its charge. Determines target.
	
	public static Robot instance;
    //from http://wpilib.screenstepslive.com/s/4485/m/26401/l/255419-choosing-an-autonomous-program-from-smartdashboard

    public TechnoDrive drive;  // class that handles basic drive operations
    public Timer timer; // Timer
    public XboxController controller; //Control for the robot
    public CANTalon climber;
    public Solenoid gearRelease;
    public Compressor myCompressor;
    public PowerDistributionPanel myPDP;
    
    /*FRC default code - keep here for now
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	*/
	
	AHRS navSensor; //The navigation sensor object
	int toggleAmt = 3; //how many different buttons are toggling
	boolean[] toggleReadys = new boolean[toggleAmt]; //{speedToggleReady, gearToggleReady, turnToggle}
	boolean gearOpen = false;
	RotationAccelerator rotator = null;
	double speed = 1;
	AccelerationHelper baseline;
	//drive = new TechnoDrive(4,1,3,2);//small bot
	CANTalon frontLeftMotor = new CANTalon(4);
	CANTalon rearLeftMotor = new CANTalon(1);
	CANTalon frontRightMotor = new CANTalon(3);
	CANTalon rearRightMotor = new CANTalon(2);
		
	//This is the constructor. Whenever a new Robot object is made
	//this is the function that will be called to make it
	public Robot() {
		contoursTable = NetworkTable.getTable("GRIP/myContoursReport");
		blobsTable = NetworkTable.getTable("GRIP/myBlobsReport");
	}
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		System.out.println("Robot has started init!");
		//Default all the "ready"s to true. No buttons should be pressed at the start, therefore all should be ready to be pressed.
		for (int i=0; i<toggleReadys.length; i++) {
			toggleReadys[i] = true;
		}
		
		/*FRC default code - keep here for now
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		*/
		
		myCompressor = new Compressor(6); 
		myCompressor.setClosedLoopControl(true);
		//gearRelease = new Solenoid(CAN ID on dashboard, channel on PCM (what's it plugged into));
		//gearRelease = new Solenoid(6, 0); //TODO SpiderBot
		gearRelease = new Solenoid(7, 0);
		gearRelease.set(false); //TODO figure out if false means closed or open
		
		
		/*
		MOTORS:
			1- Back Left
			2- Back Right
			3- Front Right
			4- Front Left

			5- Climber
		*/
		//public TechnoDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
		//drive = new TechnoDrive(2, 3, 1, 4);//TODO SpiderBot
		drive = new TechnoDrive(4,1,3,2);//small bot
		timer = new Timer();
		controller = new XboxController(0);
		climber = new CANTalon(5);
		myPDP = new PowerDistributionPanel();
		//myPDP.getVoltage();
		try {
			navSensor = new AHRS(SPI.Port.kMXP); /* Alternatives: SerialPort.Port.kMXP, I2C.Port.kMXP or SerialPort.Port.kUSB */
		} catch (RuntimeException ex) {
			DriverStation.reportError("Error instantiating navX-MXP: " + ex.getMessage(), true);
		}
		
		makeMotorsUseEncoders(new CANTalon[] {rearRightMotor, rearLeftMotor});
		
		
		System.out.println("Robot has finished init");
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	
	
	@Override
	public void autonomousInit() {
		/* default FRC code; leave it for now
		autoSelected = chooser.getSelected();
		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		*/
		navSensor.reset();
		
		//TechnoDrive theRobot, double startTime, double distance, double maxVelocity
		//baseline = new AccelerationHelper(drive, timer.get(), 167.0, .7); //The old timed acceleration //TODO delete once we get encoder acceleration to work 
		timer.start(); //TODO delete once we get encoder working (though this may be useful to figure out when to do a last second charge)
		myCompressor.start();
	}
	
	

	/**
	 * This function is called periodically during autonomous
	 */
	
	
	@Override
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
		
		//TODO put in the nice driving function
			
		/*FRC default code - keep here for now
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// put custom default here
			break;
		}
		*/
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		System.out.println("Running teleop periodic!");
		if (rotator == null) {
			System.out.println("rotator is null");
		} else {
			System.out.println(rotator.curVelocity);
		}
		
		System.out.println("How fast the rearRightMotor is going: "+rearRightMotor.getEncVelocity());
		System.out.println("How far the rearRightMotor has gone: "+rearRightMotor.getEncPosition());
		
		System.out.println("How fast the rearLeftMotor is going: "+rearLeftMotor.getEncVelocity());
		System.out.println("How far the rearLeftMotor has gone: "+rearLeftMotor.getEncPosition());
		
		//SmartDashboard.putBoolean("Trigger", toggleReadys[0]); //TODO uncomment
		double leftVal = -controller.getY(XboxController.Hand.kLeft);
        double rightVal = -controller.getY(XboxController.Hand.kRight);
        
     
        boolean[] triggers = new boolean[toggleAmt];
        //Go through each toggle 
        //Set all the values in triggers appropriately.
        for (int index = 0; index<toggleAmt; index++) {
        	//0: Speed changing
        	//1: Gear
        	//2: Auto turn
        	
        	//see if the buttons are pushed down or not
        	switch (index) {
	            case 0: triggers[index] = controller.getTrigger(XboxController.Hand.kLeft) || controller.getTrigger(XboxController.Hand.kRight);
	            		break;
	            case 1: triggers[index] = controller.getRawButton(5) || controller.getRawButton(6);
	            		break;
	            case 2: triggers[index] = controller.getSimplePOV() == 6;
	            		break;
	            default:
	            	break; //we are no longer using this toggle button
        	}
        	
        	//actually update/run
        	boolean isReady = toggleReadys[index]; //is this button ready to be activated/pressed down
        	boolean isTriggered = triggers[index];
	    	if (isReady){
	    		if (isTriggered) { //button is down and this is the first time I've noticed
	    			//fire the trigger; the button has been pressed!
	 	        	switch (index) {
			            case 0:
	         	        	if (speed==1) {
	         	        		speed = .7;
	         	        	} else {
	         	        		speed = 1;
	         	        	}
	         	        	break;
		         	    
			            case 1:
			            	if (gearOpen) {
			            		//TODO if these are changed, make sure the pipes are switched on SpiderBot
				        		gearRelease.set(false); //close it //TODO make sure these are accurate
				        		gearOpen = false;
				        	} else {
				        		gearRelease.set(true); //open it
				        		gearOpen = true;
				        	}
			            	System.out.println("gearRelease is value is "+gearOpen); //TODO delete
			            	break;
        
			            case 2:
			            	System.out.println("Starting a new rotation!");
			            	double turnAngle = 180;
			            	//public RotationAccelerationHelper (TechnoDrive driveTrain, AHRS navSensor, double turnAngle, double maxVelocity) 
			            	rotator = new RotationAccelerator(drive, navSensor, turnAngle, .8);
			            	break;
			            	
			            	
			            default:
			            	break; //we are no longer using this toggle button
		 	        	
	 	        	}	
	 	        	toggleReadys[index] = false; //Don't notice it anymore until the button is lifted up
	    		}
	    	} else { //if not ready
	         	if (!isTriggered) { //button is no longer up (or just isn't up)
	         		toggleReadys[index] = true; //I'm ready for it to be pushed down again
	  	        }
    		}
        }
      
      SmartDashboard.putBoolean("Turn is triggered", triggers[2]);
        
        if (rotator != null)
        {
        	if (controller.getSimplePOV()==3){ //If the button that cancels the turn is pressed then cancel the turn.
        		rotator = null;
        	} else {
        		System.out.println("I'm going to turn!"); //If we don't tell the robot not to turn, it turns. This isn't rocket science.
        		boolean doneYet;
        		doneYet = rotator.accelerate();
        		if (doneYet) {
        			rotator = null;
        		}
        	}
        } else {
        	drive.tankDrive(speed * leftVal, speed * rightVal);
        }
    //Up=0, up-right = 1, right = 2. Goes to 7.
    int POV = controller.getSimplePOV();
    System.out.print(POV);
	}
	
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	public void disabledInit() {
		rotator = null;
	}
	//TODO set values to 0 to start match
	public void makeMotorsUseEncoders(CANTalon[] motors) {
		for (CANTalon motor : motors) {
			motor.setFeedbackDevice(FeedbackDevice.QuadEncoder); //Set the feedback device that is hooked up to the talon
			motor.setPID(0.5, 0.001, 0.0); //Set the PID constants (p, i, d)
			motor.enableControl(); //Enable PID control on the talon
			//Notes from Trent
			//p helps you get there if you're not getting there
			//d helps you limit oscillation
			//f is feedforward which gives you a push
			//i you don't really use much
		}
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
