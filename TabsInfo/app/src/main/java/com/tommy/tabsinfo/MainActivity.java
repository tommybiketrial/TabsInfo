package com.tommy.tabsinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.theluckycoder.expandablecardview.ExpandableCardView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference itemRef;
    private TextView textTotalTabs, textAdminAccess;
    private RecyclerView recyclerMain;
    private ProgressBar loadingMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        toolbarSetup();
    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        itemRef = FirebaseDatabase.getInstance().getReference().child("TabsInfo");

        textTotalTabs = findViewById(R.id.textTotalTabs);
        textAdminAccess = findViewById(R.id.textAdminAccess);
        loadingMain = findViewById(R.id.loadingMain);

        Sprite doubleBounce = new DoubleBounce();
        loadingMain.setIndeterminateDrawable(doubleBounce);

        recyclerMain = findViewById(R.id.recyclerMain);
        recyclerMain.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getData() {

        loadingMain.setVisibility(View.VISIBLE);

        if(mAuth.getCurrentUser() == null)
            textAdminAccess.setText("家長介面");
        else
            textAdminAccess.setText("職員介面");

        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textTotalTabs.setText("Total Tabs: " + String.valueOf(snapshot.getChildrenCount()));

                if(snapshot.getChildrenCount() == 0) {

                    loadingMain.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getData();

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(itemRef, Item.class)
                        .build();

        FirebaseRecyclerAdapter<Item, MainActivity.ProductsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Item, MainActivity.ProductsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MainActivity.ProductsViewHolder holder, final int position, @NonNull Item model) {

                        if(position == 0) loadingMain.setVisibility(View.GONE);

                        holder.expandableCardView.setCardTitle(model.getTab());
                        holder.expandableCardView.setCardDescription(model.getInformation());
                    }

                    @NonNull
                    @Override
                    public MainActivity.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_user, parent, false);
                        return new MainActivity.ProductsViewHolder(view);
                    }
                };

        recyclerMain.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder{

        ExpandableCardView expandableCardView;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            expandableCardView = itemView.findViewById(R.id.expandableCardView);
        }
    }

    private void toolbarSetup() {

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.btnAdmin) {
            startActivity(new Intent(this, AdminActivity.class));
        }
        return true;
    }
}