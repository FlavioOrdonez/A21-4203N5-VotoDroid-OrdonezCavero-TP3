package com.example.tp2android.modele;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VDQuestion {
    @PrimaryKey(autoGenerate = true)
    public Long idQuestion;

    @ColumnInfo
    public String texteQuestion;
}
