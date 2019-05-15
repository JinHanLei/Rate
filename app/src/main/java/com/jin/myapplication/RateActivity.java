package com.jin.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable{

    EditText rmb;
    TextView rsl;
    private final String TAG = "Rate";
    private float dollorRate = 0.0f;
    private float euroRate = 0.0f;
    private float wonRate = 0.0f;
    private String updateDate = "";
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText)findViewById(R.id.rmb);
        rsl = (TextView)findViewById(R.id.rsl);

        //获取sp里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        dollorRate = sharedPreferences.getFloat("dollor_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        updateDate = sharedPreferences.getString("updateDate","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写m为分钟
        final String todayStr = sdf.format(today);

        Log.i(TAG,"OnCreate:sp dollorRate"+dollorRate);
        Log.i(TAG,"OnCreate:sp euroRate"+euroRate);
        Log.i(TAG,"OnCreate:sp wonRate"+wonRate);
        Log.i(TAG,"OnCreate:sp updateDate"+updateDate);
        Log.i(TAG,"OnCreate:sp todayStr"+todayStr);

        //判断时间
        if (!todayStr.equals(updateDate)){
            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else {
            Log.i(TAG, "onCreate: 不需要更新");
        }



        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //收件
                if (msg.what==5){
                    //String str = (String) msg.obj;
                    Bundle bd1 = (Bundle) msg.obj;
                    dollorRate = bd1.getFloat("dollor-rate");
                    euroRate = bd1.getFloat("euro-rate");
                    wonRate = bd1.getFloat("won-rate");
                    Log.i(TAG,"handleMessage:dollor"+dollorRate);
                    Log.i(TAG,"handleMessage:euro"+euroRate);
                    Log.i(TAG,"handleMessage:won"+wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.putFloat("dollor_rate",dollorRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.apply();

                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();

                    /*Log.i(TAG,"handleMessage: getMessage msg = "+str);
                    rsl.setText(str);*/
                }
                super.handleMessage(msg);
            }
        };

    }


    public void onClick(View btn){
        Log.i(TAG,"onClick:");
        String str = rmb.getText().toString();
        Log.i(TAG,"onClick:get strs"+str);

        float r = 0;
        if (str.length()>0){
            r = Float.parseFloat(str);
        }else {
            Toast.makeText(this,"请输入金额", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG,"onClick:r="+r);

        if(btn.getId()==R.id.btn_dollor){
            rsl.setText(String.format("%.2f",r*dollorRate));
        } else if (btn.getId()==R.id.btn_euro) {
            rsl.setText(String.format("%.2f",r*euroRate));
        }else {
            rsl.setText(String.format("%.2f",r*wonRate));
        }
    }

    public  void  openOne(View btn){
        openConfig();
    }

    public  void openConfig(){
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollor_rate_key",dollorRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        Log.i(TAG,"openOne:dollorRate"+dollorRate);
        Log.i(TAG,"openOne:euroRate"+euroRate);
        Log.i(TAG,"openOne:wonRate"+wonRate);

        startActivityForResult(config,1);

        //startActivity(config);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_set) {
            openConfig();
        }else if (item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this,MyList2Activity.class);
            startActivity(list);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            dollorRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);

            //将新的汇率写到sp
            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollor_rate",dollorRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG,"onActivityResult:数据已保存到sharedPreferences");

        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void run() {
        Log.i(TAG,"run:run()...");
        for (int i=1;i<=3;i++){
            Log.i(TAG,"run:i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //用于保存获取的汇率
        Bundle bundle;

        //获取网络数据
/*        URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);
            Log.i(TAG,"run:html="+html);
            Document doc = Jsoup.parse(html);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        bundle=getFromBOC();

        //bundle中保存所获取的汇率
        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);
        //发件

    }

    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG,"run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");//ctrl+f 查找网页
            /*int i = 1;
            for(Element table : tables) {
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/

            Element table1 = tables.get(1);
            //Log.i(TAG,"run:table1="+table1);
            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollor-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if("韩国元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
            /*for (Element td : tds){
                Log.i(TAG,"run:td="+td);
                td.text();
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG,"run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");//ctrl+f 查找网页
            /*int i = 1;
            for(Element table : tables) {
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/

            Element table1 = tables.get(0);
            //Log.i(TAG,"run:table1="+table1);
            //获取td中的数据
            Elements tds = table1.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollor-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
            /*for (Element td : tds){
                Log.i(TAG,"run:td="+td);
                td.text();
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        //输入流转化成string
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();

    }
}