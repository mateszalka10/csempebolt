package com.example.csempebolt;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.example.csempebolt.R;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CsempeListaActivity extends AppCompatActivity {
    private static final String TAG = CsempeListaActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemList;
    private ShoppingItemAdapter mAdapter;
    private int gridNumber=1;

    private FrameLayout blueCircle;
    private TextView contentTextView;
    private int cartItems = 0;
    private boolean viewRow = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_csempe_lista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Authenticated user! ");
        } else {
            Log.d(TAG, "Unauthenticated user! ");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mAdapter = new ShoppingItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mAdapter);

        intializeData();
    }

    private void intializeData() {
        String[] itemsList = getResources().getStringArray(R.array.csempe_nevek);
        String[] itemsInfo= getResources().getStringArray(R.array.csempe_leírások);
        String[] itemsPrice= getResources().getStringArray(R.array.csempe_arak);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.csempe_kepek);

        mItemList.clear();

        for (int i=0;i<itemsList.length;i++){
            mItemList.add(new ShoppingItem(itemsList[i], itemsInfo[i], itemsPrice[i],itemsImageResource.getResourceId(i,0)));
        }

        itemsImageResource.recycle();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.kereso);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.kijelentkezes) {
            Log.d(TAG, "kijelentkezés");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (id == R.id.beallitasok) {
            Log.d(TAG, "beállítások");
            return true;
        } else if (id == R.id.kosar) {
            Log.d(TAG, "kosár");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.kosar);
        FrameLayout rootView =(FrameLayout) alertMenuItem.getActionView();

        blueCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);



    }
    public void updateAlertIcon() {
        cartItems = (cartItems+1);
        if (0<cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }
        blueCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }
}