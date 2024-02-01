//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//
//import org.firstinspires.ftc.robotcore.external.ClassFactory;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
//import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
//import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
//
//import java.util.List;
//
///**
// * This 2022-2023 OpMode illustrates the basics of using the TensorFlow Object Detection API to
// * determine which image is being presented to the robot.
// *
// * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
// * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
// *
// * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
// * is explained below.
// */
//@Autonomous(name = "Autonomous Mode RIGHT", group = "Concept")
////@Disabled
//public class AutoModeRight extends LinearOpMode {
//    private RoboController roboController;
//
//    /*
//     * Specify the source for the Tensor Flow Model.
//     * If the TensorFlowLite object model is included in the Robot Controller App as an "asset",
//     * the OpMode must to load it using loadModelFromAsset().  However, if a team generated model
//     * has been downloaded to the Robot Controller's SD FLASH memory, it must to be loaded using loadModelFromFile()
//     * Here we assume it's an Asset.    Also see method initTfod() below .
//     */
//    private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
//    // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";
//
//
//    private static final String[] LABELS = {
//            "1 Bolt",
//            "2 Bulb",
//            "3 Panel"
//    };
//
//    private static final int speed = 1000;
//
//    /*
//     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
//     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
//     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
//     * web site at https://developer.vuforia.com/license-manager.
//     *
//     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
//     * random data. As an example, here is a example of a fragment of a valid key:
//     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
//     * Once you've obtained a license key, copy the string from the Vuforia web site
//     * and paste it in to your code on the next line, between the double quotes.
//     */
//    private static final String VUFORIA_KEY =
//        "AXzbuNH/////AAABmRqZd9XnO0CEsO6Tatx8KGWKRdtvODyqwzUdXMVNDuLj7GcclE21CF6Be/sz9T9X2G6kiHGvKpI68NAmSd5B9RcCSUKx+K2SeOSt4gDyLDkxuH1IN6bS7l+WKHWQvmvK8w1wb5Z0iGkXcOBJMjafhhazwYrvJrUT8h4gBfhvbeRlyUpyMadRR9ISVUhYDg4byn0xwpbjzbjECxg5rIieQ8WzMspNm+xYQHhGxnSFjJ0Gr0Lv2AM6j0eoxNm8M9PhovFps1WR0RL/FWzvd56I5Scok+qAIw0cRQFQDenAgutXhZjdi8EAlnWY7SFS5OxFUG3oHlzCmOd4/pb0IbA+jqNSQQxWWNa6Cj9rYHNnG0I2";
//
//    /**
//     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
//     * localization engine.
//     */
//    private VuforiaLocalizer vuforia;
//
//    /**
//     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
//     * Detection engine.
//     */
//    private TFObjectDetector tfod;
//
//    @Override
//    public void runOpMode() {
//        roboController = new RoboController(hardwareMap);
//
//        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
//        // first.
//        initVuforia();
//        initTfod();
//
//        /**
//         * Activate TensorFlow Object Detection before we wait for the start command.
//         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
//         **/
//        if (tfod != null) {
//            tfod.activate();
//
//            // The TensorFlow software will scale the input images from the camera to a lower resolution.
//            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
//            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
//            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
//            // should be set to the value of the images used to create the TensorFlow Object Detection model
//            // (typically 16/9).
//            tfod.setZoom(1.0, 16.0/9.0);
//        }
//
//        /** Wait for the game to begin */
//        telemetry.addData(">", "Press Play to start op mode");
//        telemetry.update();
//        waitForStart();
//        roboController.setHand(true);
//        //roboController.ClawR.setPosition(0.4);
//        sleep(100);
//        if (opModeIsActive()) {
//            sleep(3000);
//            String face = LABELS[0];
//            for (int i = 0; i < 5; i++) {
//                String newFace = getConeFace();
//                if (newFace != null) {
//                    face = newFace;
//                    break;
//                }
//                else sleep(2000);
//            }
//            moveTo(face);
//        }
//    }
//
//    public static int inchesToCounts(double inchesToDrive) {
//        double circumference = Constants.WHEEL_DIAMETER_IN_INCHES * Math.PI;
//        double rotations = inchesToDrive / circumference;
//        double countsToDrive = rotations * Constants.WHEEL_MOTOR_COUNTS_PER_ROTATION;
//        return (int) countsToDrive;
//    }
//
//    /** `face` should be one of the three labels in the `LABELS` array. */
//    private void moveTo(String face) {
//        if (face.equals("1 Bolt")) {
//            moveOnXAxis(inchesToCounts(18.5)); //from leftmost position
//            moveOnYAxis(inchesToCounts(5)); //distance to cone
//            roboController.setHand(false);
//            moveOnYAxis(inchesToCounts(-5));
//            moveOnXAxis(inchesToCounts((Constants.TILE_LENGTH_IN_INCHES)/2.0));
//            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
//        }
//        else if (face.equals("2 Bulb")) {
//            moveOnXAxis(inchesToCounts(18.5)); //from leftmost position
//            moveOnYAxis(inchesToCounts(5)); //distance to cone
//            roboController.setHand(false);
//            moveOnYAxis(inchesToCounts(-5));
//            moveOnXAxis(inchesToCounts((Constants.TILE_LENGTH_IN_INCHES)/2.0));
//            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
//            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
//            moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
//            moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES/9.0));
//
////            moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
////            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
////            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
////            moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
////            moveOnXAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES/9.0));
//        }
//        else if (face.equals("3 Panel")) {
//            moveOnXAxis(inchesToCounts(18.5)); //from leftmost position
//            moveOnYAxis(inchesToCounts(5)); //distance to cone
//            roboController.setHand(false);
//            moveOnYAxis(inchesToCounts(-5));
//            moveOnXAxis(inchesToCounts((Constants.TILE_LENGTH_IN_INCHES)/2.0));
//            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
//            moveOnYAxis(inchesToCounts(Constants.TILE_LENGTH_IN_INCHES));
//            moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
//            moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES));
//            moveOnXAxis(inchesToCounts(-Constants.TILE_LENGTH_IN_INCHES/9.0));
//        }
//    }
//
//    public void moveOnXAxis(int ticks) {
//        DcMotorEx frontLeft = roboController.FLW,
//            frontRight = roboController.FRW,
//            rearLeft = roboController.BLW,
//            rearRight = roboController.BRW;
//
//        // Reset encoders
//        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        frontLeft.setTargetPosition(ticks);
//        rearLeft.setTargetPosition(-ticks);
//
//        frontRight.setTargetPosition(-ticks);
//        rearRight.setTargetPosition(ticks);
//
//        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        frontLeft.setVelocity(-speed);
//        rearLeft.setVelocity(speed);
//
//        frontRight.setVelocity(speed);
//        rearRight.setVelocity(-speed);
//
//        while (opModeIsActive() && frontLeft.isBusy()) {
//          // Loop until the motor reaches its target position.
//          telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
//          telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
//          telemetry.addData("Rear Left Encoder", rearLeft.getCurrentPosition());
//          telemetry.addData("Rear Right Encoder", rearRight.getCurrentPosition());
//          telemetry.update();
//        }
//
//        sleep(250);
//    }
//
//    public void moveOnYAxis(int ticks) {
//        DcMotorEx frontLeft = roboController.FLW,
//            frontRight = roboController.FRW,
//            rearLeft = roboController.BLW,
//            rearRight = roboController.BRW;
//
//        // Reset encoders
//        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        // Set position
//        frontLeft.setTargetPosition(ticks);
//        frontRight.setTargetPosition(ticks);
//        rearLeft.setTargetPosition(ticks);
//        rearRight.setTargetPosition(ticks);
//
//        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        frontLeft.setVelocity(speed);
//        frontRight.setVelocity(speed);
//        rearLeft.setVelocity(speed);
//        rearRight.setVelocity(speed);
//
//        while (opModeIsActive() && frontLeft.isBusy()) {
//          // Loop until the motor reaches its target position.
//          telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
//          telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
//          telemetry.addData("Rear Left Encoder", rearLeft.getCurrentPosition());
//          telemetry.addData("Rear Right Encoder", rearRight.getCurrentPosition());
//          telemetry.update();
//        }
//
//        sleep(250);
//    }
//
//    private String getConeFace() {
//        // getUpdatedRecognitions() will return null if no new information is available since
//        // the last time that call was made.
//        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
//        if (updatedRecognitions != null && updatedRecognitions.size() > 0) {
//            return updatedRecognitions.get(0).getLabel();
//        } else return null;
//    }
//
//    /**
//     * Initialize the Vuforia localization engine.
//     */
//    private void initVuforia() {
//        /*
//         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
//         */
//        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
//
//        parameters.vuforiaLicenseKey = VUFORIA_KEY;
//        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
//
//        //  Instantiate the Vuforia engine
//        vuforia = ClassFactory.getInstance().createVuforia(parameters);
//    }
//
//    /**
//     * Initialize the TensorFlow Object Detection engine.
//     */
//    private void init() {
//        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
//            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
//        tfodParameters.minResultConfidence = 0.75f;
//        tfodParameters.isModelTensorFlow2 = true;
//        tfodParameters.inputSize = 300;
//        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
//
//        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
//        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
//        tfod.loadModelFromAsset(TFOD_MOTfodDEL_ASSET, LABELS);
//        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
//    }
//}