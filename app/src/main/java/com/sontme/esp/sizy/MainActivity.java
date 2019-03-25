package com.sontme.esp.sizy;

import android.app.slice.Slice;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {


    Map<Integer, String> hashmap = new HashMap<Integer, String>();
    //File fileee = new File("/");
    File fileee = new File(Environment.getRootDirectory().getAbsolutePath());
    File lastpath2 = fileee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File x = new File(lastpath2.getParent());
                if (x.isDirectory() && (x.getPath() != "/" || x.getPath() != "/system")) {
                    drawChart(x);
                    hashmap.clear();
                } else {
                    showToast("IS ROOT DIRECTORY");
                }


            }
        });

        try {
            drawChart(fileee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawChart(final File dir) {
        if (dir.getPath() != "/" || dir.getPath() != "/system") {
            Log.d("_FONTOS_", "EZT RAJZOLOM MOST:_" + dir.getPath());

            lastpath2 = dir;

            File[] files = dir.listFiles();
            recursiveLoop(files);
            final Map<Integer, String> sorted_hashmap = new TreeMap<>(hashmap);

            hashmap.clear();

            Random rnd = new Random();
            PieChartView chart = findViewById(R.id.chart);

            List<SliceValue> pieData = new ArrayList<>();
            chart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int arcIndex, SliceValue value) {

                    hashmap.clear();
                    sorted_hashmap.clear();

                    String a = String.valueOf(value.getLabel());
                    String[] s = a.split(" ");
                    Log.d("_FONTOS_", "EZT FOGOM RAJZOLNI:_" + fileee.getAbsolutePath());
                    File x = new File(lastpath2 + "/" + s[0]);
                    if (x.isDirectory() && (x.getPath() != "/" || x.getPath() != "/system")) {
                        drawChart(x);
                    } else if (x.isFile()) {
                        showToast(x.getAbsolutePath() + " is a file");
                    } else {
                        showToast("Root directory");
                    }

                }

                @Override
                public void onValueDeselected() {

                }
            });
            int ossz = 0;
            int i = 0;
            for (Map.Entry<Integer, String> entry : sorted_hashmap.entrySet()) {
                ossz = ossz + entry.getKey();
            }
            for (Map.Entry<Integer, String> entry : sorted_hashmap.entrySet()) {
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                SliceValue s = new SliceValue();
                s.setColor(color);
                s.setValue(entry.getKey());
                s.setLabel(entry.getValue() + " " + String.valueOf(entry.getKey() / 1024 / 1024) + " mb");
                pieData.add(s);

                Log.d("VALUE_", "key: " + entry.getKey());
                Log.d("VALUE_", "value: " + entry.getValue());
                i += entry.getKey();
            }
            PieChartData pieChartData = new PieChartData(pieData);
            pieChartData.setHasLabels(true);

            chart.startDataAnimation();
            chart.setPieChartData(pieChartData);
            chart.animate();
        }
    }
    public long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public void recursiveLoop(File[] files) {
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    //recursiveLoop(file.listFiles());
                    int size = (int) (folderSize(file));
                    if (size <= 0) {
                        size = 0;
                    }
                    hashmap.put(size, file.getName());

                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
