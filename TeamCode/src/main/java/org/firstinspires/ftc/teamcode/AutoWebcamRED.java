package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Autonomous Mode Webcam RED", group = "Concept")

public class AutoWebcamRED  extends LinearOpMode {
    private RoboController roboController;
    private TFOD_Team_Prop_Detection camera = new TFOD_Team_Prop_Detection();

    @Override
    public void runOpMode() {
        /** Wait for the game to begin */
        camera.telemetryTfod();
        String currentLabel = camera.getLabel();

        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // autonomous scoring for the red side (towards the backboard)

            // raise the claw so that it stays up completely
            roboController.Wrist.setPosition(0.53);

            // wait a little until the claw is flipped up
            sleep(1500);

            if(currentLabel.equals("red beacon middle")){
                // move right to the middle of the adjacent panel
                roboController.moveOnXAxis(RoboController.inchesToCounts(27));
            } else if(currentLabel.equals("red beacon right")){
                // move right to the middle of the adjacent panel
                roboController.moveOnYAxis(RoboController.inchesToCounts(27));
            } else {
                roboController.moveOnYAxis(RoboController.inchesToCounts(3));
            }

            // autonomous mode has now ended
            stop();
        }
    }
}
