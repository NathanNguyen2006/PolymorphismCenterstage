package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class OperatorOpMode extends LinearOpMode{
    //Personal Class
    private RoboController roboController;

    //private Gyroscope imu;
    //private DcMotor motorTest;
    //private DigitalChannel digitalTouch;
    // private DistanceSensor sensorColorRange;

    //private Servo servoTest;

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
            roboController.interpretMovepad(movePad);
            roboController.interpretArmpad(armPad);
            //roboController.interpretArmpad(armPad);
            // roboController.testWheels(movePad)
            telemetry.addData("Status", "Running");
            telemetry.addData("Direction", roboController.direction);
            telemetry.addData("Drive", roboController.drivePower);
            telemetry.addData("Strafe", roboController.strafePower);
            telemetry.addData("Turn", roboController.turnPower);
            //telemetry.addData("Armbasepos", roboController.getThePosition());
            telemetry.addData("Wrist", roboController.Wrist.getPosition());
            telemetry.addData("ClawR (dir)", roboController.ClawR.getDirection());
            telemetry.addData("ClawL (dir)", roboController.ClawL.getDirection());
            telemetry.addData("ClawR (power)", roboController.ClawR.getPower());
            telemetry.addData("ClawL (power)", roboController.ClawL.getPower());
            telemetry.addData("ArmL", roboController.ArmL.getCurrentPosition());
            telemetry.addData("ArmR", roboController.ArmR.getCurrentPosition());
            telemetry.addData("ArmRpow", roboController.ArmR.getPower());
            telemetry.addData("Drone: Plane Launcher", roboController.Drone.getPosition());
            telemetry.addData("y-stick value", armPad.left_stick_y);
            telemetry.update();
        }
    }
}