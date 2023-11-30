package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous
public class hatredanddepression2 extends LinearOpMode {

    OpenCvWebcam webcam1 = null;
    int position; //0 = left, 1 = middle, 2 = right
    private RoboController roboController;

    @Override
    public void runOpMode() {
        roboController = new RoboController(this);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam1 = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        //WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        //webcam1 = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        webcam1.setPipeline(new examplePipeline());
        webcam1.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            public void onOpened() {
                webcam1.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }//720

            public void onError(int errorCode) {
            }
        });
        waitForStart();
        if (opModeIsActive()) {
            telemetry.addData("positon", position);

            if (position == 0) {
                roboController.moveOnYAxis(RoboController.inchesToCounts(18));
                roboController.Spin(RoboController.inchesToCounts(-12));
                roboController.ClawR.setPosition(0.4);
                sleep(5000);
                roboController.moveOnYAxis(RoboController.inchesToCounts(-42));

            } else if (position == 1) {
                roboController.moveOnYAxis(RoboController.inchesToCounts(20));
                roboController.ClawL.setPosition(0.5);
                roboController.moveOnXAxis(RoboController.inchesToCounts(36));
                roboController.Spin(RoboController.inchesToCounts(12));

            } else if (position == 2) {
                roboController.moveOnYAxis(RoboController.inchesToCounts(50));
                roboController.Spin(RoboController.inchesToCounts(12));
                roboController.ClawR.setPosition(0.4);
                sleep(5000);
                roboController.moveOnYAxis(RoboController.inchesToCounts(36));

            }
            telemetry.update();
            stop();
        }
    }

    class examplePipeline extends OpenCvPipeline{
        Mat YCbCr = new Mat();
        Mat leftcrop;
        Mat rightcrop;
        Mat middlecrop;
        double leftavgfin;
        double middleavgfin;
        double rightavgfin;
        Mat outPut = new Mat();
        Scalar rectColor = new Scalar(255.0,0,0,0);

        public Mat processFrame(Mat input){
            Imgproc.cvtColor(input, YCbCr, Imgproc.COLOR_RGB2YCrCb);
            //telemetry.addLine("pipline running");

            Rect leftRect = new Rect(1,1,3,3);
            Rect rightRect = new Rect(5,1,3,3);
            Rect middleRect = new Rect(10,1,3,3);

            input.copyTo(outPut);
            Imgproc.rectangle(outPut, leftRect, rectColor, 2);
            Imgproc.rectangle(outPut, middleRect, rectColor, 2);
            Imgproc.rectangle(outPut, rightRect, rectColor, 2);

            leftcrop = YCbCr.submat(leftRect);
            rightcrop = YCbCr.submat(rightRect);
            middlecrop = YCbCr.submat(middleRect);

            Core.extractChannel(leftcrop,leftcrop,2);
            Core.extractChannel(middlecrop,leftcrop,2);
            Core.extractChannel(rightcrop,rightcrop,2);

            Scalar leftavg = Core.mean(leftcrop);
            Scalar rightavg = Core.mean(rightcrop);
            Scalar middleavg = Core.mean(middlecrop);

            leftavgfin = leftavg.val[0];
            middleavgfin = middleavg.val[0];
            rightavgfin = rightavg.val[0];


            if(leftavgfin > rightavgfin && leftavgfin > middleavgfin){
                //position = 0;
                telemetry.addLine("Left");
            }
            else if(rightavgfin > leftavgfin && rightavgfin > middleavgfin){
                //position = 2;
                telemetry.addLine("right");
            }
            else if(middleavgfin > rightavgfin && middleavgfin > leftavgfin){
                //position = 1;
                telemetry.addLine("mid");
            }
            else{
                    telemetry.addLine("2");
            }
            position = 1;
            telemetry.addData("position", position);
            telemetry.addData("left", leftavgfin);
            telemetry.addData("mid", middleavgfin);
            telemetry.addData("right", rightavgfin);
            telemetry.update();
            return(outPut);
        }
    }
}
