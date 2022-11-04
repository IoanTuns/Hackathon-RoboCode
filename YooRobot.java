package yooRobot;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.geom.Point2D;
import robocode.util.Utils;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * YooRobot - a robot by (your name here) 
 */
public class YooRobot extends AdvancedRobot 
{
    private int wallLimit = 45;
    private byte moveDirection = 1;
	private byte moveDirectionH = 1;
    private int tooCloseToWall = 0;
    double firePower = 2;
    /**
     * run: YooRobot's default behavior
     */
    public void run() {
        // Initialization of the robot should be put here
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		Condition onWallsLimit = new Condition("WallsLimit"){
            public boolean test() {
                return (
                    // we're too close to the left wall
                    getX() <= wallLimit ||
                        // or we're too close to the right wall
                        getX() >= getBattleFieldWidth() - wallLimit ||
                        // or we're too close to the bottom wall
                        getY() <= wallLimit ||
                        // or we're too close to the top wall
                        getY() >= getBattleFieldHeight() - wallLimit
                );
			}
		};
        addCustomEvent(onWallsLimit);
        // After trying out your robot, try uncommenting the import at the top,
        // and the next line:
        setColors(Color.pink, Color.blue, Color.green); // body,gun,radar
        // Robot main loop
        while (true) {
            goRadar();
            goMove();
            execute();
        }
    }

    /**
     * firePower: Calculate the fire power to use
     */
    double goFirePower(double distance, double energy) {
        if (distance < 100 && energy < 10) {
			firePower = firePower - 1;
            setFire(firePower);
        } else if (distance < 100 && energy < 10) {
            setFire(firePower);
        } else if (distance < 400 && energy < 30) {
			firePower = firePower + 1; 
            setFire(firePower);
        } else {
			firePower = firePower + 2;
            setFire( firePower );
        }
		return firePower;
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
		double absoluteBearingRadius = getHeadingRadians() + e.getBearingRadians();
		double absoluteBearing = getHeading() + e.getBearing();
		setTurnRadarRightRadians(Utils.normalRelativeAngleDegrees(absoluteBearingRadius - getRadarHeading()));
		setTurnGunRightRadians(Utils.normalRelativeAngle(absoluteBearingRadius - getGunHeadingRadians()));
		setMaxVelocity(8);
		//double moveWith = 2500 * moveDirection;
		setTurnRight( e.getBearing() + 90 - (25 * moveDirection));
        if (e.getVelocity() != 0) {
			setTurnLeft(e.getHeading());
            if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5) {
                goFirePower(e.getDistance(), e.getEnergy());
            }
        }
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 5) {
            goFirePower(e.getDistance(), e.getEnergy());
        }
    }

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // Replace the next line with any behavior you would like
        goMove();
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent  e) {
        // Replace the next line with any behavior you would like
        goMove();
    }

    public void onHitRobot(HitRobotEvent e) {
        tooCloseToWall = 0;
    }

    public void onCustomEvent(CustomEvent e) {
        if (e.getCondition().getName().equals("WallsLimit")) {
			moveDirection *= -1;
			setTurnRight(90 * moveDirection / getHeading() );
			}
    }
    void goRadar() {
        // rotate the radar
        setTurnRadarRight(360);
    }

    public void goMove() {
        

        // if we're close to the wall, eventually, we'll move away
        if (tooCloseToWall > 0) tooCloseToWall--;

        // switch directions if we've stopped
        // (also handles moving away from the wall if too close)
        if (getVelocity() == 0) {
            setMaxVelocity(8);
            moveDirection *= -1;
            setAhead(50000 * moveDirection);
    	}
	}
    //	void goGun() {
    //	onScannedRobot();

    //	}
}