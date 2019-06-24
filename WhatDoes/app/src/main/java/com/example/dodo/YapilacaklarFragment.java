package com.example.dodo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class YapilacaklarFragment extends Fragment {
    private View YapView;

    private Toolbar mToolbar;
    private RecyclerView mylist;
    private Button btn_card_add,btn_card_save,btn_card_cancel,calender_btn,btn_calender,btn_does_edit,btn_does_delete,btn_Edit_Update,btn_does_pasif;
    private LinearLayout layout_card_show,layout_calender;
    private Animation mAnimation,backanim;
    private EditText DoesTitle,DoesDecs,DoesDate;
    private CalendarView mCalender;
    private TextView yeniGorev;
    private int i=0;



    String keydoes ;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference DoesRef,RetDoesRef,DelDoesRef,PasifRef,myRef,myToRef;
    private ProgressDialog mDialog;

    public YapilacaklarFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        YapView= inflater.inflate(R.layout.fragment_yapilacaklar, container, false);

        //-------------------------------------


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        DoesRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);
        RetDoesRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);
        DelDoesRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);
        PasifRef= FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);

        myRef=FirebaseDatabase.getInstance().getReference().child("Does").child(currentUserID);

        btn_does_pasif=(Button) YapView.findViewById(R.id.btn_does_pasif);
        Tanimlamalar();
        mylist=(RecyclerView) YapView.findViewById(R.id.mylist);
        mylist.setLayoutManager(new LinearLayoutManager(getContext()));

        btn_card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GorevEkleme();
            }
        });
        btn_card_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { GorevIptal();MakeEmptyEditTexts(); }});
        calender_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { layout_calender.setVisibility(View.VISIBLE); }});
        mCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange( CalendarView view, int year, int month, int dayOfMonth) {
                String date=dayOfMonth+"/" +(month+1)+"/" +year;
                DoesDate.setText(date);
            }
        });
        btn_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { layout_calender.setVisibility(View.INVISIBLE); }});

        btn_card_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setDoesFirebase(); GorevTamam(); MakeEmptyEditTexts(); }});

        return YapView;
    }



    @Override
    public void onStart() {
        super.onStart();

        final Query myQuery =myRef.getRef().orderByChild("active").equalTo(true);
        FirebaseRecyclerOptions<Does> options=
                new FirebaseRecyclerOptions.Builder<Does>()
                        .setQuery(myQuery,Does.class)
                        .build();

        FirebaseRecyclerAdapter<Does, DoesItemViewHolder> adapter= new FirebaseRecyclerAdapter<Does, DoesItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final DoesItemViewHolder holder, final int position, @NonNull final Does model) {

                        final String userRand=getRef(position).getKey();

                        final String a=myRef.child(userRand).child("active").toString();


                        //bence biz burada veriyi alamıyoruz
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange( DataSnapshot dataSnapshot) {


                                    holder.doesTitle.setText(model.getTitle());
                                    holder.doesDesc.setText(model.getDesc());
                                    holder.doesDate.setText(model.getDate());




                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (i%2==0)
                                        {
                                            holder.itemView.findViewById(R.id.btn_does_delete).setVisibility(View.VISIBLE);
                                            holder.itemView.findViewById(R.id.btn_does_edit).setVisibility(View.VISIBLE);
                                            holder.itemView.findViewById(R.id.btn_does_pasif).setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                            holder.itemView.findViewById(R.id.btn_does_delete).setVisibility(View.INVISIBLE);
                                            holder.itemView.findViewById(R.id.btn_does_edit).setVisibility(View.INVISIBLE);
                                            holder.itemView.findViewById(R.id.btn_does_pasif).setVisibility(View.INVISIBLE);
                                        }
                                        i++;
                                        if (i==8)
                                        {
                                            i=0;
                                        }
                                    }
                                });

                                final String ad=holder.doesTitle.getText().toString();

                                holder.itemView.findViewById(R.id.btn_does_delete).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                CharSequence options[]= new CharSequence[]
                                 {
                                                                ad +" Sil",
                                                                " İptal"
                                  };
                                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle(" Görevi yaptığına göre , Artık silelim mi?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which==0)
                                        {
                                                            //-----
                                            RetDoesRef.child(userRand).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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


                                //aktifi pasif yapma
                                holder.itemView.findViewById(R.id.btn_does_pasif).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        HashMap<String,Object> EditMap=new HashMap<>();
                                        EditMap.put("active",false);
                                        final String ad=holder.doesTitle.getText().toString();
                                        myRef.child(userRand).updateChildren(EditMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(getContext(), "Tebrikler, Görevi tamamladın", Toast.LENGTH_SHORT).show();

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
                                //aktif bitiş




                                holder.itemView.findViewById(R.id.btn_does_edit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String EditTitle=holder.doesTitle.getText().toString();
                                        final String EditDesc=holder.doesDesc.getText().toString();
                                        final String EditDate=holder.doesDate.getText().toString();
                                        GorevGuncelle();
                                        DoesTitle.setText(EditTitle);
                                        DoesDecs.setText(EditDesc);
                                        DoesDate.setText(EditDate);

                                        btn_Edit_Update.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                String Editdoestitle=DoesTitle.getText().toString();
                                                String Editdoesdesc=DoesDecs.getText().toString();
                                                String Editdoesdate=DoesDate.getText().toString();

                                                if (TextUtils.isEmpty(Editdoestitle))
                                                {
                                                    Toast.makeText(getContext(), "Başlık alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
                                                }
                                                if (TextUtils.isEmpty(Editdoesdesc))
                                                {
                                                    Toast.makeText(getContext(), "Açıklama alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
                                                }
                                                if (TextUtils.isEmpty(Editdoesdate))
                                                {
                                                    Toast.makeText(getContext(), "Tarih alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                    {
                                                        HashMap<String,Object> EditMap=new HashMap<>();
                                                        EditMap.put("title",Editdoestitle);
                                                        EditMap.put("desc",Editdoesdesc);
                                                        EditMap.put("date",Editdoesdate);
                                                        EditMap.put("key",userRand);

                                                        RetDoesRef.child(userRand).updateChildren(EditMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Toast.makeText(getContext(), "Güncelleme başarılı", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else
                                                                    {
                                                                        String eror=task.getException().toString();
                                                                        Toast.makeText(getContext(), "HATA: "+ eror, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        //else bitişi

                                                        GorevTamam();
                                                        MakeEmptyEditTexts();
                                                    }
                                                });
                                                //güncelle butonu bitişi
                                            }
                                        });
                                        //edit butonu bitişi

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public DoesItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_does_layout,viewGroup,false);
                        DoesItemViewHolder viewHolder=new DoesItemViewHolder(view);
                        return viewHolder;
                    }
                };
        mylist.setAdapter(adapter);
        adapter.startListening();
    }

    public static class DoesItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView doesTitle,doesDesc,doesDate;

        public DoesItemViewHolder(@NonNull View itemView) {
            super(itemView);

            doesTitle=(TextView) itemView.findViewById(R.id.doesTitle);
            doesDesc=(TextView) itemView.findViewById(R.id.doesDescription);
            doesDate=(TextView) itemView.findViewById(R.id.doesDate);
        }
    }

    private void MakeEmptyEditTexts()
    {
        DoesTitle.setText("");
        DoesDecs.setText("");
        DoesDate.setText("");
    }
    private void GorevIptal()
    {

        layout_card_show.startAnimation(backanim);
        btn_card_add.setVisibility(View.VISIBLE);
        mylist.setVisibility(View.VISIBLE);
        layout_card_show.setAlpha(1);
        layout_card_show.setVisibility(View.INVISIBLE);
    }
    private void GorevTamam()
    {
        layout_card_show.startAnimation(backanim);
        btn_card_add.setVisibility(View.VISIBLE);
        mylist.setVisibility(View.VISIBLE);
        layout_card_show.setAlpha(1);
        layout_card_show.setVisibility(View.INVISIBLE);
    }
    private void GorevEkleme()
    {
        layout_card_show.setAlpha(1);
        layout_card_show.startAnimation(mAnimation);
        btn_card_add.setVisibility(View.INVISIBLE);
        mylist.setVisibility(View.INVISIBLE);
        DoesTitle.setVisibility(View.VISIBLE);
        DoesDecs.setVisibility(View.VISIBLE);
        DoesDate.setVisibility(View.VISIBLE);
        btn_card_save.setVisibility(View.VISIBLE);
        btn_Edit_Update.setVisibility(View.INVISIBLE);
        layout_card_show.setVisibility(View.VISIBLE);
    }
    private void GorevGuncelle()
    {
        layout_card_show.setAlpha(1);
        layout_card_show.startAnimation(mAnimation);
        btn_card_add.setVisibility(View.INVISIBLE);
        mylist.setVisibility(View.INVISIBLE);
        DoesTitle.setVisibility(View.VISIBLE);
        DoesDecs.setVisibility(View.VISIBLE);
        DoesDate.setVisibility(View.VISIBLE);
        btn_Edit_Update.setVisibility(View.VISIBLE);
        btn_card_save.setVisibility(View.INVISIBLE);
        layout_card_show.setVisibility(View.VISIBLE);
        yeniGorev.setText("Görevi Güncelle");
    }
    private void Tanimlamalar()
    {
        btn_card_add=YapView.findViewById(R.id.btn_card_add);
        btn_card_save=YapView.findViewById(R.id.btn_card_save);
        btn_card_cancel=YapView.findViewById(R.id.btn_card_cancel);
        layout_card_show=YapView.findViewById(R.id.layout_card_show);
        DoesTitle=YapView.findViewById(R.id.doesTitle);
        DoesDecs=YapView.findViewById(R.id.doesDescription);
        DoesDate=YapView.findViewById(R.id.doesDate);
        calender_btn=YapView.findViewById(R.id.calender_btn);
        layout_calender=YapView.findViewById(R.id.layout_calender);
        mCalender=YapView.findViewById(R.id.mCalender);
        btn_calender=YapView.findViewById(R.id.btn_calender);
        btn_does_edit=YapView.findViewById(R.id.btn_does_edit);
        btn_does_delete=YapView.findViewById(R.id.btn_does_delete);
        yeniGorev=YapView.findViewById(R.id.yeniGorev);
        btn_Edit_Update=YapView.findViewById(R.id.btn_Edit_Update);
        mAnimation= AnimationUtils.loadAnimation(getContext(),R.anim.animasyon);
        backanim= AnimationUtils.loadAnimation(getContext(),R.anim.backanimation);
        layout_card_show.setAlpha(0);
    }
    private void setDoesFirebase()
    {
        String doestitle=DoesTitle.getText().toString();
        String doesdesc=DoesDecs.getText().toString();
        String doesdate=DoesDate.getText().toString();

        if (TextUtils.isEmpty(doestitle))
        {
            Toast.makeText(getContext(), "Başlık alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(doesdesc))
        {
            Toast.makeText(getContext(), "Açıklama alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(doesdate))
        {
            Toast.makeText(getContext(), "Tarih alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String key=DoesRef.push().getKey();

            HashMap<String,Object> profileMap=new HashMap<>();
            profileMap.put("title",doestitle);
            profileMap.put("desc",doesdesc);
            profileMap.put("date",doesdate);
            profileMap.put("key",key);
            profileMap.put("active",true);

            DatabaseReference RootDoes=DoesRef;
            RootDoes.child(key).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getContext(), "Görev kaydı başarılı", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        String eror=task.getException().toString();
                        Toast.makeText(getContext(), "HATA: "+ eror, Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }

}
