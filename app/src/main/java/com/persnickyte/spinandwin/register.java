package com.persnickyte.spinandwin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class register extends AppCompatActivity {

    EditText rgsterfirst, rgstermiddle, rgsterlast, rgsteremail, rgsterphone, rgsterpin, rgsterconfirm;
    Button rgsterdob, rgsterbutton;
    DatePickerDialog datePickerDialog;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rgsterfirst = findViewById(R.id.rgsterfirst);
        rgstermiddle = findViewById(R.id.rgstermiddle);
        rgsterlast = findViewById(R.id.rgsterlast);
        rgsteremail = findViewById(R.id.rgsteremail);
        rgsterphone = findViewById(R.id.rgsterphone);
        rgsterpin = findViewById(R.id.rgsterpin);
        rgsterconfirm = findViewById(R.id.rgsterconfirm);
        rgsterdob = findViewById(R.id.rgsterdob);
        rgsterbutton = findViewById(R.id.rgsterbutton);


        initDatePicker();
        rgsterdob.setText(getTodaysDate());

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);

        rgsterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker();
            }
        });
    }
    private void checker(){
        String first = rgsterfirst.getText().toString();
        String middle = rgstermiddle.getText().toString();
        String last = rgsterlast.getText().toString();
        String email = rgsteremail.getText().toString();
        String phone = rgsterphone.getText().toString();
        String pin = rgsterpin.getText().toString();
        String confirm = rgsterconfirm.getText().toString();

        if (first.isEmpty()){
            rgsterfirst.setError("Enter First Name");
            return;
        }
        if (middle.isEmpty()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("middle_name");
            editor.commit();
        }
        if (last.isEmpty()){
            rgsterlast.setError("Enter Last Name");
            return;
        }
        if (email.isEmpty()){
            rgsteremail.setError("Enter email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            rgsteremail.setError("Enter a valid email");
            rgsteremail.requestFocus();
            return;
        }
        if (phone.isEmpty()){
            rgsterphone.setError("Enter phone Number");
            return;
        }
        if (phone.length() < 10){
            rgsterphone.setError("Too Short");
            return;
        }
        if (phone.length() > 13){
            rgsterphone.setError("Too Long");
            return;
        }
        if (pin.isEmpty()){
            rgsterpin.setError("Enter PIN");
            return;
        }
        if (pin.length() < 4){
            rgsterpin.setError("PIN is too short");
            return;
        }
        if (confirm.isEmpty()){
            rgsterconfirm.setError("Confirm PIN");
            return;
        }
        if (!confirm.equals(pin)){
            rgsterpin.setError("PINs don't match");
            rgsterconfirm.setError("PINs don't match");
            return;
        }

        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(register.this);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("The information you have provided cannot be changed after registration.");
        dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                progressDialog = new ProgressDialog(register.this);
                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("Creating your account...");
                progressDialog.setProgressStyle(0);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("first_name", first);
                            editor.putString("middle_name", middle);
                            editor.putString("last_name", last);
                            editor.putString("Email", email);
                            editor.putString("phone_number", phone);
                            editor.putInt("Deposit", 0);
                            editor.putString("PIN", pin);
                            editor.commit();

                            Thread.sleep(8000);
                            Intent intent = new Intent(register.this, login.class);
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
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                rgsterdob.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    private String makeDateString(int day, int month, int year)
    {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}