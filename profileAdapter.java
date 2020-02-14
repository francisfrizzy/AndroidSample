package com.example.companyXXX;

import android.content.Intent;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;


public class profileAdapter extends ArrayAdapter {

    private final Activity mcontext;
    private final String[] detailEntry;

    public profileAdapter(Activity context, String[] receiptEntry){
        super(context,R.layout.profile_row,receiptEntry);

        this.mcontext = context;
        this.detailEntry = receiptEntry;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, final ViewGroup parent) {
        //this basically maps the data from properties to the write fields in the listView rows.xml
        //file


        LayoutInflater inflater = this.mcontext.getLayoutInflater();
        View rView = inflater.inflate(R.layout.profile_row,null,true);

        //get refferences to objects on XML file
        TextView updateDetails = (TextView)rView.findViewById(R.id.updateLgd);
        final Button updateLGD = (Button)rView.findViewById(R.id.updateLGBtn);

        // TextView logout = (TextView)rView.findViewById(R.id.logouttxtv);
        // Button logoutButton = (Button)rView.findViewById(R.id.logout_btn);
        // logout.setText(detailEntry[position])
        updateDetails.setText(detailEntry[position]);

        final String[] tempDEntry = new String[detailEntry.length];
        for(int i=0;i < detailEntry.length;++i)
            tempDEntry[i] = detailEntry[i];

        final int fposition = position;
        updateLGD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tempDEntry[fposition].equals("Update Login Details")){
                    Intent Int2UpdateLogin = new Intent(mcontext, UpdateLoginDetails.class);
                    Int2UpdateLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(Int2UpdateLogin);
                }
                if(tempDEntry[fposition].equals("Logout")){
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    Intent toSignInPage = new Intent(mcontext, SignInPage.class);
                    toSignInPage.putExtra("LO",true);
                    toSignInPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    toSignInPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mcontext.startActivity(toSignInPage);
                }

            }
        });
        rView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "string: " + dataModel.getName());
                //Log.d(TAG, "int: " + dataModel.getAnInt());
                //Log.d(TAG, "double: " + dataModel.getaDouble());
                //Log.d(TAG, "otherData: " + dataModel.getOtherData());



                Toast.makeText(parent.getContext(), "view clicked: " + tempDEntry[fposition] , Toast.LENGTH_SHORT).show();
                if(tempDEntry[fposition].equals("Update Login Details")){
                    Intent Int2UpdateLogin = new Intent(mcontext, UpdateLoginDetails.class);
                    Int2UpdateLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(Int2UpdateLogin);



                }//else if(tempDEntry[fposition].equals("Logout")){
                    //System.exit(0);
                //}

                if(tempDEntry[fposition].equals("Logout")){
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    Intent toSignInPage = new Intent(mcontext, SignInPage.class);
                    toSignInPage.putExtra("LO",true);
                    toSignInPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    toSignInPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mcontext.startActivity(toSignInPage);
                }

            }
        });
        return rView;
    }
}
