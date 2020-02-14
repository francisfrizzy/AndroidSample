package com.example.companyXXX;

//Code worked well for me, I suggest you go through
//official flutterwave API website to learn how to use the API
//for different circumstances

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Deposit_Page extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    Button btnDeposit;

    EditText txfAmount;
    EditText lname;
    EditText fname;
    EditText ref;
    EditText email;
    private double amount_input;
    private int balance = -1;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference users;
    private String user_id;
    private DataSnapshot scanSnapShot;
    private String UserCode = "12345";
    private TextView refference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_deposit__page);
        //return inflater.inflate(R.layout.activity_deposit__page, container, false);
        getSupportActionBar().hide();
        refference = findViewById(R.id.referenceTextView);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        users = mFirebaseDatabase.getReference("/Users");


        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                update_user_balance(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        txfAmount = findViewById(R.id.transfer);
        fname = findViewById(R.id.cardNumber);
        lname = findViewById(R.id.nameOnCard);
        ref = findViewById(R.id.expiryDate);
        email = findViewById(R.id.cvv);

        btnDeposit = findViewById(R.id.Submit_deposit);
        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });

    }

    private void makePayment(){

        String aamount = txfAmount.getText().toString();
        String e_mail = email.getText().toString();
        String first_name = fname.getText().toString();
        String last_name = lname.getText().toString();
        String reference = ref.getText().toString();

        if(aamount.isEmpty()){
            txfAmount.setError("Please Enter Amount");
            txfAmount.requestFocus();
        }
        else if(e_mail.isEmpty()){
            email.setError("Please enter an email");
            email.requestFocus();
        }
        else if(first_name.isEmpty()){
            fname.setError("Please enter your first name.");
            fname.requestFocus();
        }
        else if(last_name.isEmpty()){
            lname.setError("Please enter your last name.");
            lname.requestFocus();
        }
        else if(reference.isEmpty()){
            ref.setError("Please enter a reference.");
            ref.requestFocus();
        }
        else {

            String paid_amount;
            paid_amount = txfAmount.getText().toString().substring(txfAmount.getText().toString().indexOf("R") + 1, txfAmount.getText().toString().length());
            paid_amount = paid_amount.replace(",", "");
            paid_amount = paid_amount.replace("R","");

            int amount = 0;
            if(paid_amount.contains(".")) {

                if(paid_amount.endsWith(".")){

                    //e.g if user types in 178. remove . and *100 to get cents amount
                    paid_amount = paid_amount.replace(".","");
                    paid_amount = paid_amount.trim();
                    amount = Integer.parseInt(paid_amount);
                    amount *= 100;

                }else{

                    paid_amount = paid_amount.replace(".","");
                    paid_amount = paid_amount.trim();
                    amount = Integer.parseInt(paid_amount);
                }
            }else{

                paid_amount = paid_amount.trim();
                amount = Integer.parseInt(paid_amount);
                amount *=100;
            }

            amount_input = amount;

            RavePayManager ravePayManager = new RavePayManager(this);
            ravePayManager.setAmount(amount/100); // gets amount from the text field
            ravePayManager.setEmail(e_mail);
            ravePayManager.setCountry("ZA");
            ravePayManager.setCurrency("ZAR");
            ravePayManager.setNarration("Deposit to wallet");
            //-------------------------------------------------------------------------------------
            // this one is the test api key.
            ravePayManager.setPublicKey("FLWVBDK-367x9f89f413a05f5cace1e9254a0149a1-X"); //Fake ofcourse
            // before release put this one
            // *SECRET* :'D
            // the one just above is the public key
   // -------------------------------------------------------------------------------------------------



        // this one just below the test api encryption key
            ravePayManager.setEncryptionKey("7c8944gjfgd6c2973c9d1ab485");   //Made up
        // before deployment put this one
            // *ANOTHER SECRET* :')
            // the one just above is the live encryption key
            ravePayManager.acceptAccountPayments(true);
            ravePayManager.acceptCardPayments(true);
            ravePayManager.acceptBankTransferPayments(true); // i dont know if i should include this, just leave it


            // set to false when on live mode.
            ravePayManager.onStagingEnv(true); // set to false if not on test mode.
            ravePayManager.shouldDisplayFee(true);

            // set to false when you put public keys
            ravePayManager.showStagingLabel(true); // set to true when we on test mode

            

            ravePayManager.setfName(first_name); // add this
            ravePayManager.setlName(last_name); // add surname
            ravePayManager.setTxRef(System.currentTimeMillis() + " " + reference); // add transaction referrence
            ravePayManager.initialize();  // shows the UI.

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response"); // can check on rave github typical messages for succeess, error, cancelled.
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS, Your deposit was successful. " , Toast.LENGTH_SHORT).show();
                // should customize the message variable
                updateBalance(scanSnapShot);
                //System.out.println(balance);

                users.child(user_id).child("companyXXX_balance").setValue(balance);
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show(); // should customize the message variable
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show(); // should customize the message variable
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void update_user_balance(DataSnapshot dataSnapshot){
        UserCode = ""+dataSnapshot.child(user_id).child("uCode").getValue(Long.class);
        refference.setText("Reference: "+UserCode);
        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

            if (dataSnapshot1.getKey().equals(user_id)){

                scanSnapShot = dataSnapshot1;
            }
        }
    }

    public void updateBalance(DataSnapshot dataSnapshot1){

        balance = dataSnapshot1.child("companyXXX_balance").getValue(Integer.class);
        System.out.println("Balance "+ balance+" amount Input "+amount_input);
        balance += amount_input;
    }

    /*
    @Override
    public void onStart(){
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                update_user_balance(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    */

    public void roastMessage(String str) {
        Toast.makeText(Deposit_Page.this, str, Toast.LENGTH_SHORT).show();
    }
}
