package bzha2709.comp5216.sydney.edu.au.runningdiary.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import bzha2709.comp5216.sydney.edu.au.runningdiary.simplepedometer.SimpleStepDetector;
import bzha2709.comp5216.sydney.edu.au.runningdiary.simplepedometer.StepListener;

/**
 * Created by Bingqing ZHAO on 2017/10/5.
 */

public class MyStepListener implements StepListener,SensorEventListener
{
    private SimpleStepDetector simpleStepDetector;
    private int numSteps;

    public MyStepListener(SimpleStepDetector s)
    {
        super();
        simpleStepDetector=s;
        numSteps=0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
    }

    public int getSteps()
    {return numSteps;}
}
