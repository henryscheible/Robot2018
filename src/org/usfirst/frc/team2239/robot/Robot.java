package org.usfirst.frc.team2239.robot;

/*
 * Jess controls:
 * Left on arrows should do 180 degree turn
 * Right on arrows should be cancel autonomous
 */

import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.CompressorJNI;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.Solenoid; //TODO uncomment
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
	
	public Robot() {
		table = NetworkTable.getTable("GRIP/myLinesReport");
	}
	
	
	public static Robot instance;
    //from http://wpilib.screenstepslive.com/s/4485/m/26401/l/255419-choosing-an-autonomous-program-from-smartdashboard

    public TechnoDrive drive;  // class that handles basic drive operations
    public Timer timer; // Timer
    public XboxController controller; //Control for the robot
    public CANTalon climber;
    //public Solenoid gearRelease; //TODO uncomment
    public Compressor myCompressor;
    public PowerDistributionPanel myPDP;
    
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	AHRS navSensor; //The navigation sensor object
	//TODO add a "how many triggers" int variable
	int toggleAmt = 3; //how many different buttons are toggling
	boolean[] toggleReadys = new boolean[toggleAmt]; //{speedToggleReady, gearToggleReady, turnToggle}
	boolean gearOpen = false;
	RotationAccelerationHelper rotator = null; 
	double speed = 1;
	AccelerationHelper baseline;
	
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
		
		
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		//myCompressor = new Compressor(6); //TODO uncomment once we have a compressor
		//myCompressor.setClosedLoopControl(true);
		//gearRelease = new Solenoid(6, 0); //TODO recomment
		//gearRelease.set(false);
		
		/*
		MOTORS:
			1- Back Left
			2- Back Right
			3- Front Right
			4- Front Left

			5- Climber
		*/
		//public TechnoDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
		//drive = new TechnoDrive(2, 3, 1, 4);//Big bot
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
		
		/*
		//network tables
		double[] defaultValue = new double[0];
		while (true) {
			double[] areas = table.getNumberArray("area", defaultValue); //code from FRC //TODO delete
			System.out.print("areas:  ");
			for (double area : areas) {
				System.out.print(area + " ");
				}
			System.out.println();
			Timer.delay(1); //All of this is from FRC and works with vision. That is all I know.
		}
		*/
			
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
		//myCompressor.start(); //TODO test
		//autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		navSensor.reset();
		//System.out.println("Auto selected: " + autoSelected);
		timer.start();
		//TechnoDrive theRobot, double startTime, double distance, double maxVelocity
		baseline = new AccelerationHelper(drive, timer.get(), 167.0, .7);
		myCompressor.start();
		
	}
	

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		myCompressor.start();
		/*
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

		SmartDashboard.putNumber("Angle", navSensor.getYaw());
		
	baseline.accelerate(timer.get());
		//TODO test
		
	/*
		if (timer.get()>9) {
			gearRelease.set(true);
		}
	*/ //TODO uncomment
		
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
		
		
		//SmartDashboard.putBoolean("Trigger", toggleReadys[0]); //TODO uncomment
		double leftVal = -controller.getY(XboxController.Hand.kLeft);
        double rightVal = -controller.getY(XboxController.Hand.kRight);
        
     
        boolean[] triggers = new boolean[toggleAmt];
        //Go through each toggle and see if the button is pushed down or not
        //Set all the values in triggers appropriately.
        for (int isTriggeredIndex = 0; isTriggeredIndex<toggleAmt; isTriggeredIndex++) {
        	switch (isTriggeredIndex) {
	            /*TODO uncomment
	             * case 0: triggers[isTriggeredIndex] = controller.getTrigger(XboxController.Hand.kLeft) || controller.getTrigger(XboxController.Hand.kRight);
	            		break;
	            
	            case 1: triggers[isTriggeredIndex] = controller.getRawButton(5) || controller.getRawButton(6);
	            		break;
	            		*/
	            case 2: triggers[isTriggeredIndex] = controller.getRawButton(3);
	            		break;
	               
	            default:
	            	break; //we are no longer using this toggle button
        	}
        }
      
      SmartDashboard.putBoolean("Turn is triggered", triggers[2]);
        
      //Go through each toggle and actually update/run
        for (int index = 0; index<toggleAmt; index++) {
        	boolean isReady = toggleReadys[index]; //is this button ready to be activated/pressed down
        	boolean isTriggered = triggers[index];
	    	if (isReady){
	    		if (isTriggered) { //button is down and this is the first time I've noticed
	    			//fire the trigger; the button has been pressed!
	 	        	switch (index) {
			            /*
			             * TODO uncomment
			             * case 0:
	         	        	if (speed==1) {
	         	        		speed = .7;
	         	        	} else {
	         	        		speed = 1;
	         	        	}
	         	        	break;
		         	    */
		         	    
		         	        /*//TODO uncomment once we have a solenoid hooked up
			            case 1:
			            	if (gearOpen) {
				        		gearRelease.set(false); //close it
				        		gearOpen = false;
				        	} else {
				        		gearRelease.set(true); //open it
				        		gearOpen = true;
				        	}
					        */
        
        
			            case 2:
			            	//TODO Luke and Ryan this should trigger a turn. See "Rotation Acceleration Helper.java"
			            	//TODO Luke and Ryan test different values to see what does a 90 degree turn= 1.571 radians
			            	//TODO set Rotator to be an actual Rotation Acceleration Helper
			        
			            	System.out.println("Starting a new rotation!");
			            	double turnAngle = 10;
			            	//public RotationAccelerationHelper (TechnoDrive driveTrain, AHRS navSensor, double turnAngle, double maxVelocity) 
			            	rotator = new RotationAccelerationHelper(drive, navSensor, turnAngle, .8);
			            	
			            	
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
        
       
        
        /*//TODO delete this is the old version
        if (speedToggleReady){
	        if (speedTriggered) {
	        	if (speed==1) {
	        		speed = .7;
	        	} else {
	        		speed = 1;
	        	}
	        	speedToggleReady = false;
	        }
        } else {
        	if (!speedTriggered) {
 	        	speedToggleReady = true;
 	        }
        }
        */
        
        
       /* //TODO uncomment
        if (controller.getRawButton(2)) {
        	climber.set(1);
        }
        else {
        	climber.set(0);
        }
        */
        /*TODO uncomment once we have solenoid hooked up
        if (controller.getRawButton(1)){
        	gearRelease.set(true); //open it
        } else {
        	gearRelease.set(false); //close it
        }
        */
        
        if (rotator != null)
        {
        	System.out.println("I'm going to turn!");
        	boolean doneYet;
        	doneYet = rotator.accelerate();
        	if (doneYet) {
        		rotator = null;
        	}
        } else {
        	drive.tankDrive(speed * leftVal, speed * rightVal);
        }

        //SmartDashboard.putNumber("speed", speed); //TODO uncomment
        
        //myCompressor.start(); //TODO uncomment
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
}
