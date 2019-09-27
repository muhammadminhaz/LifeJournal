package com.minhaz.muhammad.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.JournalApi;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, registerButton;
    private AutoCompleteTextView emailAddress;
    private ProgressBar progressBar;
    private EditText passwordText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.login_progressBar);

        emailAddress = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        loginButton = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginEmailPasswordUser(emailAddress.getText().toString().trim(), passwordText.getText().toString().trim());
            }
        });
    }

    private void loginEmailPasswordUser(String email, String pwd) {
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)){

            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    String currentUserId = user.getUid();
                    collectionReference.whereEqualTo("userId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                    if(e!=null)
                                    {
                                    }
                                    assert queryDocumentSnapshots != null;
                                    if(!queryDocumentSnapshots.isEmpty())
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                        {
                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUsername(snapshot.getString("username"));
                                            journalApi.setUserId(snapshot.getString("userId"));
                                            startActivity(new Intent(LoginActivity.this, PostJournalActivity.class));
                                            finish();

                                        }

                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,"Please enter email and password",Toast.LENGTH_LONG).show();
        }
    }
}
