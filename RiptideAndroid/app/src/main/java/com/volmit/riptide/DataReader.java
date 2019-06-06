package com.volmit.riptide;

import android.graphics.Point;
import android.os.Bundle;

import com.fdev.backgroundchart.GradientChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import riptide.data.DataType;
import riptide.device.Device;
import riptide.device.Sensor;
import riptide.net.Riptide;
import riptide.stream.WitholdingDataStream;
import spork.Spork;
import spork.android.BindView;

public class DataReader extends AppCompatActivity {

    @BindView(R.id.chart)
    private GradientChart chart;

    @BindView(R.id.dat)
    private TextView data;

    @BindView(R.id.card1)
    private CardView card1;

    private ExecutorService svc;
    private long last;
    private Handler handler;
    private boolean active;
    private Float[] floats;
    private int size = 128;
    private WitholdingDataStream<?> stream;
    private boolean mod;
    private float current = 1;
    private float multiplier = 1;
    private float min = Float.MAX_VALUE;
    private float max = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(isDark() ? R.style.Dark : R.style.Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_reader);
        Spork.bind(this);
        svc = Executors.newSingleThreadExecutor();
        handler = new Handler();
        active = true;
        floats = new Float[size];
        chart.setChartValues(floats);
        chart.setVisibility(View.GONE);
        last = System.currentTimeMillis();
        multiplier = 1;

        if(isDark())
        {
            card1.setBackgroundResource(R.drawable.noled_bg_bottom);
        }

        else
        {
            chart.setPlusColorStart(getResources().getColor(R.color.colorWhite));
            chart.setPlusColorEnd(getResources().getColor(R.color.colorWhite));
            chart.setMinusColorStart(getResources().getColor(R.color.colorBlack));
            chart.setMinusColorEnd(getResources().getColor(R.color.colorWhite));
            chart.setChartLineColor(getResources().getColor(R.color.colorWhite));
        }

        chart.setScaleX(1.2F);
    }

    public void onPause()
    {
        active = false;

        try
        {
            stream.close();
        }

        catch(Throwable e)
        {

        }

        super.onPause();
    }

    public void onResume()
    {
        active = true;
        super.onResume();
        svc.submit(new Runnable() {
            @Override
            public void run() {
                try
                {
                    String deviceName = getIntent().getStringExtra("device");
                    String sensorName = getIntent().getStringExtra("sensor");
                    Device device = Riptide.findDevice(deviceName);
                    Sensor sensor = device.getSensor(sensorName);
                    getSupportActionBar().setTitle(deviceName + " / " + sensorName);

                    switch(sensor.getDataType())
                    {
                        case FLOAT:
                            handleFloat(Riptide.stream(device, sensor));
                            break;
                        case DOUBLE:
                            handleDouble(Riptide.stream(device, sensor));
                            break;
                        case UTF:
                            handleUTF(Riptide.stream(device, sensor));
                            break;
                    }
                }

                catch(Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleUTF(WitholdingDataStream<String> stream) {
        this.stream = stream;
        stream.getConveyorBelt().setSize(size);
    }

    private void handleDouble(WitholdingDataStream<Double> stream) {
        this.stream = stream;
        stream.getConveyorBelt().setSize(size);
        setupChart();
    }

    private void handleFloat(WitholdingDataStream<Float> stream) {
        this.stream = stream;
        stream.getConveyorBelt().setSize(size);
        setupChart();
    }

    private void setupChart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                chart.setVisibility(View.VISIBLE);
                Arrays.fill(floats, 1f);
                renderChart();
            }
        });

        try
        {
            while(active)
            {
                if(stream.getSensor().getDataType().equals(DataType.DOUBLE))
                {
                    ((WitholdingDataStream<Double>)stream).getConveyorBelt().push(((WitholdingDataStream<Double>)stream).pull());
                }

                else
                {
                    ((WitholdingDataStream<Float>)stream).getConveyorBelt().push(((WitholdingDataStream<Float>)stream).pull());
                }

                mod = true;
            }
        }

        catch(Throwable e)
        {

        }
    }

    private void renderChart() {
        if(active)
        {
            min = Float.MAX_VALUE;
            max = Float.MIN_VALUE;
            if(mod)
            {
                mod = false;
                try
                {
                    if(stream.getSensor().getDataType().equals(DataType.DOUBLE))
                    {
                        for(int i = Math.min(size, stream.getConveyorBelt().getOccupancy()) - 1; i >= 0; i--)
                        {
                            Double d = ((WitholdingDataStream<Double>)stream).getConveyorBelt().getData().get(i);
                            if(d != null) {
                                floats[i] = d.floatValue();
                            }
                        }
                    }

                    else
                    {
                        for(int i = Math.min(size, stream.getConveyorBelt().getOccupancy()) - 1; i >= 0; i--)
                        {
                            Float f = ((WitholdingDataStream<Float>)stream).getConveyorBelt().getData().get(i);
                            if(f != null) {
                                floats[i] = f;
                            }
                        }
                    }

                    current = floats[floats.length - 1];

                    for(Float i : floats)
                    {
                        if(i < min)
                        {
                            min = i;
                        }

                        if(i > max)
                        {
                            max = i;
                        }
                    }

                    float actualHeight = chart.getHeight();
                    float maxVel = (actualHeight / 2F);
                    float range = Math.abs(max - min);

                    multiplier = maxVel / (range == 0 ? 1 : range);

                    for(int i = 0; i < floats.length; i++)
                    {
                        floats[i] = -(floats[i] * (multiplier));
                    }
                }

                catch(Throwable e)
                {
                    e.printStackTrace();
                }
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    renderChart();
                    data.setText("MIN: " + min + "\nMAX: " + max + "\nCurrent: " + current + "\nInterval: " + (System.currentTimeMillis() - last) + "\nMultiplier: " + multiplier);
                    last = System.currentTimeMillis();
                    chart.setZoom(1);
                    chart.setChartValues(floats);
                    chart.requestLayout();
                }
            }, 50);
        }
    }

    public boolean isDark()
    {
        return getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark", false);
    }
}
