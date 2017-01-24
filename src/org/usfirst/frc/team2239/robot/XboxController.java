package org.usfirst.frc.team2239.robot;

import edu.wpi.first.wpilibj.GenericHID;

public class XboxController extends GenericHID {

	public XboxController(int port) {
		super(port);
	}

	@Override
	public double getX(Hand hand) {
		return hand == Hand.kLeft ? getRawAxis(0) : getRawAxis(4);
	}

	@Override
	public double getY(Hand hand) {
		return hand == Hand.kLeft ? getRawAxis(1) : getRawAxis(5);
		
	}

	@Override
	public double getRawAxis(int which) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getRawButton(int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPOV(int pov) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPOVCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HIDType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOutput(int outputNumber, boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOutputs(int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRumble(RumbleType type, double value) {
		// TODO Auto-generated method stub
		
	}

}
