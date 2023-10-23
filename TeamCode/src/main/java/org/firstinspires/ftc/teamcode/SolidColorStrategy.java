package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class SolidColorStrategy {
    OpenCvCamera camera;
    SolidColorSignalPipeline pipeline;
    LinearOpMode opMode;

    public SolidColorStrategy(LinearOpMode opMode, Direction direction) {
        this.opMode = opMode;

        int cameraMonitorViewId = opMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", opMode.hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        // pipeline = new SolidColorSignalPipeline();
        pipeline = new SolidColorSignalPipeline(direction);

        camera.setPipeline(pipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });
    }

    public Signal getConePosition() {
        for (int attempt = 1; attempt <= 5; attempt++) {
            opMode.telemetry.addData("Attempt", "%d", attempt);
            opMode.telemetry.update();

            // Calling getDetectionsUpdate() will only return an object if there was a new frame
            // processed since the last time we called it. Otherwise, it will return null. This
            // enables us to only run logic when there has been a new frame, as opposed to the
            // getLatestDetections() method which will always return an object.
            Signal signal = pipeline.getSignal();

            // If there's been a new frame...
            if (signal != null) {
                return signal;
            }

            // Wait a second; the robot can be a bit slow sometimes
            opMode.sleep(2000);
        }

        // If we still can't find anything, give up
        return Signal.One;
    }
}
