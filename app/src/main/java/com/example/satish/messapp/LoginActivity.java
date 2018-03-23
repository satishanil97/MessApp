package com.example.satish.messapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "LoginActivity";

    //UI references
    private EditText mEmail,mPassword;
    Button btnSignIn, btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //auto-generated -- this function works when the Activity is spawned
        super.onCreate(savedInstanceState); //auto-generated
        setContentView(R.layout.activity_login);    //auto-generated

        //declare buttons and AutoCompleteTextViews in onCreate
        //UI elements and layout for this activity is specified in res/layout/activity_login.xml
        mEmail = (EditText) findViewById(R.id.login_email); //The Editable textfield for entering email
        mPassword = (EditText) findViewById(R.id.login_password);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnForgotPassword = (Button) findViewById(R.id.forgot_password_button);

        mAuth = FirebaseAuth.getInstance(); //Reference of the Firebase instance

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //Listens for click of Sign-In button and takes this action
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!email.equals("") && !password.equals("")) {
                    SignIn(email,password);
                }
                else {
                    toastMessage("Fill all the fields",0);  //Shows a pop-up with a message
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                if(!email.equals("")) {
                    ForgotPassword(email);
                }
                else {
                    toastMessage("Fill Email",0);
                }
            }
        });
    }

    @Override
    public void onStart() { //auto-generated
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void ForgotPassword(String email) {

        mAuth.sendPasswordResetEmail(email)         //inbuilt function in firebase to send password reset email
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            toastMessage("Password Reset Email sent",0);
                        }
                        else {
                            Log.d(TAG, "Email verification couldn't be sent");
                            toastMessage("Internet Problem / Unidentified User",0);
                        }
                    }
                });
    }

    private void SignIn(final String email, String password) {  //To Handle Sign-In
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser(); //gets cuurently logged in user
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            toastMessage("Login Failed",0);
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.login_layout);
            ll.setAlpha((float) 0.4);
            mEmail.setFocusable(false);
            mPassword.setFocusable(false);
            btnSignIn.setClickable(false);
            btnForgotPassword.setClickable(false);
            toastMessage("... Signing in ...",0);
            // email address
            final String email = user.getEmail();

            // Check if user's email is verified
            //boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            //String uid = user.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference();
            // Read from the database
            myRef.addListenerForSingleValueEvent(new ValueEventListener() { //Inbuilt firebase function which is called once when a change occurs in the database
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {   //auto-generated
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    int contractor = 0;
                    StringBuilder contractorId,rollNo,messId;
                    contractorId = new StringBuilder("");
                    messId = new StringBuilder("");
                    rollNo = new StringBuilder("");
                    //the data is stored in NoSQL form ~ like JSON format...SO it's in key value pairs, which forms a heirarchichal structure
                    for(DataSnapshot ds : dataSnapshot.child("users").child("contractor").getChildren()){   //datasnapshot stores the state of the database at that point
                        String emailID = (String) ds.getValue();
                        if(emailID != null && email.equals(emailID)) {
                            contractor = 1; //if the email is found under contractor branch, then then user is a contractor
                            for(DataSnapshot ds2 : dataSnapshot.child("contractors").getChildren()){
                                String emailID2 = (String) ds2.child("Email").getValue();
                                if(emailID2 != null && email.equals(emailID2)) {
                                    contractorId.append((String) ds2.child("Cid").getValue());
                                    messId.append((String) ds2.child("MessID").getValue());
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    if(contractor == 1) {   //if current user is contractor, we initiate contractor Activity
                        Intent intent = new Intent(LoginActivity.this,ContractorMain.class);
                        String Cid = new String(contractorId.toString());
                        String messID = new String(messId.toString());
                        intent.putExtra("Cid",Cid); //pass Cid to Contractor Activity
                        intent.putExtra("messID",messID);
                        startActivity(intent);  //start Contractor Activity
                        finish();   //exit cuurent activity => Login Activity is finished
                    }

                    else {  //student activity to be completed
                        toastMessage("student",0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {  //auto-generated
                    // Failed to read value
                    toastMessage("Database Error",0);
                    Log.w(TAG, "Database error", error.toException());
                }
            });
        }
    }

    private void toastMessage(String message, int length) { //takes a string and shows a pop-up with it as the message
        if(length == 0) //length is used to represent whether the pop-up should stay for a short while(0) or long(1).
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }
}
