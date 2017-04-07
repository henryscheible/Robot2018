package org.usfirst.frc.team2239.robot;
import edu.wpi.first.wpilibj.Timer;


public class PauseAction implements Action {

	public Timer timer;
	double timerTarget;
	public boolean timerOn = false;
	
	//how many seconds to pause for
	public PauseAction(double time) {
		timerTarget = time;
	}
	
	@Override
	public boolean run() {
		if (timerOn) {
			if (timer.get()<timerTarget) {
				return false;
			} else {
				return true;
			}
		} else {
			timer.start();
			timerOn = true;
			return false;
		}
	}

}
