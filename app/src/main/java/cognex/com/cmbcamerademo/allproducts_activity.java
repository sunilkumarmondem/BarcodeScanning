package cognex.com.cmbcamerademo;

import android.graphics.ColorSpace;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import static android.R.layout.simple_list_item_1;

public class allproducts_activity extends AppCompatActivity {

    ListView product_listview;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;

    ProgressBar progressBar;
    String HttpUrl = "http://192.168.0.127/warehouse_service/allProducts.php";
    InputStream is = null;
    String line =null;
    String result =null;
    String[] data;
    String[] data1;
    EditText product_search;
    SearchView sv;
    String  selectedText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allproducts_activity);
        product_listview=(ListView)findViewById(R.id.productListview);
        //product_search=(EditText)findViewById(R.id.list_search);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        getData();



       sv=(SearchView)findViewById(R.id.searchproduct);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        product_listview.setAdapter(adapter);
        sv.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String text) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String text) {

                        adapter.getFilter().filter(text);





                        return false;
                    }
                }
        );

        product_listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                          //itemValue    = (String) product_listview.getItemAtPosition(i);
                       String  selectedid = (String) product_listview.getItemAtPosition(i);
                       // Toast.makeText(allproducts_activity.this,selectedid, Toast.LENGTH_LONG).show();
                        //String Templistview = data[i].toString();

                        String[] xyz = selectedid.split("  |\\\n");

                        Intent intent = new Intent(allproducts_activity.this, updateProduct_activity.class);
                        intent.putExtra("ProductID",xyz[0].replaceAll("ID : ",""));
                        intent.putExtra("BrandName",xyz[3].replaceAll("Brand : ",""));
                        intent.putExtra("ProductName",xyz[4].replaceAll("Product : ",""));
                        intent.putExtra("Barcode",xyz[5].replaceAll("Barcode : ",""));
                        intent.putExtra("CassioliCode",xyz[6].replaceAll("CassioliCode : ",""));
                        intent.putExtra("PositionName",xyz[7].replaceAll("Position : ",""));
                        intent.putExtra("BrandID",xyz[1].replace("BrandID : ",""));
                        intent.putExtra("PositionID",xyz[2].replaceAll("PositionID : ",""));
                        startActivity(intent);

                    }
                }
        );
        };


    public void getData(){
        try {
            URL url = new URL(HttpUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            result=sb.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jo ;
            data = new String[ja.length()];

            for(int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);

                data[i]="ID : "+jo.getString("ProductID")+"  "+"BrandID : "+jo.getString("BrandID")+"  "+"PositionID : "+jo.getString("PositionID")+"\n"+"Brand : "+jo.getString("BrandName")+"\n"+ "Product : "+jo.getString("ProductName")+" \n"+"Barcode : "+jo.getString("Barcode")+"\n"+"CassioliCode : "+jo.getString("CassioliCode")+" \n"+"Position : "+jo.getString("PositionName");

                }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
