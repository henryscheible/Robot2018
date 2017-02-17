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
	
	public Robot() {
		table = NetworkTable.getTable("GRIP/myContoursReport");			
	}
	
	public void robotInit() {
		double[] defaultValue = new double[0];
		SmartDashboard.putNumber("Hello", 100);
		/*
		double[] areas = table.getNumberArray("area", defaultValue);
		System.out.print("areas: ");
		for (double area : areas) {
			SmartDashboard.putNumber("Area", area);
		}
		System.out.println();
		Timer.delay(1);
		*/
	}
	
	public void autonomousInit() {
		
	}
	
	public void autonomousPeriodic() {
		
	}
	
	public void teleopInit() {
		
	}
	
	public void teleopPeriodic() {
		
	}
}