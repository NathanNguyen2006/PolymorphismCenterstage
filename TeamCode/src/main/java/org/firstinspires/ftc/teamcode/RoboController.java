package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import java.lang.Math;

public class RoboController {
    public static int speed = 2000;

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
    public Servo WristL;
    public Servo WristR;
    public CRServo ClawR;
    public CRServo ClawL;
    public Servo Drone;

    public DistanceSensor distanceSensor;




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
        FLW.setDirection(DcMotor.Direction.FORWARD);
        FRW.setDirection(DcMotor.Direction.FORWARD);
        BLW.setDirection(DcMotor.Direction.REVERSE);
        BRW.setDirection(DcMotor.Direction.FORWARD);

        // Distance Sensor
        // distanceSensor = hardwareMap.get(DistanceSensor.class, "BackupSens");

        //Arms
        ArmL = hardwareMap.get(DcMotor.class,"ArmL");
        ArmR = hardwareMap.get(DcMotor.class,"ArmR");
        Extender = hardwareMap.get(DcMotor.class,"Ext");
        ClawR = hardwareMap.get(CRServo.class, "ClawR");
        ClawL = hardwareMap.get(CRServo.class, "ClawL");
        WristL = hardwareMap.get(Servo.class, "WristL");
        WristR = hardwareMap.get(Servo.class, "WristR");
        Drone = hardwareMap.get(Servo.class, "Drone");


        //ArmL.setDirection(DcMotor.Direction.REVERSE);

        //Reset Encoders
        ArmL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ArmR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    //arm stuff
    public boolean driveMode = true;

    public boolean DM = false;

    public boolean slowDown = false;


    // gamepad 1 (blue) - movement of wheels
    public void interpretMovepad(Gamepad movepad){
        Gamepad.RumbleEffect rumbleEffect1 = new Gamepad.RumbleEffect.Builder()
                .addStep(1.0, 1.0, 500)  //  Rumble 100% for 500 mSec
                .build();

        Gamepad.RumbleEffect rumbleEffect2 = new Gamepad.RumbleEffect.Builder()
                .addStep(1.0, 1.0, 500)  //  Rumble 100% for 500 mSec
                .addStep(0.0, 0.0, 300)  //  Pause for 300 mSec
                .addStep(1.0, 1.0, 500)  //  Rumble 100% for 500 mSec
                .build();

        FLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // driveMode = true --> using left and right triggers for wheels (vibrate twice when switching to this)
        // driveMode = false --> using up and down left joystick for wheels (vibrate once when switching to this)
        if(DM && movepad.triangle){
            driveMode = !driveMode;

            if(driveMode){
                movepad.runRumbleEffect(rumbleEffect2);
            } else {
                movepad.runRumbleEffect(rumbleEffect1);
            }
        }
        DM = !(movepad.triangle);

        if(movepad.left_bumper || movepad.right_bumper){
            slowDown = true;
        } else {
            slowDown = false;
        }

        if(Math.abs(movepad.right_stick_x) > .2){
            if(slowDown){
                if(movepad.right_stick_x > 0){
                    turnPower = 0.2;
                } else if(movepad.right_stick_x < 0){
                    turnPower = -0.2;
                }
            } else {
                turnPower = movepad.right_stick_x * 0.5;
            }
        }
        else{
            turnPower = 0;
        }

        if(movepad.left_stick_x > 0.2){
            if(slowDown){
                strafePower = 0.3;
            } else {
                strafePower = movepad.left_stick_x;
            }
        } else if(movepad.left_stick_x < -0.2){
            if(slowDown){
                strafePower = -0.3;
            } else {
                strafePower = movepad.left_stick_x;
            }
        } else if(movepad.left_trigger > 0.2){
            if(driveMode){
                if(slowDown){
                    drivePower = -0.3;
                } else {
                    drivePower = -movepad.left_trigger;
                }
            }
        } else if(movepad.right_trigger > 0.2){
            if(driveMode){
                if(slowDown){
                    drivePower = 0.3;
                } else {
                    drivePower = movepad.right_trigger;
                }
            }
        } else if(movepad.left_stick_y > 0.2){
            if(!driveMode){
                if(slowDown) {
                    drivePower = -0.3;
                } else {
                    drivePower = -movepad.left_stick_y;
                }
            }
        } else if(movepad.left_stick_y < -0.2){
            if(!driveMode){
                if(slowDown) {
                    drivePower = 0.3;
                } else {
                    drivePower = -movepad.left_stick_y;
                }
            }
        } else{
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

    boolean b = false;
    boolean c = false;
    boolean permaPower = false;
    boolean open2 = false;
    boolean open123 = false;

    //Not Implemented
    // gamepad 2 (red) - movement of arm
    public void interpretArmpad(Gamepad armpad){

        // sets max range for how far the arm can move back
        // when the arm goes further than 2300, the power of the arm is set to go in the
        // opposite direction to counteract the power set by the joystick
//        if(ArmR.getCurrentPosition() > 2300) {
//            ArmL.setPower(-0.1);
//            ArmR.setPower(-0.1);
//        }

        ArmL.setDirection(DcMotorSimple.Direction.FORWARD);
        ArmR.setDirection(DcMotorSimple.Direction.FORWARD);

        // moving the left joystick up or down will also move the arm up or down
        if(armpad.left_stick_y > 0.5 ){
            ArmL.setPower(-1);
            ArmR.setPower(-1);
        }
        else if(armpad.left_stick_y < -0.5 ) {
//            if (ArmR.getCurrentPosition() > 1100) {
//                ArmL.setPower(0.3);
//                ArmR.setPower(0.3);
//            } else {
            ArmL.setPower(1);
            ArmR.setPower(1);
            // }
        }

        else{
            ArmL.setPower(0);
            ArmR.setPower(0);
            //Extender.setPower(-1);
        }

        if(armpad.right_stick_y > 0.5){
            Extender.setPower(-1);
        }
        else if(armpad.right_stick_y < -0.5){
            Extender.setPower(1);
        }
        else{
            Extender.setPower(0);
        }

        // depending on the direction the claw is set to, it will rotate in that
        // direction if it's set to rotate
        // otherwise the claw won't rotate at all
        if(armpad.dpad_up){
            ClawR.setDirection(DcMotorSimple.Direction.FORWARD);
            ClawR.setPower(0.5);
            ClawL.setDirection(DcMotorSimple.Direction.REVERSE);
            ClawL.setPower(0.5);
        } else if(armpad.dpad_down) {
            ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
            ClawR.setPower(0.5);
            ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
            ClawL.setPower(0.5);
        } else {
            ClawR.setPower(0);
            ClawL.setPower(0);
        }


        //thing
        if(armpad.circle){
            ArmL.setTargetPosition(-963);
            ArmR.setTargetPosition(-976);
            Extender.setTargetPosition(1960);
            ArmL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ArmR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            if(ArmL.getCurrentPosition()> ArmL.getTargetPosition()) {
                ArmL.setPower(1);
            }
            else  if(ArmL.getCurrentPosition()< ArmL.getTargetPosition()) {
                ArmL.setPower(-1);
            }
            else{
                ArmL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if(ArmR.getCurrentPosition()> ArmR.getTargetPosition()) {
                ArmR.setPower(1);
            }
            else if(ArmR.getCurrentPosition()< ArmR.getTargetPosition()) {
                ArmR.setPower(-1);
            }
            else{
                ArmR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if(Extender.getCurrentPosition() > Extender.getTargetPosition()) {
                Extender.setPower(1);
            }
            else if(Extender.getCurrentPosition()< Extender.getTargetPosition()) {
                Extender.setPower(-1);
            }
            else{
                Extender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
        else{
            ArmR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            ArmL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Extender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if(armpad.share){
            ArmL.setTargetPosition(0);
            ArmR.setTargetPosition(0);
            Extender.setTargetPosition(0);
            ArmL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            ArmR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            if(ArmL.getCurrentPosition()> ArmL.getTargetPosition()) {
                ArmL.setPower(1);
            }
            else  if(ArmL.getCurrentPosition()< ArmL.getTargetPosition()) {
                ArmL.setPower(-1);
            }
            else{
                ArmL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if(ArmR.getCurrentPosition()> ArmR.getTargetPosition()) {
                ArmR.setPower(1);
            }
            else if(ArmR.getCurrentPosition()< ArmR.getTargetPosition()) {
                ArmR.setPower(-1);
            }
            else{
                ArmR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            if(Extender.getCurrentPosition() > Extender.getTargetPosition()) {
                Extender.setPower(1);
            }
            else if(Extender.getCurrentPosition()< Extender.getTargetPosition()) {
                Extender.setPower(-1);
            }
            else{
                Extender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }
        if(b && (armpad.right_bumper)){
            open2 = !open2;
            if(!open2) {
                WristL.setPosition(0.83);
                WristR.setPosition(0.17);
            }
            if(open2){
                WristL.setPosition(0.3);
                WristR.setPosition(0.7);
            }
        }

        // used to set b to the opposite state of the right bumper (true/false or false/true)
        // so that pressing the right bumper won't flip the wrist forever. it will only flip
        // it once in the one instance where b and the right bumper are both true, before b
        // gets set to false
        // (or at least i think this is how it functions)
        b = !(armpad.right_bumper);

        /*
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
         */

        // uses square to toggle drone launcher position
        if(c && armpad.square){
            open123 = !open123;
            if(open123){
                Drone.setPosition(1);
            } else {
                Drone.setPosition(0.725);
            }
        }
        c = !(armpad.square);

            /*
            if(armpad.circle){
                Drone.setPosition(1);

            }
            if(armpad.square){
                Drone.setPosition(0);
            }
             */

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
        rearRight.setTargetPosition(-ticks); //pos

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

    // complete !!!!
    public void autoMiddle(){
        // flip claw up
        this.WristL.setPosition(0.47);
        this.WristR.setPosition(0.53);
        opMode.sleep(500);
        // move up to the beacon
        this.moveOnYAxis(this.inchesToCounts(26));
        // flip claw down
        WristL.setPosition(0.95);
        WristR.setPosition(0.05);
        opMode.sleep(750);
        // rotate pixel out
        this.ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
        this.ClawR.setPower(0.75);
        this.ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
        this.ClawL.setPower(0.75);
        // rotate for half a second
        opMode.sleep(500);
        // stop rotating claw
        this.ClawR.setPower(0);
        this.ClawL.setPower(0);
        // flip claw back up
        this.WristL.setPosition(0.47);
        this.WristR.setPosition(0.53);
        opMode.sleep(250);
        this.moveOnYAxis(RoboController.inchesToCounts(-26));
    }

    // backPositions:
    // middle: 0
    // left: -1
    // right: 1
    public void autoAwayFromTruss(int position){
        // flip claw up
        this.WristL.setPosition(0.47);
        this.WristR.setPosition(0.53);
        opMode.sleep(500);
        // move to panel next to it
        this.moveOnXAxis(this.inchesToCounts(13*position));
        // move to panel forward
        this.moveOnYAxis(this.inchesToCounts(12));
        // flip claw down
        WristL.setPosition(0.95);
        WristR.setPosition(0.05);
        opMode.sleep(750);
        // rotate pixel out
        this.ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
        this.ClawR.setPower(0.75);
        this.ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
        this.ClawL.setPower(0.75);
        // rotate for half a second
        opMode.sleep(500);
        // stop rotating claw
        this.ClawR.setPower(0);
        this.ClawL.setPower(0);
        // flip claw back up
        this.WristL.setPosition(0.47);
        this.WristR.setPosition(0.53);
        opMode.sleep(500);
        // move to panel backwards
        this.moveOnYAxis(this.inchesToCounts(-11));
        // move to panel next to it
        this.moveOnXAxis(this.inchesToCounts(13*-position));
    }

    // complete !!!!
    // backPositions:
    // middle: 0
    // left: -1
    // right: 1
    public void autoCloseToTruss(int position){
        // flip claw up
        this.WristL.setPosition(0.47);
        this.WristR.setPosition(0.53);
        opMode.sleep(500);
        // move up to panel in front
        this.moveOnYAxis(this.inchesToCounts(27));
        // spin 90 degrees
        this.Spin(this.inchesToCounts(18*position));
        // flip claw down
        WristL.setPosition(0.95);
        WristR.setPosition(0.05);
        opMode.sleep(750);
        // rotate pixel out
        this.ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
        this.ClawR.setPower(0.75);
        this.ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
        this.ClawL.setPower(0.75);
        // rotate for half a second
        opMode.sleep(500);
        // stop rotating claw
        this.ClawR.setPower(0);
        this.ClawL.setPower(0);
        // flip claw back up
        this.WristL.setPosition(0.47);
        this.WristR.setPosition(0.53);
        opMode.sleep(500);
        // spin back to face forward
        this.Spin(this.inchesToCounts(18*-position));
        // move forward to the middle of the panel
        this.moveOnXAxis(this.inchesToCounts(1*position));
        this.moveOnYAxis(RoboController.inchesToCounts(-27));
    }

    public void farToBoard(int isBlue){ //not middle
        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(-18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(72));
        this.Spin(this.inchesToCounts(-18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(-18*isBlue));
    }

    // complete !!!!
    // backPositions:
    // middle: 0
    // left: 1
    // right: -1
    public void closeToBoard(int isBlue, int backPosition){ //not side
        // move to panel next to it
        this.moveOnXAxis(RoboController.inchesToCounts(27*-isBlue));

        // move forward to the middle of the adjacent panel
        this.moveOnYAxis(RoboController.inchesToCounts(27));

        // turn right by about 90 degrees
        this.Spin(RoboController.inchesToCounts(18*isBlue));

        // move back right against the middle of the backboard
        this.moveOnYAxis(RoboController.inchesToCounts(-19));

        //reposition on board
        this.moveOnXAxis(RoboController.inchesToCounts(8*backPosition));

        // move the arm back until it reaches a position that's right against the backboard (2050)
        while(this.ArmR.getCurrentPosition() < 2050) {
            this.ArmL.setPower(0.45);
            this.ArmR.setPower(0.45);
        }

        // once the arm is against the backboard, stop moving it back
        this.ArmL.setPower(0);
        this.ArmR.setPower(0);

        // push pixels out
        this.ClawR.setDirection(DcMotorSimple.Direction.REVERSE);
        this.ClawR.setPower(0.5);
        this.ClawL.setDirection(DcMotorSimple.Direction.FORWARD);
        this.ClawL.setPower(0.5);

        // wait one and a half seconds in case the pixels haven't been completely scored yet
        opMode.sleep(1500);

        // stop rotating claw
        this.ClawR.setPower(0);
        this.ClawL.setPower(0);

        // move the arm forward until it reaches a position that's about where the floor is (10)
        while(this.ArmR.getCurrentPosition() > 10) {
            this .ArmL.setPower(-0.45);
            this.ArmR.setPower(-0.45);
        }

        // once the arm is against the floor, stop moving it forward
        this.ArmL.setPower(0);
        this.ArmR.setPower(0);

        // wait a second to give the bot time to flip the claw
        opMode.sleep(1000);

        // move forward so that the bot isn't right against the backboard
        this.moveOnYAxis(RoboController.inchesToCounts(3));

        // move right to the middle of the adjacent panel
        this.moveOnXAxis(RoboController.inchesToCounts(27*isBlue));

        // move back to the middle of the adjacent panel (to park in the backstage area)
        this.moveOnYAxis(RoboController.inchesToCounts(-13));

        // autonomous mode has now ended
        opMode.stop();
    }

    public void farToBoardObstructed(int isBlue){ //not middle
        this.Spin(this.inchesToCounts(18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(24));
        this.Spin(this.inchesToCounts(-18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(24));
        this.Spin(this.inchesToCounts(-18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(96));
        this.Spin(this.inchesToCounts(-18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(24));
        this.Spin(this.inchesToCounts(-18*isBlue));
    }
    //

    public void closeToBoardObstructed(int isBlue){ //not side

        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(18*isBlue));
    }
}