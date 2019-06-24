package com.example.dodo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class YapildiFragment extends Fragment {

    View DoneView;
    private RecyclerView mDoneList;
    private Button btn_does_aktif,btn_does_done_delete;
    private EditText DoesTitle,DoesDecs,DoesDate;
    private int i=0;

    private FirebaseAuth mAuth;
    private DatabaseReference AktifRef,PasifRef,myRef;
    private String currentUserID;

    public YapildiFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DoneView= inflater.inflate(R.layout.fragment_yapildi, container, false);
//-----------------------------------------------------------------------------------------------------
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        PasifRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);
        AktifRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);
        myRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);
//-----------------------------------------------------------------------------------------------------
        mDoneList=(RecyclerView) DoneView.findViewById(R.id.yapildi_recycler);
        mDoneList.setLayoutManager(new LinearLayoutManager(getContext()));
        btn_does_aktif=(Button) DoneView.findViewById(R.id.btn_does_pasif);
        btn_does_done_delete=(Button) DoneView.findViewById(R.id.btn_does_done_delete);

        DoesTitle=DoneView.findViewById(R.id.doesTitle);
        DoesDecs=DoneView.findViewById(R.id.doesDescription);
        DoesDate=DoneView.findViewById(R.id.doesDate);
//-----------------------------------------------------------------------------------------------------
        return DoneView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final Query myQuery =myRef.getRef().orderByChild("active").equalTo(false);
        FirebaseRecyclerOptions<Pasif> options=new
                FirebaseRecyclerOptions.Builder<Pasif>()
                .setQuery(myQuery,Pasif.class)
                .build();
        FirebaseRecyclerAdapter<Pasif,PasifDoesItemViewHolder> adapter =new FirebaseRecyclerAdapter<Pasif, PasifDoesItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PasifDoesItemViewHolder holder, int position, @NonNull final Pasif model) {

                final String donekey=getRef(position).getKey();
                AktifRef.child(donekey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            holder.doesTitle.setText(model.getTitle());
                            holder.doesDesc.setText(model.getDesc());
                            holder.doesDate.setText(model.getDate());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (i%2==0)
                                {
                                    holder.itemView.findViewById(R.id.btn_does_done_delete).setVisibility(View.VISIBLE);
                                    holder.itemView.findViewById(R.id.btn_does_aktif).setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    holder.itemView.findViewById(R.id.btn_does_aktif).setVisibility(View.INVISIBLE);
                                    holder.itemView.findViewById(R.id.btn_does_done_delete).setVisibility(View.INVISIBLE);
                                }
                                i++;
                                if (i==8)
                                {
                                    i=0;
                                }
                            }
                        });

                        final String ad=holder.doesTitle.getText().toString();

                        holder.itemView.findViewById(R.id.btn_does_done_delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]= new CharSequence[]
                                        {
                                                ad +" Sil",
                                                " İptal"
                                        };
                                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle(" Umarız görevini başarmışsındır, Artık silelim mi?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which==0)
                                        {
                                            //-----
                                            PasifRef.child(donekey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(getContext(), ad+" Silindi...", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        String hata=task.getException().toString();
                                                        Toast.makeText(getContext(), "hata: "+hata, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            //---------------
                                        }
                                        if (which==1)
                                        {
                                            Toast.makeText(getContext(), "Silme işlemi iptal edldi", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                        /////////////////

                        //aktifi pasif yapma
                        holder.itemView.findViewById(R.id.btn_does_aktif).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                HashMap<String,Object> EditMap=new HashMap<>();

                                EditMap.put("active",true);
                                final String ad=holder.doesTitle.getText().toString();
                                myRef.child(donekey).updateChildren(EditMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(getContext(), "Görev etkin", Toast.LENGTH_SHORT).show();

                                        }
                                        else
                                        {
                                            String eror=task.getException().toString();
                                            Toast.makeText(getContext(), "HATA: "+ eror, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }
            @NonNull
            @Override
            public PasifDoesItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view=LayoutInflater.from(getContext()).inflate(R.layout.done_does_item_layout,viewGroup,false);
                PasifDoesItemViewHolder viewHolder=new PasifDoesItemViewHolder(view);
                return viewHolder;
            }
        };
        mDoneList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class PasifDoesItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView doesTitle,doesDesc,doesDate;

        public PasifDoesItemViewHolder(@NonNull View itemView) {
            super(itemView);

            doesTitle=(TextView) itemView.findViewById(R.id.doesDoneTitle);
            doesDesc=(TextView) itemView.findViewById(R.id.doesDoneDescription);
            doesDate=(TextView) itemView.findViewById(R.id.doesDoneDate);
        }
    }
}
