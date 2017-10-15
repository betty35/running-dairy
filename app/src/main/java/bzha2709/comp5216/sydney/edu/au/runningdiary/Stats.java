package bzha2709.comp5216.sydney.edu.au.runningdiary;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.TrackPoint;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.DateUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class Stats extends android.support.v4.app.Fragment {

    private MainActivity ma;
    private List<TrackPoint> tpl;
    private TextView date;
    private Date selectedTime;
    private Date endOfTheDay;
    int year1,month1,day1;
    private LineChart lineChart;
    private LineDataSet spdLDS;
    private LineData spdLD;
    Button check;
    TextView avg_spd;
    TextView step_count;
    TextView time_track;

    public Stats() {
        // Required empty public constructor
    }

    public void ini(MainActivity m)
    {
        ma=m;
        tpl=ma.getPeriodsOfDataFromDB(DateUtil.getThisMorning(),DateUtil.getTodayEvening());
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_stats, container, false);
        step_count=(TextView)view.findViewById(R.id.stat_step_count);
        time_track=(TextView)view.findViewById(R.id.stat_time_track);
        avg_spd=(TextView)view.findViewById(R.id.stat_avg_spd);
        check=(Button)view.findViewById(R.id.stat_check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHistory(view);
            }
        });
        lineChart=(LineChart)view.findViewById(R.id.stat_spd_line);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelRotationAngle(90);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long)value));
            }
        });
        date=(TextView) view.findViewById(R.id.stat_date);
        selectedTime=DateUtil.getThisMorning();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date.setText(sdf.format(selectedTime));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showDialogPick((TextView) v);}
        });
        updateSpdLineChart(lineChart);
        return view;
    }


    public void updateStepCount(int steps)
    {
        if(null!=step_count)
        step_count.setText(""+steps);
    }

    public void updateTimeTrack(int timeSecond)
    {
        int s=timeSecond;
        int h=s/3600;
        s=s-h*3600;
        int m=s/60;
        s=s-m*60;
        String second;
        String minute;
        String hour;
        if(s<10) second="0"+s;
        else second=""+s;
        if(m<10) minute="0"+m;
        else minute=""+m;
        if(h<10) hour="0"+h;
        else hour=""+h;
        String time=hour+":"+minute+":"+second;
        time_track.setText(time);
    }

    private void showDialogPick(final TextView timeText) {
        final StringBuffer time = new StringBuffer();
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                time.append(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                year1=year; month1=monthOfYear; day1=dayOfMonth;
                timeText.setText(time.toString());
            }
        }, year, month, day);
        datePickerDialog.show();

    }


    public void searchHistory(View view)
    {
        selectedTime=DateUtil.getMorningTimeOfDay(year1,month1,day1);
        endOfTheDay=DateUtil.getEveningTimeOfDay(year1,month1,day1);
        tpl= ma.getPeriodsOfDataFromDB(selectedTime,endOfTheDay);
        updateSpdLineChart(lineChart);
        ma.lines.get(2).setPoints(ma.convertToLatLng(tpl));
    }

    public void updateSpdLineChart(LineChart chart)
    {
        List<Entry> spd=new ArrayList<Entry>();
        if(null==tpl) return;
        for(int i=0;i<tpl.size();i++)
        {
            TrackPoint t=tpl.get(i);
            spd.add(new Entry(t.getTime().getTime(),t.getSpeed()));
        }
        Toast.makeText(getActivity(),"spd:"+spd.size(),Toast.LENGTH_SHORT).show();
        if(chart.getData()!=null) chart.clear();
        if(tpl.size()>0)
        {
            if(spdLDS!=null) {spdLDS.clear();spdLDS=null;}
            spdLDS=new LineDataSet(spd,"speed");
            spdLDS.setColor(getColor(R.color.colorPrimary));
        }
        if(spdLD!=null) {spdLD.clearValues();}
        else spdLD=new LineData();
        spdLD.addDataSet(spdLDS);
        chart.setData(spdLD);
        chart.invalidate();
        DecimalFormat df = new DecimalFormat("0.00");
        avg_spd.setText(df.format(avgSpeed())+"m/s");
        XAxis xAxis = lineChart.getXAxis();
        if(tpl.size()>0)
        {
            long time1=tpl.get(0).getTime().getTime();
            long time2=tpl.get(tpl.size() - 1).getTime().getTime();
            long timeB;
            long timeS;
            if(time1>time2)
            {
                timeB=time1;
                timeS=time2;
            }
            else
            {
                timeB=time2;
                timeS=time1;
            }
            xAxis.setAxisMaximum(timeB);
            xAxis.setAxisMinimum(timeS);
        }
    }


    public int getColor(int Rid)
    {
        return ContextCompat.getColor(getActivity(), Rid);
    }

    public float avgSpeed()
    {
        float sum=0;
        if(null!=tpl)
        {
            if(tpl.size()==0) return 0f;
            else
            {
                for(int i=0;i<tpl.size();i++)
                    sum+=tpl.get(i).getSpeed();
                return  sum/tpl.size();
            }
        }
        else return 0f;
    }
}
