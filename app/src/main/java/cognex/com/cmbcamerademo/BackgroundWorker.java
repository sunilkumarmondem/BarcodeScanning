package cognex.com.cmbcamerademo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.TestLooperManager;
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

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    public static AlertDialog alertDialog;
    String result="";
    String dataParsed="";
    String singleParsed="";
    InputStream is = null;

    BackgroundWorker(Context ctx){
        context=ctx;
    }
    @Override
    protected String doInBackground(String... params) {

        String type= params[0];

        String item_url="http://192.168.0.127/warehouse_service/getItems.php";
        if(type.equals("exists")){
            try {

                String Barcode=params[1];

                URL url = new URL(item_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data=URLEncoder.encode("Barcode","UTF-8")+"="+URLEncoder.encode(Barcode,"UTF-8");
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
                singleParsed="ID : "+jo.get("CommesseID")+"  "+ "ProductID : "+jo.get("ProductID")+"\n"+"Barcode : "+jo.get("Barcode")+"\n"+"BrandName : "+jo.get("BrandName")+"\n"+"Commesse : "+jo.get("code")+"\n"+"Quantity : "+jo.get("Qty")+"\n"+"Description : "+ jo.get("Item_desc");

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
   // alertDialog.dismiss();
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Item Details");
    }

    @Override
    protected void onPostExecute(final String dataParsed) {

if(dataParsed!=null){

    alertDialog.setMessage(dataParsed);
    alertDialog.setCancelable(true);
    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Edit", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String Temp=dataParsed.toString();
            String[] xyz=Temp.split("  |\\\n");

            context.startActivity(new Intent(context,update_warehouse_activity.class));
            Intent intent=new Intent(context,update_warehouse_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ProductID",xyz[1].replace("ProductID : ",""));
            intent.putExtra("Barcode",xyz[2].replace("Barcode :",""));
            intent.putExtra("Commesse",xyz[4].replace("Commesse :",""));
            intent.putExtra("Quantity",xyz[5].replace("Quantity :",""));
            intent.putExtra("CommesseID",xyz[0].replace("ID : ",""));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            alertDialog.dismiss();

            }

    });

    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            alertDialog.dismiss();
        }
    });






    alertDialog.show();
    //context.startActivity(new Intent(context,ProductActivity.class));
}
else{
    alertDialog.setMessage("Item details not found in Database");
    alertDialog.show();
}


}
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
