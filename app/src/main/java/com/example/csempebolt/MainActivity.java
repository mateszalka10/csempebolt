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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogle;
    private static final String TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 69;
    private static final int RC_SIGN_IN = 420;

    EditText emailET;
    EditText jelszoET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailET = findViewById(R.id.BEmail);
        jelszoET = findViewById(R.id.Bjelszo);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogle = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestc, int resultc, Intent data){
        super.onActivityResult(requestc,resultc,data);

        if (requestc == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle: "+ account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch (ApiException e){
                Log.w(TAG, "Google sign in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "SignInWithCredential:success");
                startcsempelist();
            } else {
                Log.d(TAG, "SignInWithCredential:fail");
                Toast.makeText(MainActivity.this, "SignInWithCredential:fail" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void bejelentkezes(View view) {


        String jelszo = jelszoET.getText().toString();
        String email = emailET.getText().toString();

        //Log.i(TAG, "email: " + email + ", jelszó: " + jelszo);

        mAuth.signInWithEmailAndPassword(email,jelszo).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "User login success");
                startcsempelist();
            } else {
                Log.d(TAG, "User login fail");
                Toast.makeText(MainActivity.this, "User login fail" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
    public void startcsempelist(/*felhasználó*/){
        Intent intent = new Intent(this, CsempeListaActivity.class);
        startActivity(intent);
    }
    public void BRegiszt(View view) {
        Intent intent = new Intent(this, RegisztActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void BGoogle(View view) {
        Intent signI = mGoogle.getSignInIntent();
        startActivityForResult(signI, RC_SIGN_IN);
    }

    public void BAnonim(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "Anonym login success");
                startcsempelist();
            } else {
                Log.d(TAG, "Anonym login fail");
                Toast.makeText(MainActivity.this, "Anonym login fail" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}