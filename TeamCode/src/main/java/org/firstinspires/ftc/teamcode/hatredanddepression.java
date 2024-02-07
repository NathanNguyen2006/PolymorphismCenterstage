package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.internal.camera.WebcamExample;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Mat;

@Autonomous
public class hatredanddepression extends OpMode {

    OpenCvWebcam webcam1 = null;
    int position; //0 = left, 1 = middle, 2 = right
    private RoboController roboController;

    @Override
    public void init() {
        //roboController = new RoboController(this);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam1 = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        //WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        //webcam1 = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        webcam1.setPipeline(new examplePipeline());
        webcam1.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            public void onOpened() {
                webcam1.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            public void onError(int errorCode) {
            }
        });
    }



    @Override
    public void loop() {
        telemetry.addLine("xsdsdsdD");
        telemetry.update();
        if(position == 0){

        }
        else if(position == 1){

        }
        else if (position == 2){

        }
        stop();
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

            Rect leftRect = new Rect(1,1,266,447);
            Rect rightRect = new Rect(267,1,266,447);
            Rect middleRect = new Rect(533,1,266,447);
            
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
                position = 0;
                telemetry.addLine("Left");
            }
            else if(rightavgfin > leftavgfin && rightavgfin > middleavgfin){
                position = 3;
                telemetry.addLine("right");
            }
            else if(middleavgfin > rightavgfin && middleavgfin > leftavgfin){
                position = 2;
                telemetry.addLine("mid");
            }
            else{
                    telemetry.addLine("2");
            }
            telemetry.addData("position", position);
            telemetry.addData("left", leftavgfin);
            telemetry.addData("mid", middleavgfin);
            telemetry.addData("right", rightavgfin);
            telemetry.update();
            return(outPut);
        }
    }
}
