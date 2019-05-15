package com.jin.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        float a,b,c;
        BigDecimal f;
        TextView out = findViewById(R.id.result);
        EditText inp = findViewById(R.id.inp);
        String str = inp.getText().toString();

        try {
            a = Float.parseFloat(str);
            b = (float) (a * 1.8 + 32);
            f = new BigDecimal(b);
            c = f.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
            String s = "" + c;
            out.setText(s);
        }catch (NumberFormatException e){
            inp.setText("");
            out.setText("请输入数字！");
        }
    }
}
