package com.sontme.esp.sizy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.slice.Slice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.measurement.module.Analytics;

import io.fabric.sdk.android.Fabric;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {


    Map<Integer, String> hashmap = new HashMap<Integer, String>();
    Map<String, Integer> extensions = new HashMap<String, Integer>();

    int filecount = 0;
    int dircount = 0;

    //File fileee = new File("/");
    File fileee = new File(Environment.getRootDirectory().getAbsolutePath());
    File lastpath2 = fileee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        Button internal = findViewById(R.id.internal);
        Button external = findViewById(R.id.external);

        internal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                drawChart(f);
            }
        });
        external.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(getApplicationContext().getFilesDir().getAbsolutePath());
                drawChart(f);
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Confirm deletion")
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setMessage("Are you sure to delete: " + lastpath2.getAbsolutePath())
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                try {
                                    FileUtils.deleteDirectory(lastpath2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
            }
        });
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(lastpath2.getParent());
                drawChart(f);
                hashmap.clear();
            }
        });
        try {
            drawChart(lastpath2);
        } catch (Exception e) {
            e.printStackTrace();
        }


        GoogleAnalytics.getInstance(this).reportActivityStart(this);

        MobileAds.initialize(this, "ca-app-pub-4051604993335718~5342619305");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-4051604993335718/8331389509");

        PublisherAdView mPublisherAdView;
        mPublisherAdView = findViewById(R.id.publisherad);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (errorCode == 3) {
                } else {
                }
            }
        });

    }

    public void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void drawChart(File dir) {
        if (dir.getAbsolutePath().length() > 1) {
            lastpath2 = dir;
            TextView curdir = findViewById(R.id.txt_curdir);
            curdir.setText("Current Directory: " + lastpath2.getAbsolutePath());

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
                    String with_metric = String.valueOf(value.getLabel());
                    String[] without_metric = with_metric.split(" ");
                    File x = new File(lastpath2.getAbsolutePath() + "/" + without_metric[0]);
                    Log.d("FONTOS_PATH_PIECLICK_", x.getAbsolutePath());
                    if (x.isDirectory()) {
                        drawChart(x);
                    } else {
                        showToast("THIS IS A FILE");
                    }
                }

                @Override
                public void onValueDeselected() {
                }
            });
            int ossz = 0;
            int fileColorCounter = 255;
            for (Map.Entry<Integer, String> entry : sorted_hashmap.entrySet()) {
                ossz++;
            }
            for (Map.Entry<Integer, String> entry : sorted_hashmap.entrySet()) {
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                int fileColorA = Color.argb(fileColorCounter, 0, 0, 255);

                SliceValue s = new SliceValue();
                if (entry.getValue().contains("(F) ")) {
                    if (fileColorA >= 1 && fileColorA <= 255) {
                        fileColorCounter = -(100 / ossz);
                    } else {
                        fileColorCounter = 255;
                    }
                    s.setColor(fileColorA);
                } else {
                    s.setColor(color);
                }
                s.setValue(entry.getKey());
                s.setLabel(entry.getValue() + " " + String.valueOf(entry.getKey() / 1024 / 1024) + " mb");
                Log.d("CHART_", String.valueOf(entry.getKey()) + "_" + entry.getValue());
                pieData.add(s);

            }
            PieChartData pieChartData = new PieChartData(pieData);
            pieChartData.setHasCenterCircle(true);

            pieChartData.setCenterCircleScale(0.65f);
            int size = (int) (folderSize(lastpath2) / 1024 / 1024);
            String x = String.valueOf(size) + " Mb";
            pieChartData.setCenterText1(x);
            pieChartData.setCenterText1Color(Color.BLACK);
            pieChartData.setCenterText1FontSize(18);
            pieChartData.setCenterText2("Files: " + filecount + " Directories: " + dircount);

            pieChartData.setCenterText2Color(Color.RED);
            pieChartData.setCenterText2FontSize(15);
            pieChartData.setHasLabels(true);
            pieChartData.setSlicesSpacing(5);
            pieChartData.setValueLabelBackgroundEnabled(true);
            pieChartData.setValueLabelBackgroundColor(Color.MAGENTA);
            pieChartData.setValueLabelsTextColor(Color.WHITE);
            chart.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            chart.setPieChartData(pieChartData);
            chart.startDataAnimation();
            chart.animate();
        } else {
            showToast("TOP Directory Reached");
        }
    }

    public String getSDCardDirectory() {
        String SdcardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        String dir = SdcardPath.substring(SdcardPath.lastIndexOf('/') + 1);
        System.out.println(dir);
        String[] trimmed = SdcardPath.split(dir);
        String sdcardPath = trimmed[0];
        System.out.println(sdcardPath);
        return sdcardPath;
    }

    public long folderSize(File directory) {
        try {
            long length = 0;
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
            }
            return length;
        } catch (Exception e) {
            return 0;
        }
    }

    public void recursiveLoop(File[] files) {
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    dircount++;
                    //recursiveLoop(file.listFiles());
                    int size = (int) (folderSize(file));
                    if (size <= 0) {
                        size = 0;
                    }
                    hashmap.put(size, file.getName());
                } else {
                    filecount++;
                    hashmap.put((int) (file.length()), "(F) " + file.getName());
                    try {
                        //String ext = file.getName().substring(file.getName().lastIndexOf("."));
                        String ext = FilenameUtils.getExtension(file.getName());
                        extensions.put(ext, extensions.get(ext) + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry entry : extensions.entrySet()) {
            Log.d("EXTENSIONS_ ", entry.getKey() + " : " + entry.getValue());
        }

    }

    public void hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void logUser() {

    }
}
