package org.usfirst.frc.team2239.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
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
	public static Robot instance;
    //from http://wpilib.screenstepslive.com/s/4485/m/26401/l/255419-choosing-an-autonomous-program-from-smartdashboard

    public TechnoDrive drive;  // class that handles basic drive operations
    public Timer timer; // Timer
    public XboxController controller; //Control for the robot
    
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	AHRS navSensor; //The navigation sensor object
	
	boolean stoppedYet = false; //TODO delete

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		//2 = left rear
		//3 = left front
		//0 = right rear
		//1 = right front
		drive = new TechnoDrive(3, 2, 1, 0);
		navSensor = new AHRS(SPI.Port.kMXP); /* Alternatives: SerialPort.Port.kMXP, I2C.Port.kMXP or SerialPort.Port.kUSB */
		timer = new Timer();
		controller = new XboxController(0);  
		try {
			navSensor = new AHRS(SPI.Port.kMXP);
		} catch (RuntimeException ex) {
			DriverStation.reportError("Error instantiating navX-MXP: " + ex.getMessage(), true);
		}
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
		double dCurrentTime;
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		navSensor.reset();
		navSensor.resetDisplacement();
		
		System.out.println("Auto selected: " + autoSelected);
		float dispX = navSensor.getDisplacementX();
		float dispY = navSensor.getDisplacementY();
		float dispZ = navSensor.getDisplacementZ();
		float angle = navSensor.getYaw();
		
		System.out.println("Printing values");
		System.out.println(dispX);
		System.out.println(dispY);
		System.out.println(dispZ);
		System.out.println(angle);
		
		/*
		DriverStation.reportWarning(sDispX, false);
		DriverStation.reportWarning(sDispY, false);
		DriverStation.reportWarning(sDispZ, false);
		DriverStation.reportWarning(sAngle, false);
		*/
		timer.start();

		while( (dCurrentTime = timer.get()) < .55 ); // wait for 0.55 second before starting autonomousPeriodic

		stoppedYet = false;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
		
		
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
		double dCurrentTime = timer.get();
		SmartDashboard.putBoolean("IMU_Connected", 		navSensor.isConnected());
		SmartDashboard.putNumber("Displacement_X", 		navSensor.getDisplacementX());
		SmartDashboard.putNumber("Displacement_Y", 		navSensor.getDisplacementY());
		SmartDashboard.putNumber("Displacement_Z", 		navSensor.getDisplacementZ());
		
		
		if(dCurrentTime <= 1.5) {
			// drive.accelerateTo(0.25, 0.25);
			drive.drive(0.25, 0);
		} else {
			if (!stoppedYet) {
				while( (dCurrentTime = timer.get()) < 2.05 ); // wait for 0.55 second before starting autonomousPeriodic
				
				float dispX = navSensor.getDisplacementX();
				float dispY = navSensor.getDisplacementY();
				float dispZ = navSensor.getDisplacementZ();
				float angle = navSensor.getYaw();
				
				System.out.println("Printing values");
				System.out.println(dispX);
				System.out.println(dispY);
				System.out.println(dispZ);
				System.out.println(angle);
				
				timer.stop();
				
				/*
				DriverStation.reportWarning(sDispX, false);
				DriverStation.reportWarning(sDispY, false);
				DriverStation.reportWarning(sDispZ, false);
				DriverStation.reportWarning(sAngle, false);
				*/
			}
			stoppedYet = true;
			drive.stopMotor();
			timer.stop();
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		double leftVal = -controller.getY(XboxController.Hand.kLeft);
        double rightVal = -controller.getY(XboxController.Hand.kRight);
        drive.tankDrive(leftVal, rightVal);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
