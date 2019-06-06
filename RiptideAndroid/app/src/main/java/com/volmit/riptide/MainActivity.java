package com.volmit.riptide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import riptide.device.Device;
import riptide.device.Sensor;
import riptide.net.LMap;
import riptide.net.MyOwnDamnConsumer;
import riptide.net.Riptide;
import riptide.net.RiptideClient;
import riptide.queue.DataRoller;
import spork.Spork;
import spork.android.BindView;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.progress_frame)
    private FrameLayout progressFrame;

    @BindView(R.id.progress_bar)
    private ProgressBar progressBar;

    @BindView(R.id.progress_text)
    private TextView progressText;

    @BindView(R.id.recycler)
    private RecyclerView recycler;
    private Map<String, String> lan;
    private LinearLayoutManager lmg;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(isDark() ? R.style.Dark : R.style.Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spork.bind(this);
        refresh();
    }

    public void refresh()
    {
        final Handler h = new Handler();
        ExecutorService svc = Executors.newSingleThreadExecutor();
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(progressFrame);
        progressBar.setProgress(0);
        progressText.setText("Mapping LAN");

        svc.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Riptide.refreshDevices(new MyOwnDamnConsumer<Double>() {
                        @Override
                        public void accept(Double aDouble) {
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(aDouble < 0D)
                                    {
                                        progressText.setText("Contacting Devices");
                                        progressBar.setIndeterminate(true);
                                    }

                                    else
                                    {
                                        progressBar.setProgress((int) (aDouble * 1000D));
                                    }
                                }
                            });
                        }
                    }, 128, 333);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.FadeOut).duration(250).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {

                            }
                        }).playOn(progressFrame);
                        refreshLayout();
                    }
                });
            }
        });
    }

    private void refreshLayout() {
        lmg = new LinearLayoutManager(this);
        adapter = new DeviceAdapter(isDark(), this);
        recycler.setLayoutManager(lmg);
        recycler.setAdapter(adapter);

        YoYo.with(Techniques.FadeInUp).duration(350).playOn(recycler);

        if(adapter.getItemCount() == 0)
        {
            Toast.makeText(this, "No Devices Found", Toast.LENGTH_SHORT).show();
        }

        else
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menu.getItem(0).setIcon(isDark() ? R.drawable.moon : R.drawable.sun);
        return true;
    }

    public void toggleDark()
    {
        getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("dark", !isDark()).commit();
    }

    public boolean isDark()
    {
        return getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark", false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.theme)
        {
            toggleDark();
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
