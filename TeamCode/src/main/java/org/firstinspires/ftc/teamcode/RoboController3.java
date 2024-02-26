package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.lang.Math;

public class RoboController3 {

        //for autonomous
        public static int speed = 1000;

        //wheel motors
        public DcMotorEx FRW;
        public DcMotorEx FLW;
        public DcMotorEx BRW;
        public DcMotorEx BLW;

        //drive power
        public double strafePower = 0;
        public double drivePower = 0;
        public double turnPower = 0;

        //init direction
        public Compass direction = Compass.North;

        //
        private LinearOpMode opMode;

        public RoboController3(LinearOpMode opMode){

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
        }

        //
        public boolean slowDown = false;

        public void interpretMovepad(Gamepad movepad){

            FLW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            FRW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BLW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BRW.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //slow down true or false
            if(movepad.left_bumper || movepad.right_bumper){
                slowDown = true;
            } else { slowDown = false; }

            //strafe right
            if(movepad.left_stick_x > 0.2){
                if(slowDown){
                    strafePower = 0.3;
                } else { strafePower = movepad.left_stick_x; }
            }

            //strafe left
            else if(movepad.left_stick_x < -0.2){
                if(slowDown){
                    strafePower = -0.3;
                } else { strafePower = movepad.left_stick_x; }
            }

            //turn right
            else if(movepad.right_stick_x > 0.2){
                if(slowDown) {
                    turnPower = 0.2;
                } else { turnPower = movepad.right_stick_x * 0.5; }
            }

            //turn left
            else if(movepad.right_stick_x < -0.2){
                if(slowDown) {
                    turnPower = -0.2;
                } else { turnPower = movepad.right_stick_x * 0.5; }
            }

            //drive back
            else if(movepad.left_trigger > 0.2){
                if(slowDown){
                    drivePower = -0.3;
                } else { drivePower = -movepad.left_trigger; }
            }

            //drive forward
            else if(movepad.right_trigger > 0.2){
                if(slowDown){
                    drivePower = 0.3;
                } else { drivePower = movepad.right_trigger; }
            }

            //stop
            else{
                drivePower = 0;
                strafePower = 0;
            }

            //setting power
            if(drivePower != 0){
                if(turnPower != 0){
                    //drive and turn
                    FRW.setPower(drivePower);
                    FLW.setPower(turnPower);
                    BRW.setPower(drivePower);
                    BLW.setPower(turnPower);
                }
                else if(strafePower != 0){
                    //drive and strafe
                    FRW.setPower(-strafePower);
                    FLW.setPower(drivePower);
                    BRW.setPower(drivePower);
                    BLW.setPower(-strafePower);
                }
                else{
                    //just drive
                    FRW.setPower(drivePower);
                    FLW.setPower(drivePower);
                    BRW.setPower(drivePower);
                    BLW.setPower(drivePower);
                }
            }
            else if(turnPower != 0){
                //just turn
                FRW.setPower(-turnPower);
                FLW.setPower(turnPower);
                BRW.setPower(-turnPower);
                BLW.setPower(turnPower);
            }
            else if(strafePower != 0){
                //just strafe
                FRW.setPower(-strafePower); //-strafe
                FLW.setPower(strafePower);
                BRW.setPower(strafePower * 0.9);
                BLW.setPower(-strafePower * 0.9); //-strafe
            }
            else{
                //stop all power
                FRW.setPower(-0);
                FLW.setPower(0);
                BRW.setPower(0);
                BLW.setPower(-0);
            }
        }
    }
