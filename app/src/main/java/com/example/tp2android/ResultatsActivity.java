package com.example.tp2android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.tp2android.bd.BD;
import com.example.tp2android.databinding.ActivityResultatsBinding;
import com.example.tp2android.databinding.ActivityVoteBinding;
import com.example.tp2android.service.ServiceImplementation;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class ResultatsActivity extends AppCompatActivity {
    ActivityResultatsBinding binding;
    BarChart chart;
    private BD maBD;//
    private ServiceImplementation service;///
    private TextView moyenneText;
    private TextView ecartText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Résultats");

        //connection a la BD
        maBD =  Room.databaseBuilder(getApplicationContext(), BD.class, "BDQuestions")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        service = ServiceImplementation.getInstance(maBD);

        //Pour afficher le design de activity_question
        binding = ActivityResultatsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Ajouter dans manifest une activity

        moyenneText = (TextView) findViewById(R.id.tVMoyenne_result);
        moyenneText.setText(String.valueOf(service.moyenneVotes()));

        ecartText = (TextView) findViewById(R.id.tVEcart_type_results);
        ecartText.setText(String.valueOf(service.ecartTypeVotes()));


        /*POUR LE GRAPHIQUE */
        chart = findViewById(R.id.chart);


        /* Settings for the graph - Change me if you want*/
        chart.setMaxVisibleValueCount(5);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new DefaultAxisValueFormatter(0));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setGranularity(1);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new DefaultAxisValueFormatter(0));
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);



        /* Data and function call to bind the data to the graph */
        Map<Integer, Integer> dataGraph = new HashMap<Integer, Integer>() {{
            put(0,0);//change les valeurs
            put(1,0);
            put(2,0);
            put(3, 2);
            put(4, 1);
            put(5, 4);

        }};
        setData(dataGraph);
    }

    private void setData(Map<Integer, Integer> datas) {

        ArrayList<BarEntry> values = new ArrayList<>();

        /* Every bar entry is a bar in the graphic */
        for (Map.Entry<Integer, Integer> key : datas.entrySet()){
            values.add(new BarEntry(key.getKey() , key.getValue()));
        }

        BarDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Notes"); //change la légende

            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(.9f);
            chart.setData(data);
        }
    }

}
