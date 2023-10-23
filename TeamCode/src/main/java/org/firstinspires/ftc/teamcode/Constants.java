package org.firstinspires.ftc.teamcode;

public class Constants {
    // https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/
    public static final double WHEEL_MOTOR_COUNTS_PER_ROTATION = ((((1d+(46d/17d))) * (1d+(46d/11d))) * 28d);
    // https://www.gobilda.com/96mm-mecanum-wheel-set-70a-durometer-bearing-supported-rollers/
    // 96 mm
    public static final double WHEEL_DIAMETER_IN_INCHES = 3.779528d;
    // Game Manual Part 2, Section 4.4: Game Definitions
    // Overshoot it a little
    public static final double TILE_LENGTH_IN_INCHES = 24;
}
