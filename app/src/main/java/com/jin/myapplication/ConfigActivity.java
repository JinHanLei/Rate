package com.jin.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {

    public final String TAG = "ConfinActivity";

    EditText dollorText;
    EditText euroText;
    EditText wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();
        float dollor2 = intent.getFloatExtra("dollor_rate_key",0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key",0.0f);
        float won2 = intent.getFloatExtra("won_rate_key",0.0f);


        Log.i(TAG,"onCreate:dollor2="+dollor2);
        Log.i(TAG,"onCreate:euro2="+euro2);
        Log.i(TAG,"onCreate:won2="+won2);

        dollorText = (EditText)findViewById(R.id.dollor_rate);
        euroText = (EditText)findViewById(R.id.euro_rate);
        wonText = (EditText)findViewById(R.id.won_rate);

        dollorText.setText(String.valueOf(dollor2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String .valueOf(won2));
    }
    public void save(View btn){
        Log.i("cfg","save:");

        float newDollor = Float.parseFloat(dollorText.getText().toString());
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());

        Log.i(TAG,"save");
        Log.i(TAG,"onCreate:newDollors"+newDollor);
        Log.i(TAG,"onCreate:newEuro"+newEuro);
        Log.i(TAG,"onCreate:newWon"+newWon);

        Intent intent = getIntent();
        Bundle bd1 = new Bundle();
        bd1.putFloat("key_dollor",newDollor);
        bd1.putFloat("key_euro",newEuro);
        bd1.putFloat("key_won",newWon);
        intent.putExtras(bd1);
        setResult(2,intent);

        finish();

    }

}
