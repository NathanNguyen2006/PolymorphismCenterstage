package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class SuperSimpleArmOp extends LinearOpMode {
    private RoboController roboController;

    private Gamepad movePad;
    private Gamepad armPad;

    @Override
    public void runOpMode() {
        //imu = hardwareMap.get(Gyroscope.class, "imu");
        //digitalTouch = hardwareMap.get(DigitalChannel.class, "digitalTouch");
        //sensorColorRange = hardwareMap.get(DistanceSensor.class, "sensorColorRange");

        //Robo Controller
        roboController = new RoboController(this);

        //Choose which gamepad is which
        movePad = gamepad1;
        armPad = gamepad2;


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()){
            //telemetry.addData("Status", "Playing");
            //Transferring movement inputs
            //roboController.ArmBase.setPower(armPad.left_stick_y);
            //roboController.ArmBase2.setPower(armPad.right_stick_y);
            //roboController.ArmTop.setPower(armPad.right_trigger - armPad.left_trigger);
            //roboController.interpretArmpad(armPad);
            // roboController.testWheels(movePad);
            telemetry.addData("movepad.right_stick_y:", gamepad2.right_stick_y);
            telemetry.addData("movepad.left_stick_y", gamepad2.left_stick_y);
            telemetry.addData("Status", "Running");
            telemetry.addData("Direction", roboController.direction);
            telemetry.addData("Drive", roboController.drivePower);
            telemetry.addData("Strafe", roboController.strafePower);
            telemetry.addData("Turn", roboController.turnPower);
            //telemetry.addData("ArmBase1", gamepad2.ArmBase.getCurrentPosition());
            //telemetry.addData("ArmBase2", gamepad2.ArmBase2.getCurrentPosition());
            //telemetry.addData("ArmTop", gamepad2.ArmTop.getCurrentPosition());
            //telemetry.addData("Drive", direction);

            telemetry.addData("Armbase2", roboController.getThePosition());

            telemetry.update();
        }
    }
}
