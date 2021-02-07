package com.tommy.tabsinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTabActivity extends AppCompatActivity {

    private String itemId;
    private DatabaseReference rootRef;
    private EditText inputTab, inputInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tab);

        itemId = getIntent().getExtras().getString("itemId");

        initViews();
        getData();
    }

    private void initViews() {

        rootRef = FirebaseDatabase.getInstance().getReference();

        inputTab = findViewById(R.id.inputTab);
        inputInformation = findViewById(R.id.inputInformation);

        findViewById(R.id.btnEditTab).setOnClickListener(v -> editTopic());
    }

    private void getData() {

        rootRef.child("TabsInfo").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Item item = snapshot.getValue(Item.class);
                inputTab.setText(item.getTab());
                inputInformation.setText(item.getInformation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editTopic() {

        String tab = inputTab.getText().toString();
        String information = inputInformation.getText().toString();

        if (tab.isEmpty()) {
            inputTab.setError("Tab is required");
            inputTab.requestFocus();
            return;
        }

        if (information.isEmpty()) {
            inputInformation.setError("Information is required");
            inputInformation.requestFocus();
            return;
        }

        Item item = new Item(tab, information);

        rootRef.child("TabsInfo").child(itemId).setValue(item)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Toast.makeText(EditTabActivity.this, "Tab edited", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(EditTabActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}