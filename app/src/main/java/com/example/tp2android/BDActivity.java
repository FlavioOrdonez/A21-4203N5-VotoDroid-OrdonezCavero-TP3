package com.example.tp2android;

import androidx.appcompat.app.AppCompatActivity;

import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;

import com.example.tp2android.bd.BD;
import com.example.tp2android.exceptions.MauvaiseQuestion;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.service.ServiceImplementation;


public class BDActivity  extends AppCompatActivity {

    private ServiceImplementation service;
    private BD maBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maBD =  Room.databaseBuilder(getApplicationContext(), BD.class, "BDQuestions")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        service = ServiceImplementation.getInstance(maBD);
        creerQuestion();
    }

    private void creerQuestion (){
        try{
            VDQuestion maQuestion = new VDQuestion();
            maQuestion.texteQuestion = "As-tu hâte au nouveau film The Matrix Resurrections?";
            service.creerQuestion(maQuestion);
        }catch (MauvaiseQuestion m){
            Log.e("CREERQUESTION", "Impossible de créer la question : " + m.getMessage());
        }
    }


}
