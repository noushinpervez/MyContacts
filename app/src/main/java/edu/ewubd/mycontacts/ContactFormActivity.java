package edu.ewubd.mycontacts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ContactFormActivity extends AppCompatActivity {

    String image = "";
    private EditText etName, etEmail, etHomePhone, etOfficePhone;
    private ImageView imgView;
    private String id = "";
    private ContactDB contactDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_form);

        contactDB = new ContactDB(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etHomePhone = findViewById(R.id.etHomePhone);
        etOfficePhone = findViewById(R.id.etOfficePhone);
        imgView = findViewById(R.id.imgView);

        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");

            if (id != null && !id.isEmpty()) {
                updateFields(id);
            }
        }

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ContactFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactFormActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                } else {
                    openGallery();
                }
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContactList();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                byte[] bytes = stream.toByteArray();
                image = Base64.encodeToString(bytes, Base64.DEFAULT);
                imgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void decodeImage() {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imgView.setImageBitmap(bitmap);
    }

    private void saveContactList() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String homePhone = etHomePhone.getText().toString().trim();
        String officePhone = etOfficePhone.getText().toString().trim();

        String errMsg = "";

        if (name.isEmpty() || email.isEmpty() || homePhone.isEmpty() || image.isEmpty()) {
            errMsg = "Please provide information for mandatory fields (Name, Email, Phone (Home), Image).";
            showErrorDialog(errMsg);
            return;
        }

        if (name.length() < 3 || name.length() > 50 || !name.matches("^[a-zA-Z0-9 ]+$")) {
            errMsg += "Name must be between 3-50 characters and alphanumeric, ";
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errMsg += "Invalid Email Address, ";
        }

        if (!((homePhone.startsWith("+880") && homePhone.length() == 14) || (homePhone.startsWith("880") && homePhone.length() == 13) || (homePhone.startsWith("01") && homePhone.length() == 11))) {
            errMsg += "Invalid Home Phone Number, ";
        }

        if (!officePhone.isEmpty()) {
            if (!((officePhone.startsWith("+880") && officePhone.length() == 14) || (officePhone.startsWith("880") && officePhone.length() == 13) || (officePhone.startsWith("01") && officePhone.length() == 11))) {
                errMsg += "Invalid Office Phone Number, ";
            }
        }

        if (errMsg.length() > 0) {
            if (errMsg.endsWith(", ")) {
                errMsg = errMsg.substring(0, errMsg.length() - 2);
            }

            showErrorDialog(errMsg);
            return;
        }

        if (id.isEmpty()) {
            id = name + System.currentTimeMillis();
            contactDB.insertContact(id, name, email, homePhone, officePhone, image);
            Toast.makeText(this, "New contact information is successfully inserted", Toast.LENGTH_SHORT).show();
        } else {
            contactDB.updateContact(id, name, email, homePhone, officePhone, image);
            Toast.makeText(this, "Existing contact information is successfully updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void updateFields(String id) {
        Contact contact = contactDB.getContactById(id);

        if (contact != null) {
            etName.setText(contact.name);
            etEmail.setText(contact.email);
            etHomePhone.setText(contact.homePhone);
            etOfficePhone.setText(contact.officePhone);

            if (contact.image != null && !contact.image.isEmpty()) {
                image = contact.image;
                decodeImage();
            } else {
                imgView.setImageResource(R.drawable.ic_launcher_foreground);
                imgView.setBackgroundResource(R.drawable.circle);
            }

            this.id = contact.id;
        }
    }

    private void showErrorDialog(String errMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage(errMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);

        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}