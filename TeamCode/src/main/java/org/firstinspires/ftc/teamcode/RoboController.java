package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.lang.Math;

public class RoboController {

    public static boolean almostEqual(double a, double b, double eps){
        return Math.abs(a-b)<eps;
    }
    private ElapsedTime runtime = new ElapsedTime();
    // change to 5000?????
    public static int speed = 3000;

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
    public CRServo ClawR;
    public CRServo ClawL;
    public Servo Drone;

    public DistanceSensor distanceSensor;



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

        // Distance Sensor
        distanceSensor = hardwareMap.get(DistanceSensor.class, "BackupSens");

        //Arms
        ArmL = hardwareMap.get(DcMotor.class,"ArmL");
        ArmR = hardwareMap.get(DcMotor.class,"ArmR");
        Extender = hardwareMap.get(DcMotor.class,"Extender");
        ClawR = hardwareMap.get(CRServo.class, "ClawR");
        ClawL = hardwareMap.get(CRServo.class, "ClawL");
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

    public boolean canDriveBack = true;
    public boolean rumbled = false;

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

        /*
        // if the bot is close enough to the back board (or any object really), meaning if it's
        // 5 inches away or closer, vibrate the controller of the wheels to alert the driver
        if(distanceSensor.getDistance(DistanceUnit.INCH) <= 5) {
            movepad.rumble(2000);
        }
        */

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

            //direction = Compass.East;
        }
        else if(movepad.left_stick_x < -0.2){
            if(slowDown){
                strafePower = -0.3;
            } else {
                strafePower = movepad.left_stick_x;
            }
            //direction = Compass.West;
        }
        else if(movepad.left_trigger > 0.2){
            if(driveMode){
                if(slowDown){
                    drivePower = -0.3;
                } else {
                    drivePower = -movepad.left_trigger;
                }
            }

            //direction = Compass.North;
        }
        else if(movepad.right_trigger > 0.2){
            if(driveMode){
                if(slowDown){
                    drivePower = 0.3;
                } else {
                    drivePower = movepad.right_trigger;
                }
            }

            //direction = Compass.South;
        }
        else if(movepad.left_stick_y > 0.2){
            if(!driveMode){
                if(slowDown) {
                    drivePower = -0.3;
                } else {
                    drivePower = -movepad.left_stick_y;
                }
            }
        }
        else if(movepad.left_stick_y < -0.2){
            if(!driveMode){
                if(slowDown) {
                    drivePower = 0.3;
                } else {
                    drivePower = -movepad.left_stick_y;
                }
            }
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
    boolean rotate = false;
    boolean open2 = false;

    //Not Implemented
    // gamepad 2 (red) - movement of arm
    public void interpretArmpad(Gamepad armpad){

        // sets max range for how far the arm can move back
        // when the arm goes further than 2300, the power of the arm is set to go in the
        // opposite direction to counteract the power set by the joystick
        if(ArmR.getCurrentPosition() > 2300) {
            ArmL.setPower(-0.1);
            ArmR.setPower(-0.1);
        }
        // moving the left joystick up or down will also move the arm up or down
        else if(armpad.left_stick_y > 0.5 ){
            ArmL.setPower(-0.45);
            ArmR.setPower(-0.45);
        }
        else if(armpad.left_stick_y < -0.5) {
            if (ArmR.getCurrentPosition() > 1100) {
                ArmL.setPower(0.3);
                ArmR.setPower(0.3);
            } else {
                ArmL.setPower(0.45);
                ArmR.setPower(0.45);
            }
        }

        else{
            ArmL.setPower(0);
            ArmR.setPower(0);
        }

        if(armpad.right_stick_y > 0.5){
            Extender.setPower(-0.7);
        }
        else if(armpad.right_stick_y < -0.5){
            Extender.setPower(0.7);
        }
        else{
            Extender.setPower(0);
        }

        /*
        if(a && armpad.left_bumper){
            open = !open;
            if(!open) {
                ClawR.setPosition(0);
                ClawL.setPosition(0.8);
            }
            if(open){
                ClawR.setPosition(0.8);
                ClawL.setPosition(0);
            }
        }
        a = !armpad.left_bumper;
        */



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
                Wrist.setPosition(0.05);
            }
            if(open2){
                Wrist.setPosition(0.525);
            }
        }
        // used to set b to the opposite state of the right bumper (true/false or false/true)
        // so that pressing the right bumper won't flip the wrist forever. it will only flip
        // it once in the one instance where b and the right bumper are both true, before b
        // gets set to false
        // (or at least i think this is how it functions)
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

            // uses square to toggle drone launcher position
            if(c && armpad.square){
                if(Drone.getPosition() > 0){
                    Drone.setPosition(0);
                } else {
                    Drone.setPosition(0.7);
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

    // complete !!!!
    public void autoMiddle(){
        // flip claw up
        this.Wrist.setPosition(0.53);
        opMode.sleep(500);
        // move up to the beacon
        this.moveOnYAxis(this.inchesToCounts(24));
        // flip claw down
        Wrist.setPosition(0.05);
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
        this.Wrist.setPosition(0.53);
        opMode.sleep(250);
    }
    public void autoLeft(){
        // flip claw up
        this.Wrist.setPosition(0.53);
        opMode.sleep(500);
        // move up to panel in front
        this.moveOnYAxis(this.inchesToCounts(25));
        // move slightly right to make room for pixel
        this.moveOnXAxis(this.inchesToCounts(8));
        // spin 90 degrees left
        this.Spin(this.inchesToCounts(-18));
        // flip claw down
        this.Wrist.setPosition(0.05);
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
        this.Wrist.setPosition(0.53);
        opMode.sleep(500);
        // spin back to face forward
        this.Spin(this.inchesToCounts(18));
        // move forward to the middle of the panel
        this.moveOnXAxis(this.inchesToCounts(-8));
    }

    // complete !!!!
    public void autoRight(){
        // flip claw up
        this.Wrist.setPosition(0.53);
        opMode.sleep(500);
        // move up to panel in front
        this.moveOnYAxis(this.inchesToCounts(25));
        // move slightly left to make room for pixel
        this.moveOnXAxis(this.inchesToCounts(-8));
        // spin 90 degrees right
        this.Spin(this.inchesToCounts(18));
        // flip claw down
        this.Wrist.setPosition(0.05);
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
        this.Wrist.setPosition(0.53);
        opMode.sleep(500);
        // spin back to face forward
        this.Spin(this.inchesToCounts(-18));
        // move forward to the middle of the panel
        this.moveOnXAxis(this.inchesToCounts(8));
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
        this.moveOnYAxis(RoboController.inchesToCounts(-27));

        // move left to the middle of the adjacent panel
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
        this.moveOnXAxis(RoboController.inchesToCounts(27));

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

    public void closeToBoardObstructed(int isBlue){ //not side

        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(18*isBlue));
        this.moveOnYAxis(this.inchesToCounts(36));
        this.Spin(this.inchesToCounts(18*isBlue));
    }
}