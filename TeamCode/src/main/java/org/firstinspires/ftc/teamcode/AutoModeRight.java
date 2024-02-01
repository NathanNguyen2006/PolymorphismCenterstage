package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This 2022-2023 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine which image is being presented to the robot.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "Autonomous Mode RIGHT (for red)", group = "Concept")
//@Disabled
public class AutoModeRight extends LinearOpMode {
    private RoboController roboController;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // raise the claw so that it stays up completely
            roboController.Wrist.setPosition(0.53);

            // wait a little until the claw is flipped up
            sleep(1500);

            // just moves to the right (for the red side)
            roboController.moveOnXAxis(RoboController.inchesToCounts(52));

            // autonomous mode has now ended
            stop();
        }
    }
}