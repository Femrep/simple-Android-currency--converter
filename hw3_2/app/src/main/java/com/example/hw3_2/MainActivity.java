package com.example.hw3_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    EditText firstInput;
    TextView secondInput;
    Spinner mySpinnerFirst,mySpinnerSecond;
    TextView tvSee;
    List<String> currency = new ArrayList<>();
    List<Float> currencyMulti = new ArrayList<>();
    String curPositionOne,curPositionTwo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstInput=findViewById(R.id.firstInput);
        secondInput=findViewById(R.id.secondInput);
        Button convert = findViewById(R.id.convertButton);
        mySpinnerFirst=findViewById(R.id.first);
        mySpinnerSecond=findViewById(R.id.second);
        tvSee=findViewById(R.id.tv1);

       currency.add("");


        if(checkNetworkConnection()){
            new HTTPAsyncTask().execute("https://api.currencyfreaks.com/v2.0/rates/latest?apikey=3ac86e1bd63448cd9b0fe4701d63ddce&format=xml");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, currency);
        mySpinnerFirst.setAdapter(adapter);
        mySpinnerSecond.setAdapter(adapter);
        mySpinnerFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                String selectedValue = adapterView.getItemAtPosition(position).toString();


                curPositionOne = ""+ position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mySpinnerSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                String selectedValue = adapterView.getItemAtPosition(position).toString();


                 curPositionTwo = ""+position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String one = firstInput.getText().toString();
                if (!one.isEmpty()) {
                    float value = Float.parseFloat(one);
                    if (!curPositionOne.equals("0") && !curPositionTwo.equals("0")) {
                        int posOne = Integer.parseInt(curPositionOne);
                        int posTwo = Integer.parseInt(curPositionTwo);
                        float f = currencyMulti.get(posOne-1);
                        float s = currencyMulti.get(posTwo-1);
                        float calculated = (value * s) / f;
                        secondInput.setText(String.valueOf(calculated));
                    }
                }
            }
        });

    }


    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo= connMgr.getActiveNetworkInfo();
        boolean isConnected=false;
        if(networkInfo!= null && (isConnected=networkInfo.isConnected())) {


        }

        return isConnected;

    }
    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            try{
                return HttpGet(urls[0]);

            }catch (IOException e){
                return "Unable to retrieve web page.  Url may be invalid";
            }
        }
        protected void onPostExecute(String result){
            try {

                XMLParser(result);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private String HttpGet(String myUrl) throws IOException{
            InputStream inputStream=null;
            String result="";
            URL url= new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.connect();
            inputStream=conn.getInputStream();

            if(inputStream!=null){
                result=convertInputStreamToString(inputStream);
            }else {
                result="Did not work";
            }
            return result;

        }
        private String convertInputStreamToString(InputStream inputStream)throws IOException{
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            String result="";
            while((line=bufferedReader.readLine())!=null){
                result+=line;

            }
            inputStream.close();
            return result;
        }
        public void XMLParser( String result)throws XmlPullParserException,IOException {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            String NewResult = "";
            String Tag = "";


            while (eventType!=XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {


                } else if (eventType == XmlPullParser.END_DOCUMENT) {


                } else if (eventType == XmlPullParser.START_TAG) {
                    Tag = xpp.getName();
                    String checker = xpp.getName();
                    if(!(checker.equals("base")||checker.equals("date")||checker.equals("LatestRatesResponse") || checker.equals("rates"))) {
                        currency.add(checker);


                    }


                } else if (eventType == XmlPullParser.END_TAG) {

                } else if (eventType == XmlPullParser.TEXT) {

                    if (!(Tag.equals("base")||Tag.equals("date")||Tag.equals("LatestRatesResponse") || Tag.equals("rates"))){
                        currencyMulti.add(Float.valueOf(xpp.getText()));
                        Tag="";
                    }


                }
                eventType = xpp.next();
            }


        }


    }



}