package org.firstinspires.ftc.teamcode;


public class Pid {
   public double proportionalGain;
    public double integralGain;
    public double derivativeGain;
    private double valueLast;
    private boolean derivativeInitialized;
    private double integrationStored;
    public double integralSaturation;
    public double outputMin; 
    public double outputMax; 
    public Pid(  double proportionalGain, double integralGain, double derivativeGain, double integralSaturation, double outputMin, double outputMax){
        this.proportionalGain = proportionalGain;
        this.integralGain = integralGain;
        this.derivativeGain = derivativeGain;
        this.integralSaturation = integralSaturation;
        this.outputMin = outputMin;
        this.outputMax = outputMax; 

    }
    public double Update(double dt, double currentValue, double targetValue) {
        //TODO: implement this
        double error = targetValue - currentValue;
        double P = proportionalGain * error;
        double valueRateOfChange = 0;
        
        if (derivativeInitialized){
            valueRateOfChange = (currentValue - valueLast) / dt;
            valueLast = currentValue;
        }
        else {
            derivativeInitialized = true;
        }
        double D = derivativeGain * -valueRateOfChange; //note the negative sign here
        
        integrationStored = integrationStored + (error * dt);
        double I = integralGain * integrationStored; 
        integrationStored = this.Clamp(integrationStored + (error * dt), -integralSaturation, integralSaturation);
        double result = P + I + D;
        return this.Clamp(result, outputMin, outputMax);
    }
    public void Reset() {
        derivativeInitialized = false;
    }   
    static public double Clamp(double min, double max, double value){
        double clampedValue = Math.max(min, Math.min(max, value));
        return clampedValue;
    }
}
