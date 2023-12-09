package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * This 2022-2023 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine which image is being presented to the robot.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "Autonomous Mode RIGHT", group = "Concept")
//@Disabled
public class AutoModeRight extends LinearOpMode {
    private RoboController roboController;

    //private SolidColorStrategy strategy;
    //private Direction direction = Direction.Right;


    @Override
    public void runOpMode() {
        roboController = new RoboController(this);
        //strategy = new SolidColorStrategy(this, direction);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        //roboController.ClawR.setPosition(0.5);

        if (opModeIsActive()) {
            /*
            sleep(3000);

            roboController.moveOnXAxis(RoboController.inchesToCounts(52));
             */


            // starting autonomous scoring
            sleep(2000);
            roboController.moveOnXAxis(RoboController.inchesToCounts(27));
            roboController.moveOnYAxis(RoboController.inchesToCounts(27));
            roboController.Spin(RoboController.inchesToCounts(-18));
            roboController.moveOnYAxis(RoboController.inchesToCounts(-20));
            while(roboController.ArmR.getCurrentPosition() < 2100) {
                roboController.ArmL.setPower(0.45);
                roboController.ArmR.setPower(0.45);
            }
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);
            roboController.ClawR.setPosition(1);
            roboController.ClawL.setPosition(0);
            sleep(2000);
            roboController.ClawR.setPosition(0);
            roboController.ClawL.setPosition(1);
            while(roboController.ArmR.getCurrentPosition() > 10) {
                roboController.ArmL.setPower(-0.45);
                roboController.ArmR.setPower(-0.45);
            }
            roboController.ArmL.setPower(0);
            roboController.ArmR.setPower(0);
            roboController.moveOnYAxis(RoboController.inchesToCounts(3));
            roboController.moveOnXAxis(RoboController.inchesToCounts(-27));
            roboController.moveOnYAxis(RoboController.inchesToCounts(-27));

            /*
            Signal face = Signal.One;
            for (int i = 0; i < 5; i++) {
                Signal newFace = getConeFace();
                if (newFace != null) {
                    face = newFace;
                    break;
                }
                else sleep(2000);
            }
            roboController.moveTo(face, direction);

             */

            stop();
        }
    }

    /*
    private Signal getConeFace() {
        return strategy.getConePosition();
    }

     */
}