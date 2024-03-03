package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "TEST: Just_Forward", group = "Concept")

public class Just_Forward extends LinearOpMode {
    private RoboController roboController;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {

            roboController.moveOnYAxis(RoboController.inchesToCounts(30));



            //roboController.moveOnXAxis(RoboController.inchesToCounts(30));



           // roboController.Spin(RoboController.inchesToCounts(18));

            // autonomous mode has now ended
            stop();
        }
    }
}
