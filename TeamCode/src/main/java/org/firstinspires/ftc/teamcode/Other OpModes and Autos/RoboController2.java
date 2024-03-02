package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class RoboController2 {

    public static boolean almostEqual(double a, double b, double eps){
        return Math.abs(a-b)<eps;
    }
    private ElapsedTime runtime = new ElapsedTime();
    private static final int speed = 1000;

    //Hardware

    //Wheels
    public DcMotorEx FRW;
    public DcMotorEx FLW;
    public DcMotorEx BRW;
    public DcMotorEx BLW;

    //Arm
    public DcMotor ArmBase2;
    public DcMotor ArmBase;
    public DcMotor ArmTop;
    private Servo Hand;
    public Servo ClawR;
    private Servo Twister;


    private boolean handClosed = false;
    private boolean previousRightBumper = false;
    private boolean previousLeftBumper = false;
    private boolean twisterTurned = false;

    public double strafePower = 0;
    public double drivePower = 0;
    public double turnPower = 0;

    //directions

    public Compass direction = Compass.North;

    private LinearOpMode opMode;

    public RoboController2(LinearOpMode opMode){
        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;
        //Wheels
        FRW = hardwareMap.get(DcMotorEx.class, "FRW");
        FLW = hardwareMap.get(DcMotorEx.class, "FLW");
        BRW = hardwareMap.get(DcMotorEx.class, "BRW");
        BLW = hardwareMap.get(DcMotorEx.class, "BLW");


        // Set direction of motors
        FLW.setDirection(DcMotor.Direction.REVERSE);
        FRW.setDirection(DcMotor.Direction.FORWARD);
        BLW.setDirection(DcMotor.Direction.REVERSE);
        BRW.setDirection(DcMotor.Direction.FORWARD);

        //Arms
        ArmTop = hardwareMap.get(DcMotor.class,"ArmTop");
        ArmBase = hardwareMap.get(DcMotor.class,"ArmBase");
        ArmBase2 = hardwareMap.get(DcMotor.class,"ArmBase2");
        ClawR = hardwareMap.get(Servo.class,"ClawR");
        Hand = hardwareMap.get(Servo.class,"Hand");
        Twister = hardwareMap.get(Servo.class,"Twister");

        ArmBase2.setDirection(DcMotor.Direction.REVERSE);

        //Reset Encoders
        ArmBase.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmTop.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmBase2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmBase.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ArmTop.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ArmBase2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Pid stuff unused lol
        //ArmTopPid = new Pid( 0.3, 0.3, 0.3, 1, -1, 1);
        //ArmBasePid = new Pid(0.1, 0.1, 0.15, 1, -1, 1);


    }




    //arm stuff
    public double armBasePower;
    public double armTopPower;

    public void interpretMovepad(Gamepad movepad){

        FLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if(Math.abs(movepad.right_stick_x) > .2){
            turnPower = movepad.right_stick_x*0.35;
        }
        else{
            turnPower = 0;
        }

        if(movepad.left_stick_x > 0.3){
            direction = Compass.East;
        }
        else if(movepad.left_stick_x < -0.3){
            direction = Compass.West;
        }
        else if(movepad.left_stick_y < -0.3){
            direction = Compass.North;
        }
        else if(movepad.left_stick_y > 0.3){
            direction = Compass.South;
        }



        //Direction
        if (movepad.dpad_up) drivePower = 0.5;
        else if (movepad.dpad_down) drivePower = -0.5;
        else{ drivePower = 0;}

        if (movepad.dpad_right) strafePower = 0.9;
        else if (movepad.dpad_left) strafePower = -0.9;
        else {
            strafePower = 0;
        }

        double tempStrafe = strafePower;
        switch (direction) {
            case North: break;
            case South:
                drivePower = -drivePower;
                strafePower = -strafePower;
                break;
            case East:
                tempStrafe = strafePower;
                strafePower = -drivePower;
                drivePower = tempStrafe;
                break;
            case West:
                tempStrafe = strafePower;
                strafePower = drivePower;
                drivePower = -tempStrafe;
                break;
        }

        //Turn overwrites strafe
        if(drivePower != 0){
            if(turnPower != 0){
                //Drive and Turn
                FRW.setPower(drivePower);
                FLW.setPower(turnPower);
                BRW.setPower(drivePower);
                BLW.setPower(turnPower);

            }else if(strafePower  != 0){
                //Drive and Strafe
                FRW.setPower(-strafePower);
                FLW.setPower(drivePower);
                BRW.setPower(drivePower);
                BLW.setPower(-strafePower);

            }else{
                //Just Drive
                FRW.setPower(drivePower);
                FLW.setPower(drivePower);
                BRW.setPower(drivePower);
                BLW.setPower(drivePower);
            }
        }else if(turnPower != 0){
            //Just Turn
            FRW.setPower(-turnPower);
            FLW.setPower(turnPower);
            BRW.setPower(-turnPower);
            BLW.setPower(turnPower);

        }else if(strafePower != 0){
            //Just Strafe
            FRW.setPower(-strafePower); //-strafe
            FLW.setPower(strafePower);
            BRW.setPower(strafePower);
            BLW.setPower(-strafePower); //-strafe
        }
        else{
            FRW.setPower(-0);
            FLW.setPower(0);
            BRW.setPower(0);
            BLW.setPower(-0);
        }
    }

    //Not Implemented
    public void interpretArmpad(Gamepad armpad){

        double baseConstFactor = -.99;
        if (Math.abs(ArmBase2.getCurrentPosition())> 5300  || Math.abs(ArmBase.getCurrentPosition())> 5300) {
            ArmBase.setPower(-0.3);
            ArmBase2.setPower(-0.3);
        }
        else if (ArmBase2.getCurrentPosition()< -250  || ArmBase.getCurrentPosition()< -250){
            ArmBase.setPower(0.3);
            ArmBase2.setPower(0.3);
        }
        else if(Math.abs(armpad.left_stick_y) > 0.3){
            ArmBase2.setPower(armpad.left_stick_y*baseConstFactor);
            ArmBase.setPower(armpad.left_stick_y*baseConstFactor);
        }
        else {
            ArmBase.setPower(0);
            ArmBase2.setPower(0);
        }
        // else if (ArmBase.getPower() != 0.001 && ArmBase2.getPower() != 0.001) {
        //     ArmBase.setPower(0.001);
        //     ArmBase2.setPower(0.001);
        // }
        double topConstFactor = .4;
        // Stop the arm from moving backwards and screwing up the system
        if(Math.abs(armpad.right_stick_y) > 0.3 && !(armpad.right_stick_y > 0 && ArmTop.getCurrentPosition() > -10 )){
            ArmTop.setPower(armpad.right_stick_y*topConstFactor);
        }
        else {
            ArmTop.setPower(0);
        }
        // else if (ArmBase.getPower() != 0.001) {
        //     ArmTop.setPower(0.001);
        // }


         //Hand
        if (!previousRightBumper && armpad.right_bumper) toggleHand();
        previousRightBumper = armpad.right_bumper;

        if (!previousLeftBumper && armpad.left_trigger > 0.9) {
            toggleTwister();
        }
        previousLeftBumper = armpad.left_trigger > 0.9;
        //ClawR.setPosition(1);
//        if (armpad.a) {
//            Hand.setPosition(0);
//        }
//         //boolean previousLeftBumper = armpad.left_bumper;
//        if (armpad.b){
//            Hand.setPosition(1);
//        }
        if(!twisterTurned) {
            Hand.setPosition(0.3 + (ArmBase.getCurrentPosition() / 640.0 / 5.5 * 0.22) - (ArmTop.getCurrentPosition() / 640.0 / 5.5));
        }
        else{
            Hand.setPosition(0.35);
        }
        //Hand.setPosition(1);



        //645.6 ticks max
        //30/48
    }

    //Getters
    public int getThePosition(){ return ArmBase.getCurrentPosition();}
    public int getThePosition2(){ return ArmTop.getCurrentPosition();}
    public double getArmBase2Power(){ return ArmBase2.getPower();}
    public double getArmBasePosition(){ return ArmBase2.getCurrentPosition();}
    public double getArmTopPower(){ return ArmTop.getPower();}
    public double getHandPos(){ return Hand.getPosition();}
    public double getClawPos(){ return Twister.getPosition();}

    public void setHand(boolean closed) {
        handClosed = closed;
        if (handClosed) {
            ClawR.setPosition(0.40);
        } else {
            ClawR.setPosition(0.6);
        }
    }

    public void toggleHand() {
        setHand(!handClosed);
    }

    public void setTwister(boolean turned) {
        twisterTurned = turned;
        if (twisterTurned) {
            Twister.setPosition(0.92);
        } else {
            Twister.setPosition(0.20);
        }
    }

    public void toggleTwister() {
        setTwister(!twisterTurned);
    }

    public static int inchesToCounts(double inchesToDrive) {
        double circumference = Constants.WHEEL_DIAMETER_IN_INCHES * Math.PI;
        double rotations = inchesToDrive / circumference;
        double countsToDrive = rotations * Constants.WHEEL_MOTOR_COUNTS_PER_ROTATION;
        return (int) countsToDrive;
    }

    /** `face` should be one of the three labels in the `LABELS` array. */
    public void moveTo(Signal face, Direction direction) {
        switch (direction) {
            case Right:
                moveOnXAxis(inchesToCounts(18.5)); //from leftmost position
                moveOnYAxis(inchesToCounts(4)); //distance to cone
                this.setHand(false);
                moveOnYAxis(inchesToCounts(-4));
                ClawR.setPosition(1);
                if (face == Signal.Three) {
                    moveOnXAxis(inchesToCounts((Constants.TILE_LENGTH_IN_INCHES)/2.0));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                }
                else if (face == Signal.Two) {
                    moveOnXAxis(inchesToCounts((Constants.TILE_LENGTH_IN_INCHES)/2.0));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES -1 ));
                    moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
                    moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES/6.0));
                }
                else if (face == Signal.One) {
                    moveOnXAxis(inchesToCounts((Constants.TILE_LENGTH_IN_INCHES)/2.0));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES - 1));
                    moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
                    moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
                    moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES/6.0));
                }
                break;

            case Left:
                moveOnXAxis(inchesToCounts(-18.5)); //from rightmost position
                moveOnYAxis(inchesToCounts(4)); //distance to cone
                this.setHand(false);
                moveOnYAxis(inchesToCounts(-4));
                ClawR.setPosition(1);
                if (face == Signal.One) {
                    moveOnXAxis(inchesToCounts((-Constants.TILE_LENGTH_IN_INCHES)/2.0));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                }
                else if (face == Signal.Two) {
                    moveOnXAxis(inchesToCounts((-Constants.TILE_LENGTH_IN_INCHES)/2.0));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES -1));
                    moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES/6.0));
                }
                else if (face == Signal.Three) {
                    moveOnXAxis(inchesToCounts((-Constants.TILE_LENGTH_IN_INCHES)/2.0));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES -1));
                    moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
                    moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES/6.0));
                }
                break;
        }


    }

    public void moveOnXAxis(int ticks) {
        DcMotorEx frontLeft = FLW,
            frontRight = FRW,
            rearLeft = BLW,
            rearRight = BRW;

        // Reset encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setTargetPosition(ticks);
        rearLeft.setTargetPosition(-ticks);
        frontRight.setTargetPosition(-ticks);
        rearRight.setTargetPosition(ticks);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setVelocity(-speed);
        rearLeft.setVelocity(speed);

        frontRight.setVelocity(speed);
        rearRight.setVelocity(-speed);

        while (opMode.opModeIsActive() && frontLeft.isBusy()) {
          // Loop until the motor reaches its target position.
          opMode.telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
          opMode.telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
          opMode.telemetry.addData("Rear Left Encoder", rearLeft.getCurrentPosition());
          opMode.telemetry.addData("Rear Right Encoder", rearRight.getCurrentPosition());
          opMode.telemetry.update();
        }

        opMode.sleep(250);
    }

    public void moveOnYAxis(int ticks) {
        DcMotorEx frontLeft = FLW,
            frontRight = FRW,
            rearLeft = BLW,
            rearRight = BRW;

        // Reset encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set position
        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);
        rearLeft.setTargetPosition(ticks);
        rearRight.setTargetPosition(ticks);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setVelocity(speed);
        frontRight.setVelocity(speed);
        rearLeft.setVelocity(speed);
        rearRight.setVelocity(speed);

        while (opMode.opModeIsActive() && frontLeft.isBusy()) {
          // Loop until the motor reaches its target position.
          opMode.telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
          opMode.telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
          opMode.telemetry.addData("Rear Left Encoder", rearLeft.getCurrentPosition());
          opMode.telemetry.addData("Rear Right Encoder", rearRight.getCurrentPosition());
          opMode.telemetry.update();
        }

        opMode.sleep(250);
    }
}