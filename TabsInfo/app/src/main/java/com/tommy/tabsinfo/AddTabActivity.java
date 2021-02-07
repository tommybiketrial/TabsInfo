package com.tommy.tabsinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTabActivity extends AppCompatActivity {

    private DatabaseReference rootRef;
    private EditText inputTab, inputInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tab);

        initViews();
    }

    private void initViews() {

        rootRef = FirebaseDatabase.getInstance().getReference();

        inputTab = findViewById(R.id.inputTab);
        inputInformation = findViewById(R.id.inputInformation);

        findViewById(R.id.btnAddTab).setOnClickListener(v -> addTopic());
    }

    private void addTopic() {

        final String randomId = generateRandomId();

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

        rootRef.child("TabsInfo").child(randomId).setValue(item)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Toast.makeText(AddTabActivity.this, "Tab added", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(AddTabActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String generateRandomId() {

        long inSeconds = System.currentTimeMillis() / 10000;
        long negative = 999999999 - inSeconds;
        return String.valueOf(negative);
    }
}