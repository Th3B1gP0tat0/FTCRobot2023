package org.firstinspires.ftc.teamcode.modules;

import androidx.annotation.NonNull;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.Movement;

public class DriveTrain extends ModuleBase {
    /**
     * The motor that drives the front right mecanum wheel
     */
    protected final DcMotorEx frontRightMecanumDriver;

    /**
     * The default name of the front right mecanum driver
     */
    public static final String FRONT_RIGHT_MECANUM_DRIVER_DEFAULT_NAME = "Front Right Mecanum Driver";


    /**
     * The motor that drives the front left mecanum wheel
     */
    protected final DcMotorEx frontLeftMecanumDriver;


    /**
     * The default name of the front left mecanum driver
     */
    public static final String FRONT_LEFT_MECANUM_DRIVER_DEFAULT_NAME = "Front Left Mecanum Driver";


    /**
     * The motor that drives the back right mecanum wheel
     */
    protected final DcMotorEx backRightMecanumDriver;


    /**
     * The default name of the back right mecanum driver
     */
    public static final String BACK_RIGHT_MECANUM_DRIVER_DEFAULT_NAME = "Back Right Mecanum Driver";

    /**
     * The motor that drives the back left mecanum wheel
     */
    protected final DcMotorEx backLeftMecanumDriver;


    /**
     * The default name of the back left mecanum driver
     */
    public static final String BACK_LEFT_MECANUM_DRIVER_DEFAULT_NAME = "Back Left Mecanum Driver";

    /**
     * Attempts to initialize the module by getting motors with the default names from a hardware map
     * @param registrar the OpMode that will be using the module
     * @exception InterruptedException The module was unable to locate the necessary motors
     */
    public DriveTrain(@NonNull OpMode registrar) throws InterruptedException {
        super(registrar);
        try {
            frontRightMecanumDriver = registrar.hardwareMap.get(DcMotorEx.class, FRONT_RIGHT_MECANUM_DRIVER_DEFAULT_NAME);
            frontLeftMecanumDriver = registrar.hardwareMap.get(DcMotorEx.class, FRONT_LEFT_MECANUM_DRIVER_DEFAULT_NAME);
            backLeftMecanumDriver = registrar.hardwareMap.get(DcMotorEx.class, BACK_RIGHT_MECANUM_DRIVER_DEFAULT_NAME);
            backRightMecanumDriver = registrar.hardwareMap.get(DcMotorEx.class, BACK_LEFT_MECANUM_DRIVER_DEFAULT_NAME);
        }
        catch (IllegalArgumentException e) {
            throw new InterruptedException(e.getMessage());
        }

        // motor config
        frontRightMecanumDriver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRightMecanumDriver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontLeftMecanumDriver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeftMecanumDriver.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        frontRightMecanumDriver.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMecanumDriver.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftMecanumDriver.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMecanumDriver.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void cleanupModule() {

    }

    @Override
    public void log() {
        // nothing to log
    }

    /**
     * the scale for our exponential scaling of motor power
     */
    public static final int SCALE = 5;

    /**
     * Moves and rotates the robot
     * @param distX The right velocity
     * @param distY The forward velocity
     * @param rotation The rotational velocity
     */
    public void setVelocity(double distX, double distY, double rotation) {
        getTelemetry().addData("Moving by vector:", "<%f, %f, %f>", distX, distY, rotation);

        // Combine the requests for each axis-motion to determine each wheel's power.
        // (formula was found on gm0)
        double leftFrontPower  = distY + distX + rotation;
        double leftBackPower = distY - distX + rotation;
        double rightFrontPower   = distY - distX - rotation;
        double rightBackPower  = distY + distX - rotation;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }

        leftFrontPower = Math.pow(leftFrontPower, SCALE) * 0.75; // TODO make this a little less jank
        rightFrontPower = Math.pow(rightFrontPower, SCALE) * 0.75;
        rightBackPower = Math.pow(rightBackPower, SCALE) * 0.75;
        leftBackPower = Math.pow(leftBackPower, SCALE) * 0.75;

        getTelemetry().addData("Setting motor power", "%f, %f, %f, %f", leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);

        // Send calculated power to wheels
        frontLeftMecanumDriver.setPower(leftFrontPower);
        frontRightMecanumDriver.setPower(rightFrontPower);
        backRightMecanumDriver.setPower(rightBackPower);
        backLeftMecanumDriver.setPower(leftBackPower);
    }

    public void setVelocity(Movement velocity) {
        setVelocity(velocity.x, velocity.y, velocity.theta);
    }
}
