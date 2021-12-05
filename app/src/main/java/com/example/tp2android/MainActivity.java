package com.example.tp2android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.tp2android.bd.BD;
import com.example.tp2android.databinding.ActivityMainBinding;
import com.example.tp2android.exceptions.MauvaiseQuestion;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.service.ServiceImplementation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    QuestionAdapter qAdapater;
    private ServiceImplementation service;///
    private BD maBD;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //connection a la BD
        maBD =  Room.databaseBuilder(getApplicationContext(), BD.class, "BDQuestions")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        service = ServiceImplementation.getInstance(maBD);

        //Met la vu main par defaut
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Pour lier le bouton et la vue pour poser une question
        binding.btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.moyenneVotes();
                Intent i = new Intent(MainActivity.this,
                        QuestionActivity.class);
                startActivity(i);
            }
        });


        //Pour intialiser et remplir la liste
        this.initRecycler();
        this.remplirRecycler();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }


    //Méthode pour initialiser le RV
    private void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView); //récupere le RV
        recyclerView.setHasFixedSize(true);

        //linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //adapter
        qAdapater = new QuestionAdapter();
        recyclerView.setAdapter(qAdapater);
    }

    private void remplirRecycler() {
        for(int i = 0; i < service.toutesLesQuestions().size(); i++){
            Question q = new Question();
            q.question = service.toutesLesQuestions().get(i).texteQuestion;
            q.id = service.toutesLesQuestions().get(i).idQuestion;
            q.img = R.drawable.ic_baseline_pie_chart_24; //!
            qAdapater.list.add(q);
        }
        qAdapater.notifyDataSetChanged();
    }
}