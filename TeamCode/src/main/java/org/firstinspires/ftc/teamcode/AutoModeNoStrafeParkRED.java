package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Autonomous Mode (No Strafing) Parking RED", group = "Concept")

public class AutoModeNoStrafeParkRED extends LinearOpMode {
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
            roboController.WristL.setPosition(0.47);
            roboController.WristR.setPosition(0.53);

            // wait a little until the claw is flipped up
            sleep(1500);

            // spin 90 degrees
            roboController.Spin(RoboController.inchesToCounts(-18));;

            // wait a little
            sleep(1500);

            // move back to park
            roboController.moveOnYAxis(RoboController.inchesToCounts(-27));

            // autonomous mode has now ended
            stop();
        }
    }
}
