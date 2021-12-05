package com.example.tp2android.modele;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(foreignKeys = @ForeignKey(entity = VDQuestion.class,
        parentColumns = "idQuestion",
        childColumns = "idQuestion"),
        indices = {@Index("idQuestion")})
public class VDVote {
    //TODO Champs à définir
    //Id cle primaire autogénérée par BD
    @PrimaryKey(autoGenerate = true)
    public Long idVote;

    //Est lie en foreign key par la declaration du haut @Entity
    @ColumnInfo
    public Long idQuestion;

    @ColumnInfo
    public float rating;

    @ColumnInfo
    public String nomVotant;
}
