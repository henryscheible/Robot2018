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
	
	public Robot() {
		table = NetworkTable.getTable("GRIP/myLinesReport");
	}
	
	
	public static Robot instance;
    //from http://wpilib.screenstepslive.com/s/4485/m/26401/l/255419-choosing-an-autonomous-program-from-smartdashboard

    public TechnoDrive drive;  // class that handles basic drive operations
    public Timer timer; // Timer
    public XboxController controller; //Control for the robot
    public CANTalon climber;
    public Solenoid gearRelease;
    public Compressor myCompressor;
    public PowerDistributionPanel myPDP;
    
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	AHRS navSensor; //The navigation sensor object
	int toggleAmt = 3; //how many different buttons are toggling
	boolean[] toggleReadys = new boolean[toggleAmt]; //{speedToggleReady, gearToggleReady, turnToggle}
	boolean gearOpen = false;
	RotationAccelerationHelper rotator = null;
	double speed = 1;
	AccelerationHelper baseline;
	//drive = new TechnoDrive(4,1,3,2);//small bot
	CANTalon frontLeftMotor = new CANTalon(4);
	CANTalon rearLeftMotor = new CANTalon(1);
	CANTalon frontRightMotor = new CANTalon(3);
	CANTalon rearRightMotor = new CANTalon(2);
	
	boolean testEncoders = true;//TODO delete
	
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
		myCompressor = new Compressor(6); 
		myCompressor.setClosedLoopControl(true);
		//gearRelease = new Solenoid(CAN ID on dashboard, channel on PCM (what's it plugged into));
		//gearRelease = new Solenoid(6, 0); //TODO SpiderBot
		gearRelease = new Solenoid(7, 0);
		gearRelease.set(false); //
		
		
		/*
		MOTORS:
			1- Back Left
			2- Back Right
			3- Front Right
			4- Front Left

			5- Climber
		*/
		//public TechnoDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor)
		//drive = new TechnoDrive(2, 3, 1, 4);//Big SpiderBot //TODO switch back
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
		System.out.println("How far the rearRightMotor has gone: "+rearRightMotor.get());
		testEncoders = true;
	}
	

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		if (testEncoders) {
			rearRightMotor.set(500);
			rearLeftMotor.set(-500);
			testEncoders=!testEncoders;
			System.out.println("Ran testEncoders");
		}
		System.out.println("How far the rearRightMotor has gone: "+rearRightMotor.get());
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
		
	/*//TODO uncomment for baseline crossing
	baseline.accelerate(timer.get());
		//TODO test
		
		if (timer.get()>9) {
			gearRelease.set(true);
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
		
		System.out.println("How far the rearRightMotor has gone: "+rearRightMotor.getEncVelocity());
		System.out.println("How far the rearRightMotor has gone: "+rearRightMotor.getEncPosition());
		
		System.out.println("How far the leftRightMotor has gone: "+rearLeftMotor.getEncVelocity());
		System.out.println("How far the leftRightMotor has gone: "+rearLeftMotor.getEncPosition());
		
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
	            case 2: triggers[index] = controller.getSimplePOV() == 6; //TODO change this to a POV for Jess
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
	         	        		speed = .6;
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
			            	rotator = new RotationAccelerationHelper(drive, navSensor, turnAngle, .8);
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
        	System.out.println("I'm going to turn!");
        	boolean doneYet;
        	doneYet = rotator.accelerate();
        	if (doneYet) {
        		rotator = null;
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
}
