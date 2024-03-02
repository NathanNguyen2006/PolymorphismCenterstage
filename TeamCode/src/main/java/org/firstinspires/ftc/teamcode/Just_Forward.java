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
            roboController.FLW.setDirection(DcMotor.Direction.FORWARD);
            roboController.FRW.setDirection(DcMotor.Direction.FORWARD);
            roboController.BLW.setDirection(DcMotor.Direction.REVERSE);
            roboController.BRW.setDirection(DcMotor.Direction.FORWARD);

            roboController.moveOnYAxis(RoboController.inchesToCounts(30));

            sleep(4000);

            roboController.moveOnXAxis(RoboController.inchesToCounts(30));

            sleep(4000);

            roboController.Spin(RoboController.inchesToCounts(18));

            // autonomous mode has now ended
            stop();
        }
    }
}
