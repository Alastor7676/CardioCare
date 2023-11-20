package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Spinner spinner;
    List<doctorlist> dataList;
    ProgressDialog progressDialog;
    doctoradapter adapter;
    doctorlist androidData;
    DatabaseReference databaseReference;
    String item;
//    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        spinner = findViewById(R.id.spinnerSortBy);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Doctor");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Set your desired message here
        progressDialog.setCancelable(false); // Set if the dialog is cancelable or not
        progressDialog.show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
                Toast.makeText(DoctorsActivity.this, ""+item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new doctoradapter(this,dataList);
        recyclerView.setAdapter(adapter);
//        androidData = new doctorlist("Asifur Rahman", "Cardiologist", "5 years", "1500 TK", R.drawable.forgot_password);
//        dataList.add(androidData);
//        androidData = new doctorlist("Asifur Rahman", "Cardiologist", "5 years", "1500 TK", R.drawable.forgot_password);
//        dataList.add(androidData);
//        androidData = new doctorlist("Asifur Rahman", "Cardiologist", "5 years", "1500 TK", R.drawable.forgot_password);
//        dataList.add(androidData);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataList.clear(); // Clear existing data before adding new data

                    // Iterate through the data snapshot to retrieve doctor information
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Assuming your doctorlist class has appropriate constructor and getter methods
                        doctorlist doctor = snapshot.getValue(doctorlist.class);
                        dataList.add(doctor); // Add the retrieved doctor information to your list
                    }

                    adapter.notifyDataSetChanged(); // Notify adapter of data changes
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors when fetching data
                Toast.makeText(DoctorsActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Default");
        arrayList.add("Fees(Low to High)");
        arrayList.add("Fees(High to Low)");
        arrayList.add("Name(A to Z)");
        arrayList.add("Name(Z to A)");
        arrayList.add("Expertise");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);
    }
}