package bzha2709.comp5216.sydney.edu.au.runningdiary.tools;

import android.os.Handler;
import android.os.Message;

import bzha2709.comp5216.sydney.edu.au.runningdiary.Stats;

/**
 * Created by Administrator on 2017/10/15.
 */

public class CounterThread implements Runnable
{
    Handler handler;
    Stats stats;
    int time_count=0;

    public CounterThread(Handler h, Stats stats)
    {
        handler=h;
        this.stats=stats;
    }

    @Override
    public void run()
    {
        time_count++;
        if(null!=stats) stats.updateTimeTrack(time_count);
        handler.postDelayed(this, 1000);
    }
}
