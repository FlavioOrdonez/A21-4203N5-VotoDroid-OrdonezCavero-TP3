package com.example.tp2android;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.tp2android.bd.BD;
import com.example.tp2android.exceptions.MauvaisVote;
import com.example.tp2android.exceptions.MauvaiseQuestion;
import com.example.tp2android.service.ServiceImplementation;
import com.example.tp2android.modele.VDVote;
import com.example.tp2android.modele.VDQuestion;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private BD bd;
    private ServiceImplementation service;

    // S'exécute avant chacun des tests. Crée une BD en mémoire
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        bd = Room.inMemoryDatabaseBuilder(context, BD.class).build();
        service = ServiceImplementation.getInstance(bd);
    }

    //region Tests de question
    @Test(expected = MauvaiseQuestion.class)
    public void ajoutQuestionKOVide() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "";
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test(expected = MauvaiseQuestion.class)
    public void ajoutQuestionKOCourte() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "aa";
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test(expected = MauvaiseQuestion.class)
    public void ajoutQuestionKOLongue() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        for (int i = 0 ; i < 256 ; i ++) question.texteQuestion += "aa";
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test(expected = MauvaiseQuestion.class)
    public void ajoutQuestionKOIDFixe() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "aaaaaaaaaaaaaaaa";
        question.idQuestion = 5L;
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test
    public void ajoutQuestionOK() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "Aimes-tu les brownies au chocolat?";
        service.creerQuestion(question);

        Assert.assertNotNull(question.idQuestion);
    }


    @Test(expected = MauvaiseQuestion.class)
    public void ajoutQuestionKOExiste() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        VDQuestion question2 = new VDQuestion();

        question.texteQuestion = "Aimes-tu les brownies au chocolat?";
        question2.texteQuestion = "Aimes-tu les BROWNIES au chocolAT?";

        service.creerQuestion(question);
        service.creerQuestion(question2);

        //TODO Ce test va fail tant que vous n'implémenterez pas toutesLesQuestions() dans ServiceImplementation
        Assert.fail("Exception MauvaiseQuestion non lancée");
    }
    //endregion

    //Mes tests
    //region Tests Vote
    @Test(expected = MauvaisVote.class)
    public void ajoutVoteKONull() throws MauvaisVote {
        VDVote vote = new VDVote();
        vote.nomVotant = null;
        service.creerVote(vote);
        Assert.fail("Exception MauvaisVote lancée");
    }

    @Test(expected = MauvaisVote.class)
    public void ajoutVoteKOCourt() throws MauvaisVote {
        VDVote vote = new VDVote();
        vote.nomVotant = "cou ";//4 characte mais seulement 3 imprimables
        service.creerVote(vote);
        Assert.fail("Exception MauvaisVote lancée");
    }

    @Test(expected = MauvaisVote.class) //fonctionne qd run tous les test mais fail qd run tt seul
    public void ajoutVoteKODejaVote() throws MauvaisVote {
        VDVote vote1 = new VDVote();
        VDVote vote2 = new VDVote();
        vote1.nomVotant = "Johnatan";
        vote2.nomVotant = "Johnatan";
        vote1.idQuestion = 1L;
        vote2.idQuestion = 1L;

        service.creerVote(vote1);
        service.creerVote(vote2);
        Assert.fail("Exception MauvaisVote lancée");
    }
    @Test(expected = MauvaisVote.class)
    public void ajoutVoteKORatingNegatif() throws MauvaisVote {
        VDVote vote1 = new VDVote();
        vote1.rating = -1;
        service.creerVote(vote1);
        Assert.fail("Exception MauvaisVote lancée");
    }
    @Test(expected = MauvaisVote.class)
    public void ajoutVoteKORatingTropGrand() throws MauvaisVote {
        VDVote vote1 = new VDVote();
        vote1.rating = (float)5.5;
        service.creerVote(vote1);
        Assert.fail("Exception MauvaisVote lancée");
    }

    @Test
    public void ajoutVoteOK() throws MauvaisVote {
        VDVote vote = new VDVote();
        vote.nomVotant = "Johns";
        vote.rating = (float) 2.5;
        service.creerVote(vote);
        Assert.assertNotNull(vote.idVote);
    }
    //endregion

    @Test
    public void effacerTousVotesOK() throws MauvaisVote, MauvaiseQuestion {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "Question";
        service.creerQuestion(q);

        VDVote vote = new VDVote();
        vote.nomVotant = "Johns";
        vote.rating = (float) 2.5;
        vote.idQuestion = 1L;
        service.creerVote(vote);
        bd.monDao().effacerVotesALL();
        assertEquals(0, bd.monDao().recupererVotes().size() );
    }
    @Test
    public void effacerToutesQuestionsOK() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "Question à effacer";
        VDQuestion question2 = new VDQuestion();
        question2.texteQuestion = "Question à effacer";
        service.creerQuestion(question2);
        bd.monDao().effacerQuestionsALL();
        assertEquals(0, service.toutesLesQuestions().size());
    }


    //region Tests ecran de resulat
    @Test
    public void moyenneOK() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "question";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 5;
        v.nomVotant = "john";
        service.creerVote(v);

        float moy = service.moyenneVotes(q.idQuestion);
        assertEquals(5,moy, 0.5);


    }






    //endregion


    /*
    @After
    public void closeDb() {
        bd.close();
    }
    */
}