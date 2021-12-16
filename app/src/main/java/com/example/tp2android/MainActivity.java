package com.example.tp2android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
                Intent i = new Intent(MainActivity.this,
                        QuestionActivity.class);
                startActivity(i);
            }
        });


        //Pour intialiser et remplir la liste
        this.initRecycler();
        this.remplirRecycler();

    }
    /* MENU */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int Id = item.getItemId();
        if (Id == R.id.action_questions) {
            effacerQuestions();
            return true;
        }
        if (Id == R.id.action_votes){
            effacerVotes();
            Toast.makeText(getApplicationContext(),"Tous les votes ont été effacés",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    //region MÉTHODES
    //Méthode -Recycler- pour initialiser le RV
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
    //Méthode -Recycler- pour remplir le RV
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

    //Methode -Menu- pour effacer toutes les questions
    private void effacerQuestions(){
        try{
            maBD.monDao().effacerVotesALL();
            maBD.monDao().effacerQuestionsALL();
            int size = qAdapater.list.size();
            qAdapater.list.clear();
            qAdapater.notifyItemRangeRemoved(0, size);
            if(size == 0){
                Toast.makeText(getApplicationContext(),"Aucune question à effacer",Toast.LENGTH_SHORT).show();
            }
            else if (size == 1){
                Toast.makeText(getApplicationContext(),size +" question effacée",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),size +" questions effacées",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.e("EFFACERQUESTIONS", "Impossible d'effacer les questions" );
        }
    }
    //Methode -Menu- pour effacer tous les votes
    private void effacerVotes(){
        try{
            maBD.monDao().effacerVotesALL();
        }catch (Exception e){
            Log.e("EFFACERVOTES", "Impossible d'effacer les votes" );
        }
    }
    //endregion
}