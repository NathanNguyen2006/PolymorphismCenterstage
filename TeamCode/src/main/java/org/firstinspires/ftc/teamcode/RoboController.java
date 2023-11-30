package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.lang.Math;

public class RoboController {

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
    public DcMotor ArmL;
    public DcMotor ArmR;
    public DcMotor Extender;
    public Servo Wrist;
    public Servo ClawR;
    public Servo ClawL;
    public Servo Drone;



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

    public RoboController(LinearOpMode opMode){
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
        ArmL = hardwareMap.get(DcMotor.class,"ArmL");
        ArmR = hardwareMap.get(DcMotor.class,"ArmR");
        Extender = hardwareMap.get(DcMotor.class,"Extender");
        ClawR = hardwareMap.get(Servo.class, "ClawR");
        ClawL = hardwareMap.get(Servo.class, "ClawL");
        Wrist = hardwareMap.get(Servo.class, "Wrist");
        Drone = hardwareMap.get(Servo.class, "Drone");


        ArmL.setDirection(DcMotor.Direction.REVERSE);

        //Reset Encoders
        ArmL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ArmR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Pid stuff unused lol
        //ArmTopPid = new Pid( 0.3, 0.3, 0.3, 1, -1, 1);
       // ArmBasePid = new Pid(0.1, 0.1, 0.15, 1, -1, 1);


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
            strafePower = 1;
            //direction = Compass.East;
        }
        else if(movepad.left_stick_x < -0.3){
            strafePower = -1;
            //direction = Compass.West;
        }
        else if(movepad.left_stick_y < -0.3){
            drivePower = 0.75;
            //direction = Compass.North;
        }
        else if(movepad.left_stick_y > 0.3){
            drivePower = -0.75;
            //direction = Compass.South;
        }
        else{
            drivePower = 0;
            strafePower = 0;
        }


        //Direction
        if (movepad.dpad_up) direction = Compass.North; //drivePower = 0.5;
        else if (movepad.dpad_down) direction = Compass.South; //drivePower = -0.5;
        //else{ drivePower = 0;}

        else if (movepad.dpad_right) direction = Compass.East; //strafePower = 0.9;
        else if (movepad.dpad_left) direction = Compass.West; //strafePower = -0.9;
        //else {
        //    strafePower = 0;
        //}

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
            BRW.setPower(strafePower * 0.9);
            BLW.setPower(-strafePower * 0.9); //-strafe
        }
        else{
            FRW.setPower(-0);
            FLW.setPower(0);
            BRW.setPower(0);
            BLW.setPower(-0);
        }
    }

    boolean a = true;
    boolean b = false;
    boolean c = false;
    boolean permaPower = false;
    boolean open = false;
    boolean open2 = false;

    //Not Implemented
    public void interpretArmpad(Gamepad armpad){

        if(ArmR.getCurrentPosition() > 1850) {
            ArmL.setPower(-0.1);
            ArmR.setPower(-0.1);
        }
        else if(armpad.left_stick_y > 0.5 ){
            ArmL.setPower(-0.45);
            ArmR.setPower(-0.45);
        }
        else if(armpad.left_stick_y < -0.5) {
            ArmL.setPower(0.45);
            ArmR.setPower(0.45);
        }
        else if(armpad.left_stick_y < -0.5 && ArmR.getCurrentPosition() > 1100) {
            ArmL.setPower(0.3);
            ArmR.setPower(0.3);
        }

        else{
            ArmL.setPower(0);
            ArmR.setPower(0);
        }
         if(armpad.right_stick_y > 0.5  ){

            Extender.setPower(0.7  );
        }
        else if(armpad.right_stick_y < -0.5 ){
            Extender.setPower(-0.7);
        }
        else{
            Extender.setPower(0);
        }

        if(a && armpad.left_bumper){
            open = !open;
            if(!open) {
                ClawR.setPosition(0.4);
                ClawL.setPosition(0.8);
            }
            if(open){
                ClawR.setPosition(1);
                ClawL.setPosition(0.4);
            }
        }
        a = !armpad.left_bumper;

        //if(armpad.right_bumper) {
            //ClawR.setPosition(0.4);
            //ClawL.setPosition(0.9);
            //double o = ClawR.getPosition();
            //double p = ClawL.getPosition();
            //p = p + 0.05;
            //o = o - 0.05;
            //ClawL.setPosition(p);
            //ClawR.setPosition(o);
        //}
        //clawR = 0.4
        //clawL = 0.9

        //b = !armpad.dpad_left;

//            if(a == true && armpad.dpad_up) {
//                double x = Wrist.getPosition();
//                x = x - 0.05;
//                Wrist.setPosition(x);
//
//            }
//        a = !armpad.dpad_up;

        if(b && (armpad.right_bumper)){
            open2 = !open2;
            if(!open2) {
                Wrist.setPosition(0);
            }
            if(open2){
                Wrist.setPosition(0.55);
            }
        }
        b = !(armpad.right_bumper);

//        if(armpad.left_trigger != 0){
//            Wrist.setPosition(0);
//        }
//            if(armpad.right_trigger != 0) {
//            Wrist.setPosition(0.55);
//        }

            if(armpad.triangle){
                ArmL.setTargetPosition(-150);
                ArmR.setTargetPosition(-150);
                ArmL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                ArmR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                permaPower = true;
            }

            if(permaPower){
                ArmR.setPower(1);
                ArmL.setPower(1);
            }

            if(armpad.circle){
                Drone.setPosition(1);

            }
            if(armpad.square){
                Drone.setPosition(0);
            }

            //double 1775 for board
        //double 95 for floor
    }

    //Getters
    public int getThePosition(){ return ArmL.getCurrentPosition();}

    public static int inchesToCounts(double inchesToDrive) {
        double circumference = Constants.WHEEL_DIAMETER_IN_INCHES * Math.PI;
        double rotations = inchesToDrive / circumference;
        double countsToDrive = rotations * Constants.WHEEL_MOTOR_COUNTS_PER_ROTATION;
        return (int) countsToDrive;
    }

    public void moveLeftABit(int asd){
        if (asd < 60000) {
            FRW.setPower(0.5);
            FLW.setPower(-0.5);
            BRW.setPower(-0.5);
            BLW.setPower(0.5);
        }
        else{
            FRW.setPower(0);
            FLW.setPower(0);
            BRW.setPower(0);
            BLW.setPower(0);
        }
    }
    /** `face` should be one of the three labels in the `LABELS` array. */
    public void moveTo(Signal face, Direction direction) {
        switch (direction) {
            case Right:
                moveOnXAxis(inchesToCounts(18.5)); //from leftmost position
                moveOnYAxis(inchesToCounts(4)); //distance to cone
                //this.setHand(false);
                moveOnYAxis(inchesToCounts(-4));
                //ClawR.setPosition(1);
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
                //this.setHand(false);
                moveOnYAxis(inchesToCounts(-4));
                //ClawR.setPosition(1);
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

    public void Spin(int ticks) {
        DcMotorEx frontLeft = FLW,
                frontRight = FRW,
                rearLeft = BLW,
                rearRight = BRW;

        // Reset encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setTargetPosition(ticks); //pos
        rearLeft.setTargetPosition(ticks); //neg
        frontRight.setTargetPosition(-ticks); //neg
        rearRight.setTargetPosition(-ticks); //po    s

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
}