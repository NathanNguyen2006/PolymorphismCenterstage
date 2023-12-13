package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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
            sleep(1000);

            // move right to the middle of the adjacent panel
            roboController.moveOnXAxis(RoboController.inchesToCounts(27));

            // move forward to the middle of the adjacent panel
            roboController.moveOnYAxis(RoboController.inchesToCounts(27));

            // turn left by about 90 degrees
            roboController.Spin(RoboController.inchesToCounts(-18));

            // move back right against the middle of the backboard
            roboController.moveOnYAxis(RoboController.inchesToCounts(-19));

            // move the arm back until it reaches a position thats right against the backboard (2050)
            while(roboController.ArmR.getCurrentPosition() < 2050) {
                roboController.ArmL.setPower(0.45);
                roboController.ArmR.setPower(0.45);
            }

            // once the arm is against the backboard, stop moving it back
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);

            // open the claw to release the pixels
            roboController.ClawR.setPosition(1);
            roboController.ClawL.setPosition(0);

            // wait a second in case the pixels haven't been completely scored yet
            sleep(1000);

            // move the arm forward until it reaches a position thats about where the floor is (10)
            while(roboController.ArmR.getCurrentPosition() > 10) {
                roboController.ArmL.setPower(-0.45);
                roboController.ArmR.setPower(-0.45);
            }

            // once the arm is against the floor, stop moving it forward
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);

            // flip the claw down so that it can be closed without obstruction
            roboController.Wrist.setPosition(0);

            // wait a second to give the bot time to flip the claw
            sleep(1000);

            // close the claw
            roboController.ClawR.setPosition(0);
            roboController.ClawL.setPosition(1);

            // wait a second to give the bot time to close the claw
            sleep(1000);

            // flip the claw back up so that the bot can move without obstruction
            roboController.Wrist.setPosition(0.55);

            // wait a second to give the bot time to flip the claw
            sleep(1000);

            // move forward so that the bot isn't right against the backboard
            roboController.moveOnYAxis(RoboController.inchesToCounts(3));

            // move left to the middle of the adjacent panel
            roboController.moveOnXAxis(RoboController.inchesToCounts(-27));

            // move back to the space next to the backboard
            roboController.moveOnYAxis(RoboController.inchesToCounts(-13));
            stop();
        }
    }
}