package com.example.tp2android.bd;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.modele.VDVote;


@Database(entities = {VDQuestion.class, VDVote.class}, version = 2)
public abstract class BD extends RoomDatabase {
    public abstract MonDao monDao();
}
