package com.example.company;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WithdrawPage extends AppCompatActivity {

    TextView available_amount;
    EditText amount_to_withdraw;
    EditText cardholder_name;
    EditText card_number;
    EditText bank_name;
    EditText bank_code;

    Button btnWithdraw;
    private String usrId;
    private int usrBalance;

    private DatabaseReference withdrawals_database;
    private DatabaseReference acc_BalanceRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_page);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        withdrawals_database = mFirebaseDatabase.getReference("withdrawals");
        acc_BalanceRef = mFirebaseDatabase.getReference("Users/");

        FirebaseUser usr = mAuth.getCurrentUser();
        usrId = usr.getUid();

        acc_BalanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getBalance(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}

        });

        //available_amount = findViewById(R.id.withdraw_balance_amount);
        amount_to_withdraw = findViewById(R.id.withdraw_value);
        cardholder_name = findViewById(R.id.withdraw_holder_name);
        card_number = findViewById(R.id.withdraw_account_number);
        bank_name = findViewById(R.id.withdraw_bank_name);
        bank_code = findViewById(R.id.withdraw_bank_code);

        btnWithdraw = findViewById(R.id.withdraw_button);
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                withdraw_money();

            }
        });

    }

    public void withdraw_money(){

        String card_information = "";



        final String paid_amount = amount_to_withdraw.getText().toString().substring(amount_to_withdraw.getText().toString().indexOf("R") + 1, amount_to_withdraw.getText().toString().length());
        String amountWithdrawn = paid_amount.replace(",", "");
        int withdraw_amount     = 0;


        if(!amountWithdrawn.isEmpty()){

            if(amountWithdrawn.contains(".")){

                amountWithdrawn = amountWithdrawn.replace(",","");
                amountWithdrawn = amountWithdrawn.replace(".","");
                amountWithdrawn = amountWithdrawn.trim();
                withdraw_amount = Integer.parseInt(amountWithdrawn);

            }
            else{

                amountWithdrawn = amountWithdrawn.replace(",","");
                amountWithdrawn = amountWithdrawn.trim();
                withdraw_amount = Integer.parseInt(amountWithdrawn)*100;

            }


        }else{

            amount_to_withdraw.setError("This field cannot be empty.");
            amount_to_withdraw.requestFocus();

        }



        //int withdraw_amount = Double.parseDouble(amount_to_withdraw.getText().toString());

        String cardholders_name = cardholder_name.getText().toString();
        String card_num = card_number.getText().toString();
        String banks_name = bank_name.getText().toString();
        String banks_code = bank_code.getText().toString();

        if(card_num.isEmpty()){
            card_number.setError("This field cannot be empty.");
            card_number.requestFocus();
        }
        else if(banks_code.isEmpty()){
            bank_code.setError("This field cannot be empty.");
            bank_code.requestFocus();
        }
        else if(banks_name.isEmpty()){
            bank_name.setError("This field cannot be empty.");
            bank_name.requestFocus();
        }
        else if(cardholders_name.isEmpty()){
            cardholder_name.setError("This field cannot be empty.");
            cardholder_name.requestFocus();
        }
        else if(card_num.length() < 9){
            card_number.setError("Card Number too short");
            card_number.requestFocus();
        }

        else if (withdraw_amount < 1999){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            amount_to_withdraw.setError("Amount to withdraw must be greater than R19.99");
            amount_to_withdraw.requestFocus();

        }else if(withdraw_amount > usrBalance){

            Toast.makeText(this, "Insufficient funds", Toast.LENGTH_LONG).show();
            amount_to_withdraw.setError("You have Insufficient Funds");
            amount_to_withdraw.requestFocus();

        }
        else {


            //card_information += "Current balance: " + available_amount_;
            card_information += "\n" +
                    "Amount to withdraw: "+  withdraw_amount;
            card_information += "\n" +
                    "Account Holder Name: "+ cardholders_name;
            card_information += "\n" +
                    "Account Number: "+ card_num;
            card_information += "\n" +
                    "Bank Name: "+ banks_name;
            card_information += "\n" +
                    "Bank Code: "+ banks_code;

            card_information += "Current balance: " + usrBalance;
            card_information += "\nAmount to withdraw: "+  withdraw_amount;
            card_information += "\nAccount Holder Name: "+ cardholders_name;
            card_information += "\nAccount Number: "+ card_num;
            card_information += "\nBank Name: "+ banks_name;
            card_information += "\nBank Code: "+ banks_code;


            String id = withdrawals_database.push().getKey(); // generates a new unique id every time.

            Withdrawal withdrawal = new Withdrawal(id,usrBalance,withdraw_amount,cardholders_name,card_num,banks_name,banks_code);

            withdrawals_database.child(id).setValue(withdrawal);

            usrBalance -= withdraw_amount;
            //usrBalance *=100;
            acc_BalanceRef.child(usrId).child("balance").setValue(usrBalance);

            //Log.i("SendMailActivity", "Send Button Clicked.");
            String emailSubject = "Withdrawal";
            String emailBody = card_information;
            ArrayList<String> emails = new ArrayList<>();
            emails.add("specialwithdrawalemails@email.com");
            new SendMailTask(WithdrawPage.this).execute("user@email.com",
                    "password", emails, "XXXXX Withdrawal", emailBody);

            Toast.makeText(this, "Withdrawal request sent to CompanyXXXX", Toast.LENGTH_LONG).show();
        }

    }

    public void getBalance(DataSnapshot ds){

        usrBalance = ds.child(usrId).child("balance").getValue(Integer.class);
    }

    @Override
    public void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mAuthListener != null){
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

}
