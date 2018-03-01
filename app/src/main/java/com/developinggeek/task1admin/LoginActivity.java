package com.developinggeek.task1admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{

    private Button btnLogin;
    private EditText edtMail ,edtPass;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button)findViewById(R.id.login_btn);
        edtMail = (EditText)findViewById(R.id.login_edt_email);
        edtPass = (EditText)findViewById(R.id.login_edt_pass);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Admin");
        mProgress = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String mail = edtMail.getText().toString();
                final String pass = edtPass.getText().toString();

                if(!TextUtils.isEmpty(mail)||!TextUtils.isEmpty(pass))
                {
                    mProgress.setMessage("Signing in... ");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                Log.i("datasnapshot",snapshot.toString());

                                String mailCheck = snapshot.child("email").getValue().toString();
                                String passwordCheck = snapshot.child("password").getValue().toString();

                                Log.i("mail and pass", mailCheck+" "+passwordCheck);

                                if(mailCheck.equals(mail) && pass.equals(passwordCheck))
                                {
                                    isValid = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    if(isValid)
                    {

                        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful())
                                {
                                    mProgress.dismiss();
                                    Toast.makeText(LoginActivity.this, "Could not log you in.. check the email or password", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    mProgress.dismiss();
                                    Intent loginIntent = new Intent(LoginActivity.this , MainActivity.class);
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(loginIntent);
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Please Use Valid ", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "enter both the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
