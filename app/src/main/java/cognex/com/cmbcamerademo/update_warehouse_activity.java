package cognex.com.cmbcamerademo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;

public class update_warehouse_activity extends AppCompatActivity {

    List<String> listItems = new ArrayList<String>();
    List<String> listItems_id = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Spinner sp;
    String HttpUrl = "http://192.168.0.127/warehouse_service/getCommesse.php";
    String HttpUrl_id = "http://192.168.0.127/warehouse_service/getCommesse_id.php";
    String HttpUrl_update = "http://192.168.0.127/warehouse_service/updateItem.php";
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    InputStream is = null;
    String line = null;
    String result = null;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    EditText txtQuantity;
    Button btnBack;
    String CommesseHolder;
    String CommesseidHolder;
    String idholder ;
    String BarcodeHolder;
    String QtyHolder;
    EditText txtBarcode;
    EditText productid;
    String productid_holder;
    Button btnUpdate;
    String finalResult;
    Button itemBack;
    EditText commesse_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_warehouse_activity);
        new getCommesse().execute();
     new getCommesse_id().execute();
        CommesseHolder=getIntent().getStringExtra("Commesse");
        CommesseidHolder=getIntent().getStringExtra("CommesseID");
        BarcodeHolder=getIntent().getStringExtra("Barcode");
        QtyHolder=getIntent().getStringExtra("Quantity");
        txtQuantity=(EditText)findViewById(R.id.Quantity_value);
        txtQuantity.setText(QtyHolder);
        productid_holder=getIntent().getStringExtra("ProductID");
       productid=(EditText)findViewById(R.id.Product_id);
        productid.setText(productid_holder);
        commesse_id=(EditText)findViewById(R.id.commesseid);
        commesse_id.setText(CommesseidHolder);
        commesse_id=(EditText)findViewById(R.id.commesseid);
        listItems.add(CommesseHolder);
        listItems_id.add(CommesseidHolder);
        commesse_id=(EditText)findViewById(R.id.commesseid);
        commesse_id.setText(CommesseidHolder);


        txtBarcode=(EditText)findViewById(R.id.Barcode);
        txtBarcode.setText(BarcodeHolder);
        btnUpdate=(Button)findViewById(R.id.updateitem);



        itemBack=(Button)findViewById(R.id.back);
        itemBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openProducts();
                    }
                }
        );

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        sp=(Spinner)findViewById(R.id.brandCommesse);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listItems);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                        String idholder=listItems_id.get(position);
                        commesse_id.setText(idholder);
                        //Toast.makeText(update_warehouse_activity.this,idholder.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        commesse_id=(EditText)findViewById(R.id.commesseid);
                        commesse_id.setText(CommesseidHolder);
                        txtQuantity=(EditText)findViewById(R.id.Quantity_value);
                        txtQuantity.setText(QtyHolder);
                    }

                }
        );
        btnUpdate.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //commesse_id.setText(CommesseidHolder);
                        ItemUpdate(productid_holder,BarcodeHolder,CommesseidHolder,QtyHolder);
                        openProducts();
                    }
                }
        );

        }
    public void openProducts(){
        Intent i = new Intent(this,ScannerActivity.class);
        startActivity(i);
        //finish();
    }
    public void ItemUpdate(final String ProductID,final String Barcode,final String CommesseID,final String Qty){
        class item_update extends AsyncTask<String,Void,String>{

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("ProductID",productid.getText().toString());
                hashMap.put("Barcode",txtBarcode.getText().toString());
                hashMap.put("CommesseID",commesse_id.getText().toString());
                hashMap.put("Qty",txtQuantity.getText().toString());
                finalResult = httpParse.postRequest(hashMap, HttpUrl_update);
                return finalResult;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(update_warehouse_activity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                progressDialog.dismiss();
                Toast.makeText(update_warehouse_activity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();
            }
        }
        item_update update_items= new item_update();
        update_items.execute(ProductID,Barcode,CommesseID,Qty);

    }
    private class getCommesse extends AsyncTask<Void, Void, Void> {
        List<String> list;

        @Override
        protected Void doInBackground(Void... params) {
            InputStream is=null;
            String result ="";
            try{
                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(HttpUrl);
                HttpResponse response=httpClient.execute(httpPost);
                HttpEntity entity=response.getEntity();
                is=entity.getContent();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }try{
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is,"utf-8"));
                StringBuilder sb = new StringBuilder();
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }
                is.close();
                result=sb.toString();
            }catch(IOException e) {
                e.printStackTrace(); }
            try{
                JSONArray jsonArray=new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    list.add((String) jsonObject.get("code"));

                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();

        }

        @Override
        protected void onPostExecute(Void result) {
            //list.remove(1);
            //StringBuilder builder= new StringBuilder();
            listItems.addAll(list);

            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    private class getCommesse_id extends AsyncTask<Void, Void, Void> {
        List<String> list;

        @Override
        protected Void doInBackground(Void... params) {
            InputStream is=null;
            String result ="";
            try{
                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(HttpUrl_id);
                HttpResponse response=httpClient.execute(httpPost);
                HttpEntity entity=response.getEntity();
                is=entity.getContent();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }try{
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is,"utf-8"));
                StringBuilder sb = new StringBuilder();
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }
                is.close();
                result=sb.toString();
            }catch(IOException e) {
                e.printStackTrace(); }
            try{
                JSONArray jsonArray=new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    list.add((String) jsonObject.get("CommesseID"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();

        }

        @Override
        protected void onPostExecute(Void result) {
            //list.remove(1);
            //StringBuilder builder= new StringBuilder();
            listItems_id.addAll(list);

            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
