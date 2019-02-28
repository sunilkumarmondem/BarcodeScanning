package cognex.com.cmbcamerademo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;


@RequiresApi(api = Build.VERSION_CODES.O)
public class add_product_activity extends AppCompatActivity {
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> listItems_id = new ArrayList<>();
    ArrayList<String> listItems1 = new ArrayList<>();
    ArrayList<String> listItems1_id = new ArrayList<>();

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;
    String JsonString;

    Spinner sp;
    Spinner spl;
    String HttpUrl = "http://192.168.0.127/warehouse_service/getbrands.php";
    String HttpUrl1 = "http://192.168.0.127/warehouse_service/getpositions.php";
   String HttpUrl2 = "http://192.168.0.127/warehouse_service/getBrandID.php";
  String HttpUrl3="http://192.168.0.127/warehouse_service/getPositionsID.php";
    ProgressBar progressBar;
    InputStream is = null;

    String line = null;

    String result="";
    String dataParsed="";
    Object singleParsed="";
    String id="";

    String[] data;
    EditText brand_id;
    EditText position_id;
    EditText Product_Name;
    EditText Product_Barcode;
    EditText Cassioli_Code;
    EditText Position;
    Button ProductAdd;
    Button ProductViewall;
    Boolean checkText;
    String finalResult;
    ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String HttpURL_save = "http://192.168.0.127/warehouse_service/saveProduct.php";
    String productNameHolder;
    String productBarcodeHolder;
    String CassioliCodeHolder;
    String Brandidholder;
    String Positionidholder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_activity);
        new getPositions().execute();
       new getPositions_id().execute();
        new getBrands().execute();
        new getBrands_id().execute();
        Product_Name=(EditText)findViewById(R.id.ProductName);
        productNameHolder=Product_Name.getText().toString();
        Product_Barcode=(EditText)findViewById(R.id.ProductBarcode);
        productBarcodeHolder=Product_Barcode.getText().toString();
        Cassioli_Code=(EditText)findViewById(R.id.CassioliCode);
        CassioliCodeHolder=Cassioli_Code.getText().toString();
        brand_id=(EditText)findViewById(R.id.brandid);
        position_id=(EditText)findViewById(R.id.positionid);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        sp=(Spinner)findViewById(R.id.brandSpinner);
        spl=(Spinner)findViewById(R.id.brandspinnerPosition);
        //listItems.add("Select Brand");
        //listItems_id.add("None");
        //listItems1.add("Select Position");
       // listItems1_id.add("None");
        //listItems1.add("");
        //listItems1_id.add("");
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, listItems);
        adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, listItems1);
        sp.setAdapter(adapter);
        spl.setAdapter(adapter1);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent1, View view, int position1, long l) {

                 Brandidholder = listItems_id.get(position1);

                brand_id.setText(Brandidholder);
                //Toast.makeText(add_product_activity.this,Brandidholder.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                }
        });
        spl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position1, long l) {

                Positionidholder = listItems1_id.get(position1);

                position_id.setText(Positionidholder);
                //Toast.makeText(add_product_activity.this,Positionidholder.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ProductAdd=(Button)findViewById(R.id.add_product);
        ProductAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ProductAdd(Brandidholder,productNameHolder,productBarcodeHolder,CassioliCodeHolder,Positionidholder);
                        openMain();
                    }
                }
        );


        ProductViewall=(Button)findViewById(R.id.view_products);
        ProductViewall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i= new Intent(add_product_activity.this,allproducts_activity.class);
                        startActivity(i);
                    }
                }
        );
        // ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,);

    }
    public void openMain(){
        Intent i= new Intent(this,ScannerActivity.class);
        startActivity(i);
        finish();

    }


    public void ProductAdd(final String BrandID,final String ProductName,final String Barcode,final String CassioliCode,final String PositionID)
    {
        class product_add_class extends AsyncTask<String,Void,String>{

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("BrandID",brand_id.getText().toString());
                hashMap.put("ProductName",Product_Name.getText().toString());
                hashMap.put("Barcode",Product_Barcode.getText().toString());
               hashMap.put("CassioliCode",Cassioli_Code.getText().toString());
                hashMap.put("PositionID",position_id.getText().toString());

                finalResult = httpParse.postRequest(hashMap, HttpURL_save);
                return finalResult;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(add_product_activity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                progressDialog.dismiss();
                Toast.makeText(add_product_activity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        }
        product_add_class product_add = new product_add_class();
        product_add.execute(BrandID,ProductName,Barcode,CassioliCode,PositionID);
    }




    private class getBrands extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;


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
                    //list.add(String.valueOf((Integer) jsonObject.get("BrandID")));
                    list.add((String) jsonObject.get("BrandName"));


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

            //Collections.replaceAll(listItems,("[^a-zA]"), "");


            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    private class getPositions extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;


        @Override
        protected Void doInBackground(Void... params) {
            InputStream is=null;
            String result ="";
            try{
                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(HttpUrl1);
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

                    list.add((String) jsonObject.get("PositionName"));


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
            listItems1.addAll(list);

            //Collections.replaceAll(listItems,("[^a-zA]"), "");


            adapter1.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    private class getBrands_id extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;

        @Override
        protected Void doInBackground(Void... params) {
            InputStream is=null;
            String result ="";
            try{
                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(HttpUrl2);
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
                    //list.add(String.valueOf((Integer) jsonObject.get("BrandID")));
                    list.add((String) jsonObject.get("BrandID"));


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

            //Collections.replaceAll(listItems,("[^a-zA]"), "");


            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    private class getPositions_id extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;

        @Override
        protected Void doInBackground(Void... params) {
            InputStream is=null;
            String result ="";
            try{
                HttpClient httpClient=new DefaultHttpClient();
                HttpPost httpPost=new HttpPost(HttpUrl3);
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
                    //list.add(String.valueOf((Integer) jsonObject.get("BrandID")));
                    list.add((String) jsonObject.get("PositionID"));


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
            listItems1_id.addAll(list);

            //Collections.replaceAll(listItems,("[^a-zA]"), "");


            adapter1.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }





}
