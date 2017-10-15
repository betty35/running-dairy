package bzha2709.comp5216.sydney.edu.au.runningdiary.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.Toast;

import bzha2709.comp5216.sydney.edu.au.runningdiary.MainActivity;
import bzha2709.comp5216.sydney.edu.au.runningdiary.Stats;
import bzha2709.comp5216.sydney.edu.au.runningdiary.simplepedometer.SimpleStepDetector;
import bzha2709.comp5216.sydney.edu.au.runningdiary.simplepedometer.StepListener;

/**
 * Created by Bingqing ZHAO on 2017/10/5.
 */

public class MyStepListener implements StepListener,SensorEventListener
{
    private SimpleStepDetector simpleStepDetector;
    private int numSteps;
    MainActivity m1;
    Stats stats;

    public MyStepListener(SimpleStepDetector s, MainActivity m,Stats s2)
    {
        super();
        simpleStepDetector=s;
        numSteps=0;
        m1=m;
        stats=s2;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long timeNs= simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
            if(timeNs!=0) step(timeNs);
        }
    }

    @Override
    public void step(long timeNs)
    {
        if(m1.started) {
            numSteps++;
            m1.numSteps = numSteps;
            stats.updateStepCount(numSteps);
        }
    }

    public void setNumSteps(int steps)
    {numSteps=steps;}

    public int getNumSteps()
    {
        return numSteps;
    }

}
