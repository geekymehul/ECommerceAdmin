package com.developinggeek.task1admin;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity
{
    private EditText edtProductName,edtName,edtSubcat,edtPrice,edtDesc,edtStatus,edtCat;
    private Button btnOK,btnUpdate,btnImg1,btnImg2;
    private DatabaseReference mDatabase;
    private ImageView img1,img2;
    private boolean bool1=false,bool2=false;
    private Uri imgUri1,imgUri2;
    private String uri1,uri2;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        edtProductName = (EditText)findViewById(R.id.edit_edt_name);
        edtSubcat = (EditText)findViewById(R.id.edit_subcategory);
        edtName = (EditText)findViewById(R.id.edit_name);
        edtCat = (EditText)findViewById(R.id.edit_category);
        edtPrice = (EditText)findViewById(R.id.edit_price);
        edtDesc = (EditText)findViewById(R.id.edit_desc);
        edtStatus = (EditText)findViewById(R.id.edit_stock);
        btnOK = (Button)findViewById(R.id.edit_btn_search);
        btnUpdate = (Button)findViewById(R.id.edit_update);
        img1 = (ImageView)findViewById(R.id.edit_img1);
        img2 = (ImageView)findViewById(R.id.edit_img2);
        btnImg1 = (Button)findViewById(R.id.edit_btn_img1);
        btnImg2 = (Button)findViewById(R.id.edit_btn_img2);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mStorage = FirebaseStorage.getInstance().getReference();

        btnOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String category = edtCat.getText().toString();
                String name = edtProductName.getText().toString();

                if(!TextUtils.isEmpty(category)&&!TextUtils.isEmpty(name))
                {
                    DatabaseReference productDatabase = mDatabase.child(category).child(name);

                    productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            edtName.setText(dataSnapshot.child("title").getValue().toString());
                            edtPrice.setText(dataSnapshot.child("price").getValue().toString());
                            edtDesc.setText(dataSnapshot.child("description").getValue().toString());
                            edtStatus.setText(dataSnapshot.child("status").getValue().toString());
                            edtSubcat.setText(dataSnapshot.child("subcategory").getValue().toString());
                            Picasso.with(EditProductActivity.this).load(dataSnapshot.child("image").getValue().toString()).into(img1);
                            Picasso.with(EditProductActivity.this).load(dataSnapshot.child("image2").getValue().toString()).into(img2);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(EditProductActivity.this, "enter all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 1);
            }
        });

        btnImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String name = edtName.getText().toString();
                String subcat = edtSubcat.getText().toString();
                String price = edtPrice.getText().toString();
                String desc = edtDesc.getText().toString();
                String status = edtStatus.getText().toString();

                if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(subcat)&&!TextUtils.isEmpty(price)&&!TextUtils.isEmpty(desc)
                        &&!TextUtils.isEmpty(status))
                {
                    String category = edtCat.getText().toString();
                    String pname = edtProductName.getText().toString();

                    final DatabaseReference productDatabase = mDatabase.child(category).child(pname);
                    productDatabase.child("title").setValue(name);
                    productDatabase.child("subcategory").setValue(subcat);
                    productDatabase.child("status").setValue(status);
                    productDatabase.child("price").setValue(price);
                    productDatabase.child("description").setValue(desc);

                    if(bool1)
                    {
                        StorageReference filepath = mStorage.child(name).child(imgUri1.getLastPathSegment());
                        filepath.putFile(imgUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                @SuppressWarnings("VisibleForTests") final Uri downUri1 = taskSnapshot.getDownloadUrl();
                                uri1 = downUri1.toString();
                                Log.i("uri1",uri1);
                                productDatabase.child("image").setValue(uri1);
                            }
                        });
                    }

                    if(bool2)
                    {
                        StorageReference filepath = mStorage.child(name).child(imgUri2.getLastPathSegment());
                        filepath.putFile(imgUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                @SuppressWarnings("VisibleForTests") final Uri downUri2 = taskSnapshot.getDownloadUrl();
                                uri2 = downUri2.toString();
                                Log.i("uri2",uri2);
                                productDatabase.child("image2").setValue(uri2);
                            }
                        });
                    }

                }

                else
                {
                    Toast.makeText(EditProductActivity.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1 && resultCode==RESULT_OK)
        {
            bool1 = true;
            imgUri1 = data.getData();
            img1.setImageURI(imgUri1);
        }

        if(requestCode==2 && resultCode==RESULT_OK)
        {
            bool2 = true;
            imgUri2 = data.getData();
            img2.setImageURI(imgUri2);
        }

        Log.i("bool","1 "+bool1+" 2 "+bool2);

        super.onActivityResult(requestCode, resultCode, data);
    }

}
