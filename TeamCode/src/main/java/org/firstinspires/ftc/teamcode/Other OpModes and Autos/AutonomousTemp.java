package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class AutonomousTemp extends LinearOpMode {
    private RoboController roboController;
    private int a = 0;
    @Override
    public void runOpMode() {
        roboController = new RoboController(this);
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();
        while (opModeIsActive()) {
            a++;
            roboController.moveLeftABit(a);
            telemetry.addData("a", a);
            telemetry.update();
        }
    }
}

