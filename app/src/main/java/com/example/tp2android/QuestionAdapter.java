package com.example.tp2android;

import java.util.List;
import com.example.tp2android.bd.BD;
import com.example.tp2android.service.ServiceImplementation;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;



import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
        public List<Question> list;
        public int indexQuestion;

    private ServiceImplementation service;///
    private BD maBD;//
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvQuestion;
            public ImageView imgView; //!
            public ViewHolder(LinearLayout view) {
                super(view);
                tvQuestion = view.findViewById(R.id.tvQuestion);
                imgView = view.findViewById(R.id.imgView); //!
            }
        }

        public QuestionAdapter() { list = new ArrayList<>(); }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            LinearLayout v =(LinearLayout) LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.question_item, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            Question questionCourrante =  list.get(position);
            viewHolder.tvQuestion.setText(questionCourrante.question);
            viewHolder.imgView.setImageResource(questionCourrante.img); //!

            // Pour lier une activité avec le recycler view
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   // indexQuestion = viewHolder.getLayoutPosition();
                    Intent i = new Intent(view.getContext(), VoteActivity.class);

                    //Voir video 1ere semaine android : EXTRA
                    i.putExtra("questionId", position );//Passes des donnes dans une autre activite
                    view.getContext().startActivity(i);
                }
            });

            // Lier l'imageView à l'intérieur du recycle view
            viewHolder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ResultatsActivity.class);
                    i.putExtra("questionId", position );//Passes des donnes dans une autre activite
                    view.getContext().startActivity(i);
                }
            });
        }

        // renvoi taille de la liste
        @Override
        public int getItemCount() {
            return list.size();
        }

}