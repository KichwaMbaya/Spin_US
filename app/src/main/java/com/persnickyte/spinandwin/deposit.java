package com.persnickyte.spinandwin;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class deposit extends AppCompatActivity {

    TextView depos_statement;
    Button depos_but;
    EditText depos_amount;
    String Open_K, Hidden_K, least, acount_bal;
    int mpesa_amount;
    SharedPreferences sharedPreferences;
    String customerID, emphericalKey, ClientSecret, OpenK, HiddenK, minim;
    PaymentSheet paymentSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        depos_statement = findViewById(R.id.depos_statement);
        depos_but = findViewById(R.id.depos_but);
        depos_amount = findViewById(R.id.depos_amount);

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        acount_bal = sharedPreferences.getString("Points_Earned", "0");
        Open_K = sharedPreferences.getString("OpenK", "");
        Hidden_K = sharedPreferences.getString("HiddenK", "");
        least = sharedPreferences.getString("least", "10");

        depos_statement.setText("You can top up your account balance here to continue winning. To make a deposit, enter the amount " +
                "you want to deposit below and click on deposit.\n\n" +
                "NB: The amount you are depositing should not be less than $" + least + ".\n\n" +
                "Click on the button below to deposit.");

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Confidential")
                .document("Active");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot dsnaps = task.getResult();

                OpenK = dsnaps.getString("Open");
                HiddenK = dsnaps.getString("Hidden");
                minim = dsnaps.getString("Least");

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("OpenK", OpenK);
                editor.putString("HiddenK", HiddenK);
                editor.putString("least", minim);
                editor.commit();

                depos_statement.setText("You can top up your account balance here to continue winning. To make a deposit, enter the amount " +
                        "you want to deposit below and click on deposit.\n\n" +
                        "NB: The amount you are depositing should not be less than $" + least + ".\n\n" +
                        "Click on the button below to deposit.");
            }
        });

        depos_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mp_amount = depos_amount.getText().toString();
                int minimun = Integer.parseInt(least);

                if (mp_amount.isEmpty()){
                    depos_amount.setError("Enter amount");
                    Toast.makeText(deposit.this, "Enter the amount you deposited", Toast.LENGTH_SHORT).show();
                    return;
                }
                mpesa_amount = Integer.parseInt(mp_amount);

                if (mpesa_amount < minimun) {
                    Toast.makeText(deposit.this, "Amount should be atleast $ " +least , Toast.LENGTH_SHORT).show();
                }
                else if ((mpesa_amount >= minimun)){
                    Toast.makeText(deposit.this, "Please wait", Toast.LENGTH_SHORT).show();
                    PayNow();
                }
            }
        });
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);
        });
    }
    private void PayNow(){

        PaymentConfiguration.init(this,Open_K);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            customerID = object.getString("id");

                            getEmphericalKey(customerID);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+ Hidden_K);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(deposit.this);
        requestQueue.add(stringRequest);

    }

    private void getEmphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            emphericalKey = object.getString("id");

                            getClientSecret(customerID, emphericalKey);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+ Hidden_K);
                header.put("Stripe-Version","2020-08-27");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(deposit.this);
        requestQueue.add(stringRequest);

    }

    private void getClientSecret(String customerID, String emphericalKey) {

        int lon = Integer.parseInt(depos_amount.getText().toString());
        String deni = String.valueOf((lon) * 100 + 30);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            ClientSecret = object.getString("client_secret");

                            PaymentFlow();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+ Hidden_K);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", deni);
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(deposit.this);
        requestQueue.add(stringRequest);

    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(deposit.this, "Successful", Toast.LENGTH_SHORT).show();

            int add = Integer.parseInt(acount_bal) + mpesa_amount * 100;
            String result = String.valueOf(add);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Points_Earned", result);
            editor.putInt("Deposit", 1);
            editor.commit();

            Intent intent = new Intent(deposit.this, MainActivity.class);
            deposit.this.startActivity(intent);
            finish();
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(deposit.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void PaymentFlow() {

        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration(getResources().getString(R.string.app_name),
                        new PaymentSheet.CustomerConfiguration(customerID, emphericalKey))
        );

    }
}