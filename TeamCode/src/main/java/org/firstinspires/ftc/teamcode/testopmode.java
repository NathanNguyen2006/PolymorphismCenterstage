package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class testopmode extends LinearOpMode{
    //Personal Class
    private RoboController roboController;

    //private Gyroscope imu;
    //private DcMotor motorTest;
    //private DigitalChannel digitalTouch;
    // private DistanceSensor sensorColorRange;

    //private Servo servoTest;
    public DcMotor FRW;
    public DcMotor FLW;
    public DcMotor BRW;
    public DcMotor BLW;
    private Gamepad movePad;
    private Gamepad armPad;

    @Override
    public void runOpMode() {
        //imu = hardwareMap.get(Gyroscope.class, "imu");
        //digitalTouch = hardwareMap.get(DigitalChannel.class, "digitalTouch");
        //sensorColorRange = hardwareMap.get(DistanceSensor.class, "sensorColorRange");

        //Robo Controller

        FRW = hardwareMap.get(DcMotorEx.class, "FRW");
        FLW = hardwareMap.get(DcMotorEx.class, "FLW");
        BRW = hardwareMap.get(DcMotorEx.class, "BRW");
        BLW = hardwareMap.get(DcMotorEx.class, "BLW");
        FLW.setDirection(DcMotor.Direction.REVERSE);
        FRW.setDirection(DcMotor.Direction.FORWARD);
        BLW.setDirection(DcMotor.Direction.REVERSE);
        BRW.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()){


            if (gamepad1.triangle){
                FRW.setPower(1); //-strafe
                FLW.setPower(1);
                BRW.setPower(1);
                BLW.setPower(1); //-strafe
            }
            else if (gamepad1.cross){
                FRW.setPower(-1); //-strafe
                FLW.setPower(-1);
                BRW.setPower(-1);
                BLW.setPower(-1); //-strafe
            }
            else if (gamepad1.circle){
                FRW.setPower(1); //-strafe
                FLW.setPower(-1);
                BRW.setPower(-1);
                BLW.setPower(1); //-strafe
            }
            else if (gamepad1.square){
                FRW.setPower(-1); //-strafe
                FLW.setPower(1);
                BRW.setPower(1);
                BLW.setPower(-1); //-strafe
            }
            else {
                FRW.setPower(0); //-strafe
                FLW.setPower(0);
                BRW.setPower(0);
                BLW.setPower(0); //-strafe
            }
        }
    }
}