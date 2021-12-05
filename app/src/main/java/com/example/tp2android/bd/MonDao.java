package com.example.tp2android.bd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.modele.VDVote;

import java.util.List;


@Dao
public abstract class MonDao {
    @Insert
    public abstract Long insertQuestion(VDQuestion v);

    //TODO Compléter les autres actions


    //Selectionne toutes les questions
    @Query("SElECT * FROM VDQuestion")
    public abstract List<VDQuestion> recupererQuestions();

    //Selectionne touts les votes
    @Query("SElECT * FROM VDVote")
    public abstract List<VDVote> recupererVotes();


    //Efface toutes les questions
    @Query("DELETE FROM VDQuestion")
    public abstract void effacerQuestionsALL();

    //Efface tous les votes
    @Query("DELETE FROM VDVote")
    public abstract void effacerVotesALL();


    @Insert
    public abstract Long insertVote(VDVote vdVote);

    @Transaction
    public Long creerQuestioVote(VDQuestion quest, List<VDVote> votes){
        Long id = this.insertQuestion(quest);

        for(VDVote v : votes){
            quest.idQuestion = id;
            this.insertVote(v);
        }
        return id;
    }
}
