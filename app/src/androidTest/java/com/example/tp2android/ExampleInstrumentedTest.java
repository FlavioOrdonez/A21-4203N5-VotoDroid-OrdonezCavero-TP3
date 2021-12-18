package com.example.tp2android;

import android.content.Context;
import android.util.SparseIntArray;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Test(expected = MauvaiseQuestion.class)//GOOD
    public void ajoutQuestionKOVide() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "";
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test(expected = MauvaiseQuestion.class)//GOOD
    public void ajoutQuestionKOCourte() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "aa";
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test(expected = MauvaiseQuestion.class)//GOOD
    public void ajoutQuestionKOLongue() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        for (int i = 0 ; i < 256 ; i ++) question.texteQuestion += "aa";
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test(expected = MauvaiseQuestion.class)//GOOD
    public void ajoutQuestionKOIDFixe() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "aaaaaaaaaaaaaaaa";
        question.idQuestion = 5L;
        service.creerQuestion(question);

        Assert.fail("Exception MauvaiseQuestion non lancée");
    }


    @Test//GOOD
    public void ajoutQuestionOK() throws MauvaiseQuestion {
        VDQuestion question = new VDQuestion();
        question.texteQuestion = "Aimes-tu les brownies au chocolat?";
        service.creerQuestion(question);

        Assert.assertNotNull(question.idQuestion);
    }


    @Test(expected = MauvaiseQuestion.class)//GOOD
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
    @Test(expected = MauvaisVote.class)//GOOD
    public void ajoutVoteKONull() throws MauvaisVote {
        VDVote vote = new VDVote();
        vote.nomVotant = null;
        service.creerVote(vote);
        Assert.fail("Exception MauvaisVote lancée");
    }

    @Test(expected = MauvaisVote.class)//GOOD
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
    @Test(expected = MauvaisVote.class)//GOOD
    public void ajoutVoteKORatingNegatif() throws MauvaisVote {
        VDVote vote1 = new VDVote();
        vote1.rating = -1;
        service.creerVote(vote1);
        Assert.fail("Exception MauvaisVote lancée");
    }
    @Test(expected = MauvaisVote.class)//GOOD
    public void ajoutVoteKORatingTropGrand() throws MauvaisVote {
        VDVote vote1 = new VDVote();
        vote1.rating = (float)5.5;
        service.creerVote(vote1);
        Assert.fail("Exception MauvaisVote lancée");
    }

    @Test//GOOD
    public void ajoutVoteOK() throws MauvaisVote {
        VDVote vote = new VDVote();
        vote.nomVotant = "Johns";
        vote.rating = (float) 2.5;
        service.creerVote(vote);
        Assert.assertNotNull(vote.idVote);
    }
    //endregion

    //region Test ListedeQuestion
    @Test//fonctionne seul, fail quand tous les test sont run
    public void listQuestionOKCount() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "qListCount1";
        service.creerQuestion(q);
        VDQuestion a = new VDQuestion();
        a.texteQuestion = "qListCount2";
        service.creerQuestion(a);

       List<VDQuestion> list= service.toutesLesQuestions();
       assertEquals(2, list.size());
    }

    @Test//GOOD
    public void listQuestionOKOrder() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "qListCountOrder1";
        service.creerQuestion(q); //Na pas de vote

        VDQuestion a = new VDQuestion();
        a.texteQuestion = "qListCountOrder2";
        service.creerQuestion(a);

        VDVote v = new VDVote();
        v.idQuestion = 2L;
        v.rating = 5;
        v.nomVotant = "john";
        service.creerVote(v);// 1 vote

        List<VDQuestion> list= service.toutesLesQuestions();
        assertEquals("qListCountOrder2", list.get(0).texteQuestion);//Question crée en 2em doit apparaitre en 1er
    }

    //endregion


    //region Tests ecran de resulat
    @Test//GOOD
    public void moyenneOK() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "questionMoyenne";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 5;
        v.nomVotant = "john";
        service.creerVote(v);
        VDVote w = new VDVote();
        w.idQuestion = 1L;
        w.rating = 4;
        w.nomVotant = "johns";
        service.creerVote(w);
        VDVote e = new VDVote();
        e.idQuestion = 1L;
        e.rating = 3;
        e.nomVotant = "johnsa";
        service.creerVote(e);
        int position = 0; //Il n'y a qu'une seule question

        float moy = service.moyenneVotes(position);
        assertEquals(4,moy, 0.5);
    }

    @Test//fonctionne seul, fail quand tous les test sont run
    public void moyenneOKZero() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "questionMoyenneZero";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 0;
        v.nomVotant = "johnMOZ";
        service.creerVote(v);
        VDVote w = new VDVote();
        w.idQuestion = 1L;
        w.rating = 0;
        w.nomVotant = "johnsMOZ";
        service.creerVote(w);
        VDVote e = new VDVote();
        e.idQuestion = 1L;
        e.rating = 0;
        e.nomVotant = "johnsaMOZ";
        service.creerVote(e);
        int position = 0; //Il n'y a qu'une seule question

        float moy = service.moyenneVotes(position);
        assertEquals(0,moy, 0.5);
    }
    @Test//fonctionne seul, fail quand tous les test sont run
    public void ecartTypeOK() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "questionEcartType";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 5;
        v.nomVotant = "johnQET";
        service.creerVote(v);
        VDVote w = new VDVote();
        w.idQuestion = 1L;
        w.rating = 4;
        w.nomVotant = "johnsQET";
        service.creerVote(w);
        VDVote e = new VDVote();
        e.idQuestion = 1L;
        e.rating = 3;
        e.nomVotant = "johnsaQET";
        service.creerVote(e);
        int position = 0; //Il n'y a qu'une seule question

        float ecartT = service.ecartTypeVotes(position);
        assertEquals(0.81,ecartT, 0.5);
    }
    @Test//fonctionne seul, fail quand tous les test sont run
    public void ecartTypeOKZero() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "questionEcartTypeZero";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 0;
        v.nomVotant = "johnQEZ";
        service.creerVote(v);
        VDVote w = new VDVote();
        w.idQuestion = 1L;
        w.rating = 0;
        w.nomVotant = "johnsQEZ";
        service.creerVote(w);
        VDVote e = new VDVote();
        e.idQuestion = 1L;
        e.rating = 0;
        e.nomVotant = "johnsaQEZ";
        service.creerVote(e);
        int position = 0; //Il n'y a qu'une seule question

        float ecartT = service.ecartTypeVotes(position);
        assertEquals(0,ecartT, 0.5);
    }
    @Test//fonctionne seul, fail quand tous les test sont run
    public void distributionOKKey() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "questionDistributionKey";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 5;
        v.nomVotant = "johnDK";
        service.creerVote(v);
        int position = 0; //Il n'y a qu'une seule question

        Map<Integer,Integer> m = service.distributionVotes(position);
        List<Integer> keyList = new ArrayList<Integer>(m.keySet());
        int key =keyList.get(0);
        assertEquals(1,key);
    }

    @Test//fonctionne seul, fail quand tous les test sont run
    public void distributionOKValue() throws MauvaiseQuestion, MauvaisVote {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "questionDistributionValue";
        service.creerQuestion(q);
        VDVote v = new VDVote();
        v.idQuestion = 1L;
        v.rating = 5;
        v.nomVotant = "johnDV";
        service.creerVote(v);
        int position = 0; //Il n'y a qu'une seule question

        Map<Integer,Integer> m = service.distributionVotes(position);
        List<Integer> valueList = new ArrayList<>(m.values());
        int key =valueList.get(0);
        assertEquals(5,key);
    }
    //endregion


    //region Test menu developpement
    @Test//GOOD
    public void effacerTousVotesOK() throws MauvaisVote, MauvaiseQuestion {
        VDQuestion q = new VDQuestion();
        q.texteQuestion = "QuestionVoteEff";
        service.creerQuestion(q);

        VDVote vote = new VDVote();
        vote.nomVotant = "JohnsQVE";
        vote.rating = (float) 2.5;
        vote.idQuestion = 1L;
        service.creerVote(vote);
        bd.monDao().effacerVotesALL();
        assertEquals(0, bd.monDao().recupererVotes().size() );
    }
    @Test//fonctionne seul, fail quand tous les test sont run
    public void effacerToutesQuestionsOK() throws MauvaiseQuestion {
        VDQuestion qEffacerQuestionsOK = new VDQuestion();
        qEffacerQuestionsOK.texteQuestion = "Question à effacer";
        VDQuestion qEffacerQuestionsOK2 = new VDQuestion();
        qEffacerQuestionsOK2.texteQuestion = "Question à effacer";
        service.creerQuestion(qEffacerQuestionsOK2);
        bd.monDao().effacerQuestionsALL();
        assertEquals(0, service.toutesLesQuestions().size());
    }
    //endregion



/*
    @After
    public void closeDb() {
        bd.close();
    }*/


}