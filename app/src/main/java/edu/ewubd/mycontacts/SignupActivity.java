package edu.ewubd.mycontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText etUserName, etEmail, etPhone, etUserId, etPW, etRPW;
    private CheckBox cbRemUserId, cbRemPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decideNavigation(getIntent().getBooleanExtra("FROM_LOGIN", false));

        setContentView(R.layout.activity_signup);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etUserId = findViewById(R.id.etUserId);
        etPW = findViewById(R.id.etPW);
        etRPW = findViewById(R.id.etRPW);

        cbRemUserId = findViewById(R.id.cbRemUserId);
        cbRemPass = findViewById(R.id.cbRemPass);

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSignup();
            }
        });
    }

    private void processSignup() {
        String userName = etUserName.getText().toString().trim();
        String userEmail = etEmail.getText().toString().trim();
        String userPhone = etPhone.getText().toString().trim();
        String userId = etUserId.getText().toString().trim();
        String userPW = etPW.getText().toString().trim();
        String userRPW = etRPW.getText().toString().trim();

        String errMsg = "";

        if (userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || userId.isEmpty() || userPW.isEmpty() || userRPW.isEmpty()) {
            errMsg = "Please provide information for all fields.";
            showErrorDialog(errMsg);
            return;
        }

        if (userName.length() < 3 || userName.length() > 50 || !userName.matches("^[a-zA-Z0-9 ]+$")) {
            errMsg += "Username must be between 3-50 characters and alphanumeric, ";
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            errMsg += "Invalid Email Address, ";
        }

        if (!((userPhone.startsWith("+880") && userPhone.length() == 14) || (userPhone.startsWith("880") && userPhone.length() == 13) || (userPhone.startsWith("01") && userPhone.length() == 11))) {
            errMsg += "Invalid Phone Number, ";
        }

        if (userId.length() < 3 || userId.length() > 10 || !userId.matches("^[a-zA-Z0-9]+$")) {
            errMsg += "User ID must be between 3-10 characters and contain only letters and numbers, ";
        }

        if (userPW.length() != 6) {
            errMsg += "Password must be 6 characters long, ";
        }

        if (!userPW.equals(userRPW)) {
            errMsg += "Passwords do not match.";
        }

        if (errMsg.length() > 0) {
            if (errMsg.endsWith(", ")) {
                errMsg = errMsg.substring(0, errMsg.length() - 2);
            }

            showErrorDialog(errMsg);
            return;
        }

        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();

        e.putString("USER_NAME", userName);
        e.putString("USER_EMAIL", userEmail);
        e.putString("USER_PHONE", userPhone);
        e.putString("USER_ID", userId);
        e.putString("PASSWORD", userPW);

        e.putBoolean("REM_USER", cbRemUserId.isChecked());
        e.putBoolean("REM_PASS", cbRemPass.isChecked());

        e.apply();

        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
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

    private void decideNavigation(boolean fromLogin) {
        SharedPreferences sp = this.getSharedPreferences("user_info", MODE_PRIVATE);
        String userName = sp.getString("USER_NAME", "NOT-CREATED");

        if (!userName.equals("NOT-CREATED") && !fromLogin) {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}