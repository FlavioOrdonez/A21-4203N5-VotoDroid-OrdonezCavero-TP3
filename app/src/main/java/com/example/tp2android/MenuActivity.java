package com.example.tp2android;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.tp2android.bd.BD;
import com.example.tp2android.service.ServiceImplementation;

public class MenuActivity extends AppCompatActivity {
    private ServiceImplementation service;///
    private BD maBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connection a la BD
        maBD =  Room.databaseBuilder(getApplicationContext(), BD.class, "BDQuestions")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        service = ServiceImplementation.getInstance(maBD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            // do something
            effacerVotes();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    //Methode pour effacer toutes les questions
    private void effacerQuestions(){
        try{
            maBD.monDao().effacerQuestionsALL();
        }catch (Exception e){
            Log.e("EFFACERQUESTIONS", "Impossible d'effacer les questions" );
        }
    }

    //Methode pour effacer tous les votes
    private void effacerVotes(){
        try{
            maBD.monDao().effacerVotesALL();
        }catch (Exception e){
            Log.e("EFFACERVOTES", "Impossible d'effacer les votes" );
        }
    }

}
