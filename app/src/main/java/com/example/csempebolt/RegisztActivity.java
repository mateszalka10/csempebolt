package com.example.csempebolt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisztActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = RegisztActivity.class.getName();
    EditText NevET;
    EditText EmailET;
    EditText JelszoET;
    EditText TelET;
    EditText CimET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regiszt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int secret_key = getIntent().getIntExtra("SECRET_KEY",0);

        if(secret_key != 69) finish();

        NevET = findViewById(R.id.RNev);
        EmailET = findViewById(R.id.REmail);
        JelszoET = findViewById(R.id.RJelszo);
        TelET = findViewById(R.id.RTel);
        CimET = findViewById(R.id.RCim);
        mAuth = FirebaseAuth.getInstance();
    }

    public void RMegse(View view) {
        finish();
    }

    public void RRegiszt(View view) {
        String jelszo = JelszoET.getText().toString();
        String email = EmailET.getText().toString();
        String nev = NevET.getText().toString();
        String tel = TelET.getText().toString();
        String cim = CimET.getText().toString();

        Log.i(TAG, "A Regisztrált adatok:  " + nev + email + jelszo + tel + cim);

        mAuth.createUserWithEmailAndPassword(email, jelszo).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "User created successfully");
                startcsempelist();
            } else {
                Log.d(TAG, "User wasn't created siccesfully");
                Toast.makeText(RegisztActivity.this, "User wasn't created siccesfully" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void startcsempelist(/*felhasználó*/){
        Intent intent = new Intent(this, CsempeListaActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}