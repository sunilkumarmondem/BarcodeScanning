package cognex.com.cmbcamerademo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class updateProduct_activity extends AppCompatActivity {
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> listItems_id = new ArrayList<>();
    ArrayList<String> listItems1 = new ArrayList<>();
    ArrayList<String> listItems1_id = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;
    ArrayAdapter  adapter2;
    String JsonString;
    //ArrayAdapter<String> adapter1;
    Spinner sp;
    Spinner spl;
    String HttpUrl = "http://192.168.0.127/warehouse_service/getbrands.php";
    String HttpUrl_id="http://192.168.0.127/warehouse_service/getBrandID.php";
    String HttpUrl1 = "http://192.168.0.127/warehouse_service/getpositions.php";
    String HttpUrl1_id = "http://192.168.0.127/warehouse_service/getPositionsID.php";
    ProgressBar progressBar;
    InputStream is = null;

    String line = null;

    String result = null;

    String[] data;
    EditText productID;
    EditText brandId;
    EditText positionId;
    EditText Product_Name;
    EditText Product_Barcode;
    EditText Cassioli_Code;
    Button ProductAdd;
    Button ProductViewall;
    Boolean checkText;
    String finalResult;
    String finalResult1;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String HttpURL_update = "http://192.168.0.127/warehouse_service/updateProduct.php";
    String HttpURL_delete = "http://192.168.0.127/warehouse_service/deleteProduct.php";
    String productNameHolder;
    String productBarcodeHolder;
    String CassioliCodeHolder;
    String IDholder;
    String Brandidholder;
    String Brand_idholder;
    String Positionidholder;
    String Position_idholder;
    String brandidholder;
    String positionidholder;
    Button product_delete;
    Button product_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_activity);
        new getPositions().execute();
        new getBrands().execute();
        new getBrands_id().execute();
        new getPositions_id().execute();
        productID=(EditText)findViewById(R.id.productID1);
        IDholder=getIntent().getStringExtra("ProductID");
        productID.setText(IDholder);
        Product_Name=(EditText)findViewById(R.id.ProductName1);
        productNameHolder=getIntent().getStringExtra("ProductName");
        Product_Name.setText(productNameHolder);
        Product_Barcode=(EditText)findViewById(R.id.ProductBarcode1);
        productBarcodeHolder=getIntent().getStringExtra("Barcode");
        Product_Barcode.setText(productBarcodeHolder);
        Cassioli_Code=(EditText)findViewById(R.id.CassioliCode1);
        CassioliCodeHolder=getIntent().getStringExtra("CassioliCode");
        Cassioli_Code.setText(CassioliCodeHolder);
        Brand_idholder=getIntent().getStringExtra("BrandName");
        Position_idholder=getIntent().getStringExtra("PositionName");
        brandidholder=getIntent().getStringExtra("BrandID");
        positionidholder=getIntent().getStringExtra("PositionID");
        brandId=(EditText)findViewById(R.id.brand__id);
        positionId=(EditText)findViewById(R.id.position__id);
        brandId.setText(brandidholder);
        positionId.setText(positionidholder);


        listItems.add(Brand_idholder);
        listItems_id.add(brandidholder);
        listItems1.add(Position_idholder);
        listItems1_id.add(positionidholder);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        sp=(Spinner)findViewById(R.id.brandSpinner1);
        spl=(Spinner)findViewById(R.id.brandPosition1);

        adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listItems1);
        //adapter2=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,Brandidholder);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listItems);

        spl.setAdapter(adapter1);
        //spl.getSelectedItem().toString();
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String BrandSelected=listItems_id.get(position);
                brandId.setText(BrandSelected);

                //Toast.makeText(add_product_activity.this,Brandidholder.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent1, View view, int position1, long l) {
                //String PositionSelected=parent1.getItemAtPosition(position1).toString();
                String Position1Selected=listItems1_id.get(position1);
                positionId.setText(Position1Selected);
                //Toast.makeText(add_product_activity.this,Positionidholder.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        product_delete=(Button)findViewById(R.id.delete_product);
        product_delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        productDelete(productID.getText().toString());
                        openProducts();
                    }
                }
        );
        product_update=(Button)findViewById(R.id.update_product);
        product_update.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        productUpdate(IDholder,Brandidholder,productNameHolder,productBarcodeHolder,CassioliCodeHolder,Positionidholder);
                        openProducts();
                    }
                }
        );
    }
    public void productUpdate(final String ProductID,final String BrandID,final String ProductName,final String Barcode,final String CassioliCode,final String PositionID){
        class product_update_class extends AsyncTask<String,Void,String>{

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("ProductID",productID.getText().toString());
                hashMap.put("BrandID",brandId.getText().toString());
                hashMap.put("ProductName",Product_Name.getText().toString());
                hashMap.put("Barcode",Product_Barcode.getText().toString());
                hashMap.put("CassioliCode",Cassioli_Code.getText().toString());
                hashMap.put("PositionID",positionId.getText().toString());
                finalResult = httpParse.postRequest(hashMap, HttpURL_update);
                return finalResult;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog1 = ProgressDialog.show(updateProduct_activity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                progressDialog1.dismiss();
                Toast.makeText(updateProduct_activity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }
        }
        product_update_class update= new product_update_class();
        update.execute(ProductID,BrandID,ProductName,Barcode,CassioliCode,PositionID);

    }
    public void openProducts(){
        Intent i = new Intent(this,allproducts_activity.class);
        startActivity(i);
    }
    public void productDelete(final String ProductID){
        class productDeleteClass extends AsyncTask<String,Void,String>{

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("ProductID",productID.getText().toString());
                finalResult = httpParse.postRequest(hashMap, HttpURL_delete);
                return finalResult;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(updateProduct_activity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
                progressDialog.dismiss();

                Toast.makeText(updateProduct_activity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                finish();
            }
        }
        productDeleteClass delete=new productDeleteClass();
        delete.execute(productID.getText().toString());
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

            adapter.notifyDataSetChanged();
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
            }
            try{
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is,"utf-8"));
                StringBuilder sb = new StringBuilder();
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");


                }
                is.close();
                result=sb.toString();
            }catch(IOException e) {
                e.printStackTrace();
            }
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
            adapter1.notifyDataSetChanged();
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
                HttpPost httpPost=new HttpPost(HttpUrl1_id);
                HttpResponse response=httpClient.execute(httpPost);
                HttpEntity entity=response.getEntity();
                is=entity.getContent();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is,"utf-8"));
                StringBuilder sb = new StringBuilder();
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");


                }
                is.close();
                result=sb.toString();
            }catch(IOException e) {
                e.printStackTrace();
            }
            try{
                JSONArray jsonArray=new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
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
            adapter1.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
