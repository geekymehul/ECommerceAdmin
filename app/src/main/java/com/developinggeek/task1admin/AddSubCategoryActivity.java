package com.developinggeek.task1admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddSubCategoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    private EditText edtSubCat;
    private Button btnSubmit;
    private Spinner spinner;
    private DatabaseReference mDatabase;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_category);

        edtSubCat = (EditText)findViewById(R.id.subcat_edt_name);
        btnSubmit = (Button)findViewById(R.id.subcat_btn_submit);
        spinner = (Spinner)findViewById(R.id.subcat_spinner);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Category");

        List<String> categories = new ArrayList<String>();
        categories.add("category 1");
        categories.add("category 2");
        categories.add("category 3");
        categories.add("category 4");
        categories.add("category 5");
        categories.add("category 6");
        categories.add("category 7");
        categories.add("category 8");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                uploadNewSubCategory();
            }
        });

    }

    private void uploadNewSubCategory()
    {
        String subcatName = edtSubCat.getText().toString();

        if(!TextUtils.isEmpty(subcatName))
        {
            Log.i("subcat",subcatName);

            category = spinner.getSelectedItem().toString();
            Log.i("selected item",category+"");

            DatabaseReference subDatabase = mDatabase.child(category);
            subDatabase = subDatabase.child(subcatName);
            HashMap subMap = new HashMap();
            subMap.put("name",subcatName);
            subDatabase.setValue(subMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AddSubCategoryActivity.this, "subCategory added", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(AddSubCategoryActivity.this, "could not add", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        else
        {
            Toast.makeText(this, "enter the sabcategory name", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        category = adapterView.getItemAtPosition(i).toString();
        Log.i("selected"," category : "+category);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

}
