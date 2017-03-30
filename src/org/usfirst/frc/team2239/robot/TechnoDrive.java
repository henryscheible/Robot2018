package org.usfirst.frc.team2239.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * Utility for driving the robot
 * Only put functions that operate instantaneously in this class
 *
 * Author: Dean Bassett and London Lowmanstone
 */
public class TechnoDrive extends RobotDrive {
    public static final double MAIN_SPEED = 1;

    public TechnoDrive(int leftMotorChannel, int rightMotorChannel) {
        super(new CANTalon(leftMotorChannel), new CANTalon(rightMotorChannel));
    }

    public TechnoDrive(int frontLeftMotor, int rearLeftMotor, int frontRightMotor, int rearRightMotor) {
        super(new CANTalon(frontLeftMotor), new CANTalon(rearLeftMotor),
                new CANTalon(frontRightMotor), new CANTalon(rearRightMotor)); //calls the RobotDrive constructor
    }

    public double[] getMotorValues() {
        return new double[] {super.m_rearLeftMotor.get(), -super.m_rearRightMotor.get()};
    }

    /* Writing our own encoder/tick/wheel-rotation based function //TODO delete
    public void accelerateTo(double left, double right) {
        double[] speed = getMotorValues();
        tankDrive(accelerateHelper(speed[0], left), accelerateHelper(speed[1], right));
    }

    private double accelerateHelper(double from, double to) {
        if(from < to) {
            return Math.max(to, from - .005);
        } else {
            return Math.min(to, from + .005);
        }
    }
    */

    /*
     * Goal: Write a program to ramp up at acceleration until halfway there, and then ramp down afterwards
     * 
     */
    public double autoRamp(double acceleration, double rotationAmt, double mySpeed) {
    	int curEncoderCount = 0; //dummy variable //how many times the wheels have rotated.
    	//tankDrive(mySpeed, mySpeed); 
    	if ((mySpeed < 1) && (curEncoderCount < rotationAmt/2))
    	{
    		mySpeed += acceleration;
    	}
    	
    	if  (curEncoderCount >= rotationAmt/2)
    	{
    		mySpeed -= acceleration;
    	}
    	
    	tankDrive(mySpeed, mySpeed);
    	return mySpeed;
    }
    
    @Override
    public void tankDrive(GenericHID leftStick, GenericHID rightStick) {
        if (leftStick == null || rightStick == null) {
            throw new NullPointerException("Null HID provided");
        }

        double left = -leftStick.getY();
        double right = -rightStick.getY();
        this.tankDrive(left, right);

    }

    public void tankDrive(XboxController controller) {
    	System.out.println("Running tankDrive"); //TODO delete
    	System.out.println("left: "+controller.getY(XboxController.Hand.kLeft)+" right: "+controller.getY(XboxController.Hand.kRight));
    	this.tankDrive(-controller.getY(XboxController.Hand.kLeft), -controller.getY(XboxController.Hand.kRight));
    }
    
        //rightStick is straight, controller is full speed
        //if the controller is triggered then it will run at full speed

        //straight

    
    @Override
    public void tankDrive(double left, double right) {
        tankDrive(left, right, true);
    }

    @Override
    public void tankDrive(double left, double right, boolean squared) {
        super.tankDrive(left, right, squared);
    }
}
