package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Autonomous Mode Parking RED", group = "Concept")

public class AutoModeParkRED extends LinearOpMode {
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

            // move right to park
            roboController.FRW.setPower(-0.8);
            roboController.FLW.setPower(0.8);
            roboController.BRW.setPower(0.8);
            roboController.BLW.setPower(-0.8);

            // wait a little until the claw is flipped up
            sleep(4000);

            // stop
            roboController.FRW.setPower(0);
            roboController.FLW.setPower(0);
            roboController.BRW.setPower(0);
            roboController.BLW.setPower(0);
            sleep(500);

            // move the arm forward until it reaches a position that's about where the floor is (10)
            while(roboController.ArmR.getCurrentPosition() > 10) {
                roboController.ArmL.setPower(-0.45);
                roboController.ArmR.setPower(-0.45);
            }

            // once the arm is against the floor, stop moving it forward
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);
            // flip claw down
            roboController.WristL.setPosition(0.95);
            roboController.WristR.setPosition(0.05);
            sleep(500);
            // rotate pixel out
            roboController.ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
            roboController.ClawR.setPower(0.75);
            roboController.ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
            roboController.ClawL.setPower(0.75);
            // rotate for half a second
            sleep(1500);
            // stop rotating claw
            roboController.ClawR.setPower(0);
            roboController.ClawL.setPower(0);
            // flip claw back up
            roboController.WristL.setPosition(0.47);
            roboController.WristR.setPosition(0.53);
            sleep(500);

            // autonomous mode has now ended
            stop();
        }
    }
}