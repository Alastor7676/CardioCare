package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditActivity extends AppCompatActivity {

    TextView blood,age,type,gender,phone,add;
    Button updateButton,imgbtn;
    ImageView img;
    Uri imguri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        blood = findViewById(R.id.editBlood);
        age = findViewById(R.id.editage);
        type = findViewById(R.id.edittype);
        gender = findViewById(R.id.editgender);
        phone = findViewById(R.id.editphone);
        add = findViewById(R.id.editAddress);
        img = findViewById(R.id.imageView5);
        updateButton = findViewById(R.id.updateButton);
        imgbtn = findViewById(R.id.chooseImageButton);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        id = preferences.getString("id", "null");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("patient").child(id);
        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    patient p = dataSnapshot.getValue(patient.class); // Create a User class that matches your database structure
                    // Update UI with user data
                    blood.setText(String.valueOf(p.getBt()));
                    age.setText(String.valueOf(p.getAge()));
                    type.setText(String.valueOf(p.getType()));
                    gender.setText(String.valueOf(p.getGender()));
                    phone.setText(String.valueOf(p.getPhone()));
                    add.setText(String.valueOf(p.getAddress()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the file picker when the "Choose Image" button is clicked
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imguri = data.getData();
            img.setImageURI(imguri);
        }
        else {
            Toast.makeText(EditActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        // Update the user profile in Firebase
        databaseReference.child("bt").setValue(blood.getText().toString());
        databaseReference.child("age").setValue(Integer.parseInt(age.getText().toString()));
        databaseReference.child("gender").setValue(gender.getText().toString());
        databaseReference.child("phone").setValue(phone.getText().toString());
        databaseReference.child("type").setValue(type.getText().toString());
        databaseReference.child("address").setValue(add.getText().toString());

        // Check if an image is selected
        if (imguri != null) {
            StorageReference imageReference = storageReference.child("patient/" + id + ".jpg");

            imageReference.putFile(imguri).addOnSuccessListener(taskSnapshot -> {
                // Get the download URL of the uploaded image
                imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Store the image URL in the database
                    databaseReference.child("imgurl").setValue(uri.toString());

                    Toast.makeText(EditActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // Finish the activity or navigate back to the profile page
                    Intent intent = new Intent(EditActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e ->
                        Toast.makeText(EditActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e ->
                    Toast.makeText(EditActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            // If no image is selected, proceed without uploading an image
            Toast.makeText(EditActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            // Finish the activity or navigate back to the profile page
            Intent intent = new Intent(EditActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }

}