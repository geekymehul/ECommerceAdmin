package com.developinggeek.task1admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    public static Activity fa;
    private FirebaseAuth mAuth;
    private EditText edtTitle , edtDesc , edtPrice , edtStatus;
    private TextView tvSubCat;
    private Button btnImg ,btnUpload ,btnImg2 ;
    private ImageButton imgView , imgView2;
    private DatabaseReference mDatabase,catDatabase;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private Uri mImageUri = null , mImageUri2 = null;
    private String category,subCategory;
    private Spinner spinner,subSpinner;
    private Toolbar mToolbar;
    private ArrayList<String> subArraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fa = this;

        edtTitle = (EditText)findViewById(R.id.main_edt_title);
        edtDesc = (EditText)findViewById(R.id.main_edt_desc);
        edtPrice = (EditText)findViewById(R.id.main_edt_price);
        edtStatus = (EditText)findViewById(R.id.main_edt_stock);
        btnImg = (Button)findViewById(R.id.main_img_btn);
        imgView = (ImageButton)findViewById(R.id.main_image);
        btnUpload = (Button)findViewById(R.id.main_btn_upload);
        spinner = (Spinner)findViewById(R.id.main_spinner);
        btnImg2 = (Button)findViewById(R.id.main_img_btn_2);
        imgView2 = (ImageButton)findViewById(R.id.main_image2);
        mToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        tvSubCat = (TextView)findViewById(R.id.main_edt_subcategory);
        subSpinner = (Spinner)findViewById(R.id.main_subcat_spinner);

        setSupportActionBar(mToolbar);

        mProgress = new ProgressDialog(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        catDatabase = FirebaseDatabase.getInstance().getReference().child("Category");
        mStorage = FirebaseStorage.getInstance().getReference();
        subArraylist = new ArrayList<String>();

        subArraylist.add("select one");

        if(mAuth.getCurrentUser()==null)
        {
            Intent loginIntent = new Intent(MainActivity.this , LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }

        else
        {
            spinner.setOnItemSelectedListener(this);

            // set up the list for spinner
            List<String> categories = new ArrayList<String>();
            categories.add("Sensors");
            categories.add("Embedded");
            categories.add("Do it Yourself");
            categories.add("Basic Components");
            categories.add("Robotics");
            categories.add("Tools");
            categories.add("PCB Request");
            categories.add("Contact us");

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
            // Drop down layout style - list view with radio button
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter2);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subArraylist);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            subSpinner.setAdapter(dataAdapter);


            subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {}

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });


            btnImg.setOnClickListener(new View.OnClickListener() {
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

            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    upload();
                }
            });

        }

    }

    private void upload()
    {

        final String title = edtTitle.getText().toString();
        final String desc = edtDesc.getText().toString();
        final String price = edtPrice.getText().toString();
        final String status = edtStatus.getText().toString();
        subCategory = subSpinner.getSelectedItem().toString();

        tvSubCat.setText(subCategory+"");

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(status)
           && !TextUtils.isEmpty(subCategory) && !TextUtils.isEmpty(category) && mImageUri!=null &&mImageUri2!=null)
        {

            mProgress.setTitle("Uploading....");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            StorageReference filepath = mStorage.child(title).child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                // Uploading First Image
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    @SuppressWarnings("VisibleForTests") final Uri imgUri = taskSnapshot.getDownloadUrl();

                    StorageReference filepath2 = mStorage.child("blog_images").child(mImageUri2.getLastPathSegment());
                    filepath2.putFile(mImageUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        // Uploading Second Image
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {

                            @SuppressWarnings("VisibleForTests") final Uri imgUri2 = taskSnapshot.getDownloadUrl();

                            DatabaseReference productDB = mDatabase.child(category).child(title);

                            HashMap productMap = new HashMap();
                            productMap.put("title",title);
                            productMap.put("description",desc);
                            productMap.put("price","₹ "+price);
                            productMap.put("status",status);
                            productMap.put("category",category);
                            productMap.put("subcategory",subCategory);
                            productMap.put("image",imgUri.toString());
                            productMap.put("image2",imgUri2.toString());
                            productDB.setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "product uploaded", Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                    } else {
                                        Toast.makeText(MainActivity.this, "error couldnot be uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });

                        }
                    });

                }

            });
        }
        else
        {
            Toast.makeText(this, "please enter all the fields!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.main_menu_logout : mAuth.signOut();
                                         startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                         break;

            case R.id.main_menu_add_aubcategory : startActivity(new Intent(MainActivity.this,AddSubCategoryActivity.class));
                                                  break;

            case R.id.main_menu_search : startActivity(new Intent(MainActivity.this,EditProductActivity.class));
                                         break;

            case R.id.main_menu_coupon : startActivity(new Intent(MainActivity.this,CouponActivity.class));
                                         break;

            case R.id.main_menu_update_coupon : startActivity(new Intent(MainActivity.this,UpdateCouponActivity.class));
                                                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK)
        {
            mImageUri = data.getData();
            imgView.setImageURI(mImageUri);
        }

        if(requestCode==2 && resultCode==RESULT_OK)
        {
            mImageUri2 = data.getData();
            imgView2.setImageURI(mImageUri2);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
            subArraylist.clear();

            category = adapterView.getItemAtPosition(i).toString();
            Log.i("spinner"," category : "+category);

            DatabaseReference subDatabase = catDatabase.child(category);
            subDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Log.i("snapshot",snapshot.toString());

                        String subcat = snapshot.child("name").getValue().toString();
                        Log.i("subcat",subcat);
                        subArraylist.add(subcat);
                    }

                    Log.i("arraylist",subArraylist.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /*// Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, subArraylist);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            subSpinner.setAdapter(dataAdapter);*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

}
