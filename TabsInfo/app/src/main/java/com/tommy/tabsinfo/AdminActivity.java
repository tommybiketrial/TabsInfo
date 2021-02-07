package com.tommy.tabsinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference itemRef;
    private RecyclerView recyclerAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initViews();
        toolbarSetup();
    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        itemRef = FirebaseDatabase.getInstance().getReference().child("TabsInfo");

        recyclerAdmin = findViewById(R.id.recyclerAdmin);
        recyclerAdmin.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddTabActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null) {

            finish();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            return;
        }

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(itemRef, Item.class)
                        .build();

        FirebaseRecyclerAdapter<Item, ProductsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Item, AdminActivity.ProductsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminActivity.ProductsViewHolder holder, final int position, @NonNull Item model) {

                        String id = getRef(position).getKey();

                        holder.textTab.setText(model.getTab());
                        holder.textInformation.setText(model.getInformation());
                        holder.imageDelete.setOnClickListener(v -> deleteItem(id));
                        holder.cardView.setOnClickListener(v -> editItem(id));
                    }

                    @NonNull
                    @Override
                    public AdminActivity.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_admin, parent, false);
                        return new AdminActivity.ProductsViewHolder(view);
                    }
                };

        recyclerAdmin.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView textTab, textInformation;
        ImageView imageDelete;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            textTab = itemView.findViewById(R.id.textTab);
            textInformation = itemView.findViewById(R.id.textInformation);
            imageDelete = itemView.findViewById(R.id.imageDelete);
        }
    }

    private void deleteItem(String itemId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete this post?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> itemRef.child(itemId).removeValue());

        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void editItem(String itemId) {

        Intent intent = new Intent(this, EditTabActivity.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    private void toolbarSetup() {

        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.btnLogout) {
            mAuth.signOut();
            finish();
        }
        return true;
    }
}