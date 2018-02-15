package com.developinggeek.task1admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
{

    private Button btnLogin;
    private EditText edtMail ,edtPass;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button)findViewById(R.id.login_btn);
        edtMail = (EditText)findViewById(R.id.login_edt_email);
        edtPass = (EditText)findViewById(R.id.login_edt_pass);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String mail = edtMail.getText().toString();
                String pass = edtPass.getText().toString();

                if(!TextUtils.isEmpty(mail)||!TextUtils.isEmpty(pass))
                {
                    mProgress.setMessage("Signing in... ");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

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
                    Toast.makeText(LoginActivity.this, "enter both the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
