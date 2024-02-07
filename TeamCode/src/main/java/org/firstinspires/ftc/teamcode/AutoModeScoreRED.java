package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Autonomous Mode Scoring RED", group = "Concept")

public class AutoModeScoreRED extends LinearOpMode {
    private RoboController roboController;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // autonomous scoring for the red side (towards the backboard)

            // raise the claw so that it stays up completely
            roboController.Wrist.setPosition(0.53);

            // wait a little until the claw is flipped up
            sleep(1500);

            // move right to the middle of the adjacent panel
            roboController.moveOnXAxis(RoboController.inchesToCounts(27));

            // move forward to the middle of the adjacent panel
            roboController.moveOnYAxis(RoboController.inchesToCounts(27));

            // turn left by about 90 degrees
            roboController.Spin(RoboController.inchesToCounts(-18));

            // move back right against the middle of the backboard
            roboController.moveOnYAxis(RoboController.inchesToCounts(-19));

            // move the arm back until it reaches a position that's right against the backboard (2050)
            while(roboController.ArmR.getCurrentPosition() < 2050) {
                roboController.ArmL.setPower(0.45);
                roboController.ArmR.setPower(0.45);
            }

            // once the arm is against the backboard, stop moving it back
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);

            // push pixels out
            roboController.ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
            roboController.ClawR.setPower(0.5);
            roboController.ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
            roboController.ClawL.setPower(0.5);

            // wait one and a half seconds in case the pixels haven't been completely scored yet
            sleep(1500);

            // stop rotating claw
            roboController.ClawR.setPower(0);
            roboController.ClawL.setPower(0);

            // move the arm forward until it reaches a position that's about where the floor is (10)
            while(roboController.ArmR.getCurrentPosition() > 10) {
                roboController.ArmL.setPower(-0.45);
                roboController.ArmR.setPower(-0.45);
            }

            // once the arm is against the floor, stop moving it forward
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);

            // move forward so that the bot isn't right against the backboard
            roboController.moveOnYAxis(RoboController.inchesToCounts(3));

            // move left to the middle of the adjacent panel
            roboController.moveOnXAxis(RoboController.inchesToCounts(-27));

            // move back to the middle of the adjacent panel (to park in the backstage area)
            roboController.moveOnYAxis(RoboController.inchesToCounts(-13));

            // autonomous mode has now ended
            stop();
        }
    }
}