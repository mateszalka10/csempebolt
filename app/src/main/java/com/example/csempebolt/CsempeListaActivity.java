package com.example.csempebolt;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private int querylimit=10;
    private AlarmManager mAlarmManager;
private NotificationHandler mNotificationHandler;

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

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerRecevier, filter);

        mNotificationHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setmAlarmManager();
    }

    BroadcastReceiver powerRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case Intent.ACTION_POWER_CONNECTED:
                    querylimit = 10;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    querylimit = 2;
                    break;
            }
            queryData();;
        }
    };

    private void queryData() {
        mItemList.clear();

        //mItems.whereEqualTo()....
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(querylimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemList.add(item);
            }
            if (mItemList.size()==0){
                intializeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    public void DeleteItem(ShoppingItem item) {
        DocumentReference ref = mItems.document(item._getId());

        ref.delete().addOnSuccessListener(success -> {
                    Log.d(TAG, "Sikeres törlés: " + item._getId());
        })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this,"Csempe " +item._getId()+ "sikertelen törlése", Toast.LENGTH_LONG).show();
                });
        queryData();
        mNotificationHandler.cancel();
    }


    private void intializeData() {
        String[] itemsList = getResources().getStringArray(R.array.csempe_nevek);
        String[] itemsInfo= getResources().getStringArray(R.array.csempe_leírások);
        String[] itemsPrice= getResources().getStringArray(R.array.csempe_arak);
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.csempe_kepek);

        // mItemList.clear();

        for (int i=0;i<itemsList.length;i++){
            mItems.add(new ShoppingItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPrice[i],
                    itemsImageResource.getResourceId(i,0),
                    0));
        }

        itemsImageResource.recycle();

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
    public void updateAlertIcon(ShoppingItem item) {
        cartItems = (cartItems+1);
        if (0<cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }
        blueCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount",item.getCartedCount()+1)
                .addOnFailureListener(failure -> {
                   Toast.makeText(this, "Csempe " +item._getId()+ "sikertelen frissítés",Toast.LENGTH_LONG).show();
                });
        mNotificationHandler.send(item.getName());
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerRecevier);
    }
    private void setmAlarmManager() {
        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
        Intent intent = new Intent(this, AlarmManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, pendingIntent);

        //mAlarmManager.cancel(pendingIntent);
    }
}