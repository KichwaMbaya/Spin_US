package com.persnickyte.spinandwin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class bank extends AppCompatActivity {

    EditText ban_rout, ban_acc, ban_conf;
    Button ban_submit;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        ban_submit =findViewById(R.id.ban_submit);
        ban_rout = findViewById(R.id.ban_rout);
        ban_acc = findViewById(R.id.ban_acc);
        ban_conf = findViewById(R.id.ban_conf);

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String check = sharedPreferences.getString("account", "");

        if (!check.isEmpty()){
            Intent intent = new Intent(bank.this, withdraw.class);
            startActivity(intent);
            finish();
        }

        ban_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String ban_routn = ban_rout.getText().toString();
                 String ban_accn = ban_acc.getText().toString();
                 String ban_confn = ban_conf.getText().toString();

                 if (ban_routn.length() != 9){
                     Toast.makeText(bank.this, "Invalid routing number", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if (ban_accn.length() < 5){
                     Toast.makeText(bank.this, "Invalid account number", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if (ban_accn.length() > 20){
                     Toast.makeText(bank.this, "Invalid account number", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if (!ban_confn.equals(ban_accn)){
                     Toast.makeText(bank.this, "Please confirm your account number", Toast.LENGTH_SHORT).show();
                     return;
                 }


                ProgressDialog progressDialog = new ProgressDialog(bank.this);
                progressDialog.setMessage("Saving...");
                progressDialog.setProgressStyle(0);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("routing", ban_routn);
                            editor.putString("account", ban_accn);
                            editor.commit();

                            Thread.sleep(5000);
                            Intent intent = new Intent(bank.this, withdraw.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });
    }
}