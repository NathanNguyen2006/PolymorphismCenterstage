// https://github.com/KookyBotz/PowerPlaySleeveDetection/tree/main

package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SolidColorSignalPipeline extends OpenCvPipeline {
    /*
    YELLOW  = Parking Left
    CYAN    = Parking Middle
    MAGENTA = Parking Right
     */

    private Point sleeve_pointA, sleeve_pointB;

    // Width and height for the bounding box
    public static int REGION_WIDTH = 75;
    public static int REGION_HEIGHT = 75;

    // TOPLEFT anchor points for the bounding box
    private static Point SLEEVE_TOPLEFT_ANCHOR_POINT_LEFT = new Point(275, 168);
    private static Point SLEEVE_TOPLEFT_ANCHOR_POINT_RIGHT = new Point(800-REGION_WIDTH * 1.5, 168);

    public SolidColorSignalPipeline(Direction direction){
        super();

        Point anchorPoint = SLEEVE_TOPLEFT_ANCHOR_POINT_LEFT;
        if (direction == Direction.Left) {
            anchorPoint = SLEEVE_TOPLEFT_ANCHOR_POINT_LEFT;
        } else if (direction == Direction.Right) {
            anchorPoint = SLEEVE_TOPLEFT_ANCHOR_POINT_RIGHT;
        }

         // Anchor point definitions
            sleeve_pointA = new Point(
                    anchorPoint.x,
                    anchorPoint.y);
            sleeve_pointB = new Point(
                    anchorPoint.x + REGION_WIDTH,
                    anchorPoint.y + REGION_HEIGHT);
    }

    // Color definitions
    private final Scalar
            YELLOW  = new Scalar(255, 255, 0),
            CYAN    = new Scalar(0, 255, 255),
            MAGENTA = new Scalar(255, 0, 255);

    // Running variable storing the parking position
    private volatile Signal signal = null;

    @Override
    public Mat processFrame(Mat input) {
        // Get the submat frame, and then sum all the values
        Mat areaMat = input.submat(new Rect(sleeve_pointA, sleeve_pointB));
        Scalar sumColors = Core.sumElems(areaMat);

        // Get the minimum RGB value from every single channel
        double minColor = Math.min(sumColors.val[0], Math.min(sumColors.val[1], sumColors.val[2]));

        // Change the bounding box color based on the sleeve color
        if (sumColors.val[0] == minColor) {
            signal = Signal.Two;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    CYAN,
                    2
            );
        } else if (sumColors.val[1] == minColor) {
            signal = Signal.Three;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    MAGENTA,
                    2
            );
        } else {
            signal = Signal.One;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    YELLOW,
                    2
            );
        }

        // Release and return input
        areaMat.release();
        return input;
    }

    // Returns an enum being the current position where the robot will park
    public Signal getSignal() {
        return signal;
    }
}
