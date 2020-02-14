package com.example.companyXXX;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ReceivePayment extends AppCompatActivity {



    private DatabaseReference myUidRef;

    //{CODE_START}
    private DatabaseReference sendRequest;
    private FirebaseUser senderUser;
    private String myUcode;
    private EditText sender_amount;
    private EditText sender_uCode;
    private Button request_btn;
    private PayRequest payrequest;
    private ProgressDialog request_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment);

        senderUser = FirebaseAuth.getInstance().getCurrentUser();
        final String sender_uid = senderUser.getUid();

        //refer to sendRequests under specific user_uid
        sendRequest = FirebaseDatabase.getInstance().getReference().child("sendRequests").child(sender_uid);
        myUidRef = FirebaseDatabase.getInstance().getReference().child("Users").child(sender_uid);

        myUidRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getmyUid(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        sender_amount = findViewById(R.id.amount_to_pay_receivepayment1);
        sender_uCode = findViewById(R.id.receivepage_customer_code);
        request_btn = findViewById(R.id.receivepayment_button);
        request_progress = new ProgressDialog(this);

        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code_string = sender_uCode.getText().toString().trim();
                String amount_string = sender_amount.getText().toString().trim();

                amount_string = amount_string.replace(",", "");
                amount_string = amount_string.replace("R", "");
                amount_string = amount_string.trim();

                String amount_start = null;
                String amount_end = null;

                if(amount_string.contains(".")){

                    amount_start = amount_string.substring(0,amount_string.indexOf("."));
                    amount_end = amount_string.substring(amount_string.indexOf(".")+1);
                }else{

                    amount_start = amount_string;
                    amount_end = "00";
                }

                if(code_string.isEmpty()){
                    sender_uCode.setError("Please enter user code.");
                    sender_uCode.requestFocus();
                }
                else if(amount_string.isEmpty()){
                    sender_amount.setError("Please enter amount.");
                    sender_amount.requestFocus();
                }
                else if(code_string.length() != 5){
                    sender_uCode.setError("Please enter appropriate code");
                    sender_uCode.requestFocus();
                }
                else if(Double.parseDouble(amount_string) <= 0.00){
                    sender_amount.setError("Please enter amount more than 0");
                    sender_amount.requestFocus();
                }
                else if(amount_end.length() != 2){
                    sender_amount.setError("Please enter amount in suggested format.");
                    sender_amount.requestFocus();
                }
                else if(!(amount_string.isEmpty() && code_string.isEmpty())){

                    if(!code_string.equals(myUcode)){

                        request_progress.setMessage("Sending Request... ");
                        request_progress.show();
                        int code_int = Integer.parseInt(code_string);     //user code in integer form
                        String amount_cents_string = amount_start + amount_end;   //amount requested, in cents(String)
                        int amount_cents_int = Integer.parseInt(amount_cents_string);       //amount requested, in cents(int)
                        payrequest = new PayRequest(sender_uid, code_int, amount_cents_int);  //request to be sent to db
                        String request_id = sendRequest.push().getKey();  //get random generated
                        sendRequest.child(request_id).setValue(payrequest);   //push into db respective slot
                        request_progress.setMessage("Request complete!");
                        request_progress.dismiss();
                        Toast.makeText(ReceivePayment.this,"Request completed successfully!", Toast.LENGTH_SHORT).show();
                        Intent toTabs = new Intent(ReceivePayment.this, Tabs.class);
                        startActivity(toTabs);

                    }else{

                        sender_uCode.setError("Please enter a different user code from yours");
                        sender_uCode.requestFocus();
                    }
                }
            }
        });
    }


    public void getmyUid(DataSnapshot dataSnapshot){

        myUcode = ""+dataSnapshot.child("uCode").getValue(Long.class);
    }
}