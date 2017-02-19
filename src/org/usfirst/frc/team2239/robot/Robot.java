package org.usfirst.frc.team2239.robot;

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
import edu.wpi.first.wpilibj.Solenoid;



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
    
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	AHRS navSensor; //The navigation sensor object
	boolean speedToggleReady = true;
	boolean gearToggleReady = true;
	double speed = 1;
	AccelerationHelper baseline;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		myCompressor = new Compressor(6);
		//myCompressor.setClosedLoopControl(true);
		gearRelease = new Solenoid(6, 0);
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
		drive = new TechnoDrive(2, 3, 1, 4);
		timer = new Timer();
		controller = new XboxController(0);  
		climber = new CANTalon(5);
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
		
		if (timer.get()>9) {
			gearRelease.set(true);
		}
		
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		SmartDashboard.putBoolean("Trigger", speedToggleReady);
		double leftVal = -controller.getY(XboxController.Hand.kLeft);
        double rightVal = -controller.getY(XboxController.Hand.kRight);
        boolean speedTriggered = controller.getTrigger(XboxController.Hand.kLeft) || controller.getTrigger(XboxController.Hand.kRight);
        boolean gearTriggered = controller.getRawButton(5) || controller.getRawButton(6);
        
        if (speedToggleReady){
	        if (speedTriggered) {
	        	if (speed==1) {
	        		speed = .5;
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
        
        if (speedToggleReady){
	        if (speedTriggered) {
	        	if (speed==1) {
	        		speed = .5;
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
        
        if (controller.getRawButton(2)) {
        	climber.set(1);
        }
        else {
        	climber.set(0);
        }
        
        //TODO test
        if (controller.getRawButton(1)){
        	gearRelease.set(true); //open it
        } else {
        	gearRelease.set(false); //close it
        }
        
        	
        
        SmartDashboard.putNumber("speed", speed);
        drive.tankDrive(speed * leftVal, speed * rightVal);
        //TODO test
        //if (myCompressor.getPressureSwitchValue()) {
        	myCompressor.start();
        //} else {
        	//myCompressor.stop();
        //}
        
	}
	
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
