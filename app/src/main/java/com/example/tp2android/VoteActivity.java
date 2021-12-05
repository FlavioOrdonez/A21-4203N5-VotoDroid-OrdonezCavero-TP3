package com.example.tp2android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.tp2android.bd.BD;
import com.example.tp2android.databinding.ActivityVoteBinding;
import com.example.tp2android.exceptions.MauvaisVote;
import com.example.tp2android.exceptions.MauvaiseQuestion;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.modele.VDVote;
import com.example.tp2android.service.ServiceImplementation;

public class VoteActivity extends AppCompatActivity {
    private ActivityVoteBinding binding;
    private ServiceImplementation service;///
    private BD maBD;//
    private TextView questionText;
    private EditText voteNom;
    private RatingBar rating;
    private long questionId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //connection a la BD
        maBD =  Room.databaseBuilder(getApplicationContext(), BD.class, "BDQuestions")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        service = ServiceImplementation.getInstance(maBD);

        int position = getIntent().getIntExtra("questionId", -1);//Récupere le id de questionAdapter


        //Pour afficher le design de activity_question
        binding = ActivityVoteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        questionText = (TextView) findViewById(R.id.tvVote_question);
        questionText.setText(maBD.monDao().recupererQuestions().get(position).texteQuestion);//Met le txt correspondant a la question

        //Ajouter dans manifest une activity

        questionId = maBD.monDao().recupererQuestions().get(position).idQuestion;
        voteNom = (EditText) findViewById(R.id.editTextTextPersonName);
        rating = (RatingBar) findViewById(R.id.starsRating);


        //Lie le bouton vote avec le Main Activity
        binding.btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerVote();
                Intent i = new Intent(VoteActivity.this,
                        MainActivity.class );
                startActivity(i);
            }
        });

    }
    //Methode pour creer un vote
    private void creerVote (){
        try{
            VDVote monVote = new VDVote();
            monVote.idQuestion = questionId;
            monVote.rating = rating.getRating();
            monVote.nomVotant = voteNom.getText().toString();
            service.creerVote(monVote);
        }catch (MauvaisVote m){
            Log.e("CREERVOTE", "Impossible de créer le vote : " + m.getMessage());
        }
    }
}