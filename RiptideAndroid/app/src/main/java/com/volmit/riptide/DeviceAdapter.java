package com.volmit.riptide;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nex3z.flowlayout.FlowLayout;

import java.util.List;

import riptide.device.Device;
import riptide.device.Emitter;
import riptide.device.Sensor;
import riptide.net.Riptide;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{

    private MainActivity main;
    private List<Device> devices;
    private boolean dark;

    public DeviceAdapter(boolean dark, MainActivity main)
    {
        this.main = main;
        devices = Riptide.getDevices();
        this.dark = dark;
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.device_card, parent, false);
        if(dark)
        {
            v.setBackgroundResource(R.drawable.noled_bg_bottom);
        }

        return new DeviceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device d = devices.get(position);
        holder.tn.setText(d.getName());
        holder.td.setText(d.getSensors().size() + " Sensor" + (d.getSensors().size() == 1 ? "" : "s") + ", " + d.getEmitters().size() + " Emitter" + (d.getEmitters().size() == 1 ? "" : "s"));
        holder.grid.removeAllViews();

        if(dark)
        {
            holder.icon.setImageTintList(ColorStateList.valueOf(holder.cv.getContext().getResources().getColor(R.color.colorWhite)));
        }

        for(Sensor i : d.getSensors())
        {
            LinearLayout v = (LinearLayout) LayoutInflater.from(holder.cv.getContext()).inflate(R.layout.sensor_list_item, holder.grid, false);
            TextView name = v.findViewById(R.id.name);

            if(dark)
            {
                v.setBackgroundResource(R.drawable.bubble_sensor_dark);
                ImageView icon = v.findViewById(R.id.icon);
                name.setTextColor(holder.cv.getContext().getResources().getColor(R.color.sensorDarkestColor));
                icon.setImageTintList(ColorStateList.valueOf(holder.cv.getContext().getResources().getColor(R.color.sensorDarkestColor)));
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DataReader.class);
                    intent.putExtra("device", d.getName());
                    intent.putExtra("sensor", i.getName());
                    v.getContext().startActivity(intent);
                }
            });

            name.setText(i.getName());
            holder.grid.addView(v);
        }

        for(Emitter i : d.getEmitters())
        {
            LinearLayout v = (LinearLayout) LayoutInflater.from(holder.cv.getContext()).inflate(R.layout.emitter_list_item, holder.grid, false);
            TextView name = v.findViewById(R.id.name);

            if(dark)
            {
                v.setBackgroundResource(R.drawable.bubble_emitter_dark);
                ImageView icon = v.findViewById(R.id.icon);
                name.setTextColor(holder.cv.getContext().getResources().getColor(R.color.emitterDarkestColor));
                icon.setImageTintList(ColorStateList.valueOf(holder.cv.getContext().getResources().getColor(R.color.emitterDarkestColor)));
            }

            name.setText(i.getName());
            holder.grid.addView(v);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tn;
        TextView td;
        ImageView icon;
        FlowLayout grid;

        public DeviceViewHolder(CardView itemView) {
            super(itemView);
            cv = (CardView)itemView;
            tn = cv.findViewById(R.id.device_name);
            td = cv.findViewById(R.id.device_description);
            grid = cv.findViewById(R.id.elements);
            icon = cv.findViewById(R.id.icon);
        }
    }
 
}