package com.minhaz.muhammad.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    private EditText emailText,passwordText,usernameText;
    private ProgressBar progressBar;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();




        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.register_progress_bar);
        usernameText = findViewById(R.id.reg_username);
        emailText = findViewById(R.id.reg_email);
        passwordText = findViewById(R.id.reg_password);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null)
                {
                    //user already logged in
                }

                else
                {
                    //no user yet
                }
            }
        };

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(emailText.getText().toString())&&!TextUtils.isEmpty(passwordText.getText().toString()) &&
                !TextUtils.isEmpty(usernameText.getText().toString()))
                {
                    String email = emailText.getText().toString().trim();
                    String password = passwordText.getText().toString().trim();
                    String username = usernameText.getText().toString().trim();

                    createUserEmailAccount(email,password,username);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Empty fields are forbidden",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String username)
    {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username))
        {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {

                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();

                        Map<String, String> userObject = new HashMap<>();
                        userObject.put("userId", currentUserId);
                        userObject.put("username", username);


                        collectionReference.add(userObject).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (Objects.requireNonNull(task.getResult()).exists())
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = task.getResult().getString("username");

                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUserId(currentUserId);
                                            journalApi.setUsername(name);


                                            Intent intent = new Intent(RegisterActivity.this, PostJournalActivity.class);
                                            intent.putExtra("username",name);
                                            intent.putExtra("userId",currentUserId);
                                            startActivity(intent);

                                        }
                                        else{
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }

                    else
                    {
                        Toast.makeText(RegisterActivity.this,"Task has been failed",Toast.LENGTH_LONG).show();

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        else
        {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
