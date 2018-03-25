package com.developinggeek.task1admin;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateCouponActivity extends AppCompatActivity
{
    private EditText edtName,edtDisc,edtStatus;
    private Button btnSearch,btnUpdate;
    private DatabaseReference couponDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_coupon);

        edtName = (EditText)findViewById(R.id.update_coupon_edt_name);
        edtDisc = (EditText)findViewById(R.id.update_coupon_edt_disc);
        edtStatus = (EditText)findViewById(R.id.update_coupon_edt_status);
        btnSearch = (Button)findViewById(R.id.update_coupon_btn_search);
        btnUpdate = (Button)findViewById(R.id.update_coupon_btn_update);

        couponDatabase = FirebaseDatabase.getInstance().getReference().child("Coupons");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCoupon();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCoupon();
            }
        });
    }

    private void updateCoupon()
    {
        String disc = edtDisc.getText().toString();
        String status = edtStatus.getText().toString();
        String name = edtName.getText().toString();

        if(!TextUtils.isEmpty(disc)&&!TextUtils.isEmpty(status)&&!TextUtils.isEmpty(status))
        {
            if(status.equalsIgnoreCase("false") || status.equalsIgnoreCase("true"))
            {
                DatabaseReference coupon = couponDatabase.child(name);
                coupon.child("discount").setValue(disc);
                coupon.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UpdateCouponActivity.this, "Coupon Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "status should be either true or false", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "enter all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchCoupon()
    {
        String name = edtName.getText().toString();

        if(!TextUtils.isEmpty(name))
        {
            DatabaseReference coupon = couponDatabase.child(name);

            coupon.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    edtDisc.setText(dataSnapshot.child("discount").getValue().toString());
                    edtStatus.setText(dataSnapshot.child("status").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(this, "enter the name of coupon", Toast.LENGTH_SHORT).show();
        }

    }

}
