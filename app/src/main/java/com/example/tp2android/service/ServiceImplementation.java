package com.example.tp2android.service;

import com.example.tp2android.exceptions.MauvaisVote;
import com.example.tp2android.modele.VDQuestion;
import com.example.tp2android.bd.BD;
import com.example.tp2android.modele.VDVote;
import com.example.tp2android.exceptions.MauvaiseQuestion;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

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
        List<VDQuestion> listQuestions = maBD.monDao().recupererQuestions();
        List<VDVote> listVote = maBD.monDao().recupererVotes();
        for(int j = 0; j< listQuestions.size(); j++) {
            int cpt = 0;
            VDQuestion actualQuest = listQuestions.get(j);
            for (int i = 0; i < listVote.size(); i++) {
                VDVote v = listVote.get(i);

                if(actualQuest.idQuestion.equals(v.idQuestion)){
                    cpt++;
                }
            }
            actualQuest.nbreVote = cpt;
        }
        //Trier la liste selon nombre de vote du plus petit au plus grand
        for(int i = 0 ; i < listQuestions.size();i++) {
            for (int j = i + 1; j < listQuestions.size(); j++) {
                if (listQuestions.get(i).nbreVote > listQuestions.get(j).nbreVote) {
                    int temp = listQuestions.get(i).nbreVote;
                    listQuestions.get(i).nbreVote = listQuestions.get(j).nbreVote;
                    listQuestions.get(j).nbreVote = temp;
                }
            }
        }
        //Inverse la liste
        Collections.reverse(listQuestions);
        return listQuestions;
    }

    public float moyenneVotes(int position) {
        List<VDVote> listVotes = recupererListSelonQuestion(position);
        float total = 0;
        int i = 0;
        while(i < listVotes.size()){
            total += listVotes.get(i).rating;
            i++;
        }
        return total / listVotes.size();
    }

    public float ecartTypeVotes(int position) {
        List<VDVote> listVotes =  recupererListSelonQuestion(position);
        float moyenne = moyenneVotes((int)position);
        float total =0;
        for (int i = 0; i< listVotes.size(); i++){
            float etapeUn = (listVotes.get(i).rating - moyenne); //Chaques valeurs moins moyenne
            etapeUn = etapeUn*etapeUn; //Resultat au carre
            total += etapeUn;
        }
        float variance = total/listVotes.size();//resulat de la variance
        return (float)java.lang.Math.sqrt(variance);//Variance racine^2 = ecart type
    }

    public Map<Integer, Integer> distributionVotes(Integer position) {
        List<VDVote> votes = recupererListSelonQuestion(position);
        Map<Integer, Integer> mapVoteValue = new HashMap<>();
        for (int i = 0; i < votes.size(); i++){
            Long voteId = votes.get(i).idVote;
            int rating = (int)votes.get(i).rating;
            mapVoteValue.put( voteId.intValue(), rating);
        }
        return mapVoteValue;
    }

    //Recupere tous les votes d'une question passée en parametre
    private List<VDVote> recupererListSelonQuestion(int position){
        VDQuestion q = maBD.monDao().recupererQuestions().get(position);
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
