package com.gorkemozdemir.supermarketapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductList extends AppCompatActivity {
    ElegantNumberButton numberButton;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    ArrayList<String> Name;
    ArrayList<String> Number;
    private DocumentReference documentReference,documentReference1;
    ArrayList<String> Money;
    ProductHolder productHolder;
    RecyclerView recyclerVieww;
    String UserID;



    int realmoney;
    int MONEY;
    int rsayac;


    String productName;
    String productMoney;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Name = new ArrayList<>();
        Number = new ArrayList<>();
        Money = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerVieww = findViewById(R.id.rcyc1);
        recyclerVieww.setLayoutManager(new LinearLayoutManager(this));
        productHolder = new ProductHolder(Name, Money, Number);
        new ItemTouchHelper(item).attachToRecyclerView(recyclerVieww);
        recyclerVieww.setAdapter(productHolder);
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("meyve")) {
            getDataF("Meyve");
        } else if (info.matches("sebze")) {
            getDataF("Sebze");
        } else if (info.matches("et")) {
            getDataF("Et");
        } else if (info.matches("balık")) {
            getDataF("Balik");
        } else if (info.matches("kahvaltı")) {
            getDataF("Kahvaltılık");
        } else if (info.matches("süt")) {
            getDataF("Sut");
        }




    }


    public void getDataF(String Tur) {

        CollectionReference collectionReference = firebaseFirestore.collection(Tur);
        collectionReference.orderBy("Money", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(ProductList.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String Namee = (String) data.get("Name");
                        String Imagee = (String) data.get("Url");
                        String Moneyy = (String) data.get("Money");



                        Name.add(Namee);
                        Money.add(Moneyy);
                        Number.add(Imagee);


                        productHolder.notifyDataSetChanged();

                    }
                }
            }
        });


    }


    ItemTouchHelper.SimpleCallback item = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            UserID = firebaseAuth.getCurrentUser().getUid();



            //gelen parayı int'e çeviriyoruz
            realmoney = Integer.parseInt(Money.get(viewHolder.getAdapterPosition()));



            String url = Number.get(viewHolder.getAdapterPosition());
            productName = Name.get(viewHolder.getAdapterPosition());
            productMoney = Money.get(viewHolder.getAdapterPosition());
            productHolder.notifyDataSetChanged();

            HashMap<String, Object> postData = new HashMap<>();
            postData.put("productName", productName);
            postData.put("productMoney",realmoney);
            postData.put("date", FieldValue.serverTimestamp());
            postData.put("downloadUrl",url);
            postData.put("productNumber", "1");


            documentReference = firebaseFirestore.collection(UserID).document(productName);
            documentReference.set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProductList.this,"Ürün başarıyla sepetinize eklendi",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProductList.this,"Hata oluştu daha sonra tekrar deneyiniz",Toast.LENGTH_SHORT).show();

                }
            });

        }



    };



}













