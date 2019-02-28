package cognex.com.cmbcamerademo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.TestLooperManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class getBrandID extends AsyncTask<String,Void,String> {
    Context context;
    public static AlertDialog alertDialog;
    String result="";
    String dataParsed="";
    String singleParsed="";
    InputStream is = null;
    public getBrandID(Context ctx){
        this.context= (Context) ctx;

    }
    @Override
    protected String doInBackground(String... params) {
        String type= params[0];

        String item_url="http://192.168.0.127/warehouse_service/getBrandID.php";
        if(type.equals("exists")){
            try {

                String BrandName=params[1];

                URL url = new URL(item_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data=URLEncoder.encode("Barcode","UTF-8")+"="+URLEncoder.encode(BrandName,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream= httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    result+=line;
                }
                JSONArray JA= new JSONArray(result);
                for(int i=0;i<JA.length();i++){
                    JSONObject jo = (JSONObject) JA.get(i);
                    singleParsed= (String) jo.get("BrandID");

                    dataParsed+=singleParsed;
                }
                return dataParsed;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(final String dataParsed) {
        String Temp=dataParsed.toString();
        String[] xyz=Temp.split("\n");
        context.startActivity(new Intent(context,getBrandID.class));
        Intent intent=new Intent(context,add_product_activity.class);
        intent.putExtra("BrandID",xyz[0]);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


    }
}
