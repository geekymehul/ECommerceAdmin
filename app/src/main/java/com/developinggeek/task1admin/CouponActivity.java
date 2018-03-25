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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class CouponActivity extends AppCompatActivity
{

    private EditText edtName,edtDisc;
    private Button btnAdd;
    private DatabaseReference couponDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        edtName = (EditText)findViewById(R.id.coupon_edt_name);
        edtDisc = (EditText)findViewById(R.id.coupon_edt_discount);
        btnAdd = (Button)findViewById(R.id.coupon_btn_submit);

        couponDatabase = FirebaseDatabase.getInstance().getReference().child("Coupons");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCoupon();
            }
        });
    }

    private void addCoupon()
    {
        String name = edtName.getText().toString();
        String disc = edtDisc.getText().toString();

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(disc))
        {
            Toast.makeText(this, "enter all the fields!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            DatabaseReference coupon = couponDatabase.child(name);
            HashMap couponMap = new HashMap();
            couponMap.put("name",name);
            couponMap.put("discount",disc);
            couponMap.put("status","true");

            coupon.setValue(couponMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(CouponActivity.this, "Coupon Added", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(CouponActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
