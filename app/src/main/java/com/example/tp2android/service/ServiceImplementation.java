package com.example.tp2android.service;

import com.example.tp2android.exceptions.MauvaisVote;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.bd.BD;
import com.example.tp2android.modele.VDVote;
import com.example.tp2android.exceptions.MauvaiseQuestion;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceImplementation {

    private static ServiceImplementation single_instance = null;
    private BD maBD;

    private ServiceImplementation(BD maBD){
        this.maBD = maBD;
    }

    public static ServiceImplementation getInstance(BD maBD)
    {
        if (single_instance == null)
            single_instance = new ServiceImplementation(maBD);

        return single_instance;
    }

    public void creerQuestion(VDQuestion vdQuestion) throws MauvaiseQuestion {
        // Validation
        if (vdQuestion.texteQuestion == null || vdQuestion.texteQuestion.trim().length() == 0) throw new MauvaiseQuestion("Question vide");
        if (vdQuestion.texteQuestion.trim().length() < 5) throw new MauvaiseQuestion("Question trop courte");
        if (vdQuestion.texteQuestion.trim().length() > 255) throw new MauvaiseQuestion("Question trop longue");
        if (vdQuestion.idQuestion != null) throw new MauvaiseQuestion("Id non nul. La BD doit le gérer");

        // Doublon du texte de la question
        for (VDQuestion q : toutesLesQuestions()){
            if (q.texteQuestion.toUpperCase().equals(vdQuestion.texteQuestion.toUpperCase())){
                    throw new MauvaiseQuestion("Question existante");
            }
        }
        // Ajout
        vdQuestion.idQuestion = maBD.monDao().insertQuestion(vdQuestion);
    }

    public void creerVote(VDVote vdVote) throws MauvaisVote {
        if (vdVote.nomVotant == null) throw new MauvaisVote("Aucun nom dans associé au vote");
        if(vdVote.nomVotant.trim().length()<4) throw  new MauvaisVote("Nom trop petit");
        if(vdVote.rating > 5.0) throw new MauvaisVote("Vote trop grand");
        if(vdVote.rating < 0) throw new MauvaisVote("Trop petit rating");
        List<VDVote> lvote = maBD.monDao().recupererVotes();

        for (int i= 0; i< lvote.size(); i++ ){
            if(vdVote.nomVotant.equals(lvote.get(i).nomVotant))
                if(vdVote.idQuestion.equals(lvote.get(i).idQuestion))
                    throw new MauvaisVote("Vote deja present pour cet utilisateur");
        }
        vdVote.idVote = maBD.monDao().insertVote(vdVote);
    }
    
    public List<VDQuestion> toutesLesQuestions() {
        //TODO Trier la liste reçue en BD par nombre de votes et la retourner

       // new ArrayList<>() maBD.monDao().recupererQuestions();
        return maBD.monDao().recupererQuestions();
    }

    public float moyenneVotes(float questionId) {
        List<VDVote> listVotes = recupererListSelonQuestion(questionId);
        float total = 0;
        int i = 0;
        while(i < listVotes.size()){
            total += listVotes.get(i).rating;
            i++;
        }
        return total / listVotes.size();
    }

    public float ecartTypeVotes(float questionId) {
        List<VDVote> listVotes =  recupererListSelonQuestion(questionId);
        float moyenne = moyenneVotes((int)questionId);
        float total =0;
        for (int i = 0; i< listVotes.size(); i++){
            float etapeUn = (listVotes.get(i).rating - moyenne); //Chaques valeurs moins moyenne
            etapeUn = etapeUn*etapeUn; //Resultat au carre
            total += etapeUn;
        }
        float variance = total/listVotes.size();//resulat de la variance
        return (float)java.lang.Math.sqrt(variance);//Variance racine^2 = ecart type
    }

    public Map<Integer, Integer> distributionVotes() {
        return null;
    }


    //Recupere tous les votes d'une question passée en parametre
    public List<VDVote> recupererListSelonQuestion(float questionId){
        VDQuestion q = maBD.monDao().recupererQuestions().get((int)questionId);
        List<VDVote> listVotes =  maBD.monDao().recupererVotes();

        for(int j = listVotes.size() -1; j >= 0; j--){
            VDVote v = listVotes.get(j);
            if (!v.idQuestion.equals(q.idQuestion)){
                listVotes.remove(v);
            }
        }
        return listVotes;
    }
}
