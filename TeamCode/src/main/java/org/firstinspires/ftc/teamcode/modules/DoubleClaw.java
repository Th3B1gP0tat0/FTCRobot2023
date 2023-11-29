package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class DoubleClaw extends ModuleBase {
    public enum ClawState {
        BOTH_GRABBED,
        ONE_GRABBED,
        BOTH_RELEASED
    }

    private final Claw inner;

    private final Claw outer;

    private ClawState clawState;

    public static final String OUTER_CLAW_SERVO_NAME = "Outer " + Claw.CLAW_SERVO_DEFAULT_NAME;

    public static final String INNER_CLAW_SERVO_NAME = "Inner " + Claw.CLAW_SERVO_DEFAULT_NAME;

    /**
     * Initializes the module and registers it with the specified OpMode
     *
     * @param registrar The OpMode initializing the module
     */
    public DoubleClaw(OpMode registrar) {
        super(registrar);
        outer = new Claw(registrar, OUTER_CLAW_SERVO_NAME, 1-Claw.DEFAULT_GRABBING_SERVO_POSITION, 1-Claw.DEFAULT_RELEASED_SERVO_POSITION);
        inner = new Claw(registrar, INNER_CLAW_SERVO_NAME, +Claw.DEFAULT_GRABBING_SERVO_POSITION, +Claw.DEFAULT_RELEASED_SERVO_POSITION);

        inner.grab();
        outer.grab();
        clawState = ClawState.BOTH_GRABBED;
    }

    public ClawState getClawState() {
        return clawState;
    }

    @Override
    public void log() {
        getTelemetry().addData("Claw state", getClawState());
        inner.log();
        outer.log();
    }

    public ClawState incrementClawState() {
        switch (clawState) {
            case BOTH_GRABBED:
                outer.release();
                clawState = ClawState.ONE_GRABBED;
                break;
            case ONE_GRABBED:
                inner.release();
                clawState = ClawState.BOTH_RELEASED;
                break;
            case BOTH_RELEASED:
                inner.grab();
                outer.grab();
                clawState = ClawState.BOTH_GRABBED;
                break;
        }
        return getClawState();
    }

    @Override
    public void cleanupModule() {

    }
}
