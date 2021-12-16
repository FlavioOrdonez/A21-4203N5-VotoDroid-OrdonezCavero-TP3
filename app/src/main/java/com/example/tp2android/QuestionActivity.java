package com.example.tp2android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.tp2android.bd.BD;
import com.example.tp2android.databinding.ActivityQuestionBinding;
import com.example.tp2android.exceptions.MauvaiseQuestion;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.service.ServiceImplementation;

public class QuestionActivity extends AppCompatActivity {
    private ActivityQuestionBinding binding;
    private ServiceImplementation service;///
    private BD maBD;//
    private EditText questionText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Pour afficher le design de activity_question
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        //connection a la BD
        maBD =  Room.databaseBuilder(getApplicationContext(), BD.class, "BDQuestions")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        service = ServiceImplementation.getInstance(maBD);

        //Lie l'élément editText de la classe par son id
        questionText = (EditText) findViewById(R.id.editTextTextPersonName);

        //Lie le bouton pose la question avec le Main Activity
        binding.btnPoserQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerQuestion();
                Intent i = new Intent(QuestionActivity.this,
                        MainActivity.class );
                startActivity(i);
            }

        });
    }

    //Methode pour creer question
    private void creerQuestion (){
        try{
            VDQuestion maQuestion = new VDQuestion();
            maQuestion.texteQuestion = questionText.getText().toString();
            service.creerQuestion(maQuestion);
            Toast.makeText(getApplicationContext(),"Question créée avec succès",Toast.LENGTH_SHORT).show();

        }catch (MauvaiseQuestion m){
            Log.e("CREERQUESTION", "Impossible de créer la question : " + m.getMessage());
            Toast.makeText(getApplicationContext(),m.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
