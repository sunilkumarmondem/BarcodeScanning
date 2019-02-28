package cognex.com.cmbcamerademo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cognex.dataman.sdk.CameraMode;
import com.cognex.dataman.sdk.ConnectionState;
import com.cognex.dataman.sdk.PreviewOption;
import com.cognex.dataman.sdk.exceptions.CameraPermissionException;
import com.cognex.mobile.barcode.sdk.ReadResult;
import com.cognex.mobile.barcode.sdk.ReadResults;
import com.cognex.mobile.barcode.sdk.ReaderDevice;
import com.cognex.mobile.barcode.sdk.ReaderDevice.Availability;
import com.cognex.mobile.barcode.sdk.ReaderDevice.OnConnectionCompletedListener;
import com.cognex.mobile.barcode.sdk.ReaderDevice.ReaderDeviceListener;
import com.cognex.mobile.barcode.sdk.ReaderDevice.Symbology;

import java.io.ByteArrayOutputStream;

import static cognex.com.cmbcamerademo.BackgroundWorker.alertDialog;


public class ScannerActivity extends AppCompatActivity implements
        OnConnectionCompletedListener, ReaderDeviceListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    static final int REQUEST_PERMISSION_CODE = 12322;

    public static TextView tvConnectionStatus, tvSymbology, tvCode;
    Button btnScan;
    RelativeLayout rlPreviewContainer;
    ImageView ivPreview;
    Button btnAdd;

    boolean isScanning = false;

    ReaderDevice readerDevice;

    static enum DeviceType {MX, PHONE_CAMERA}
    static final DeviceType[] deviceTypeValues = DeviceType.values();
    static DeviceType deviceTypeFromInt(int i) {
        return deviceTypeValues[i];
    }

    boolean isDevicePicked = false;

    DeviceType param_deviceType = DeviceType.PHONE_CAMERA;

    boolean dialogAppeared = false;

    String selectedDevice = "";

    boolean listeningForUSB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        tvConnectionStatus = (TextView) findViewById(R.id.tvStatus);
        tvSymbology = (TextView) findViewById(R.id.tvSymbol);
        tvCode = (TextView) findViewById(R.id.tvCode);
        tvCode.setMovementMethod(new ScrollingMovementMethod());

        rlPreviewContainer = (RelativeLayout) findViewById(R.id.rlPreviewContainer);

        ivPreview = (ImageView) findViewById(R.id.ivPreview);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnAdd=(Button)findViewById(R.id.buttonadd);
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent=new Intent(ScannerActivity.this,add_product_activity.class);
                        startActivity(intent);
                    }
                }
        );
        btnScan.setEnabled(false);
        btnScan.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {
                if (readerDevice != null) {
                    if (!isScanning) {
                        readerDevice.startScanning();

                        isScanning = true;

                        btnScan.setText("STOP SCANNING");
                    } else {
                        readerDevice.stopScanning();

                        isScanning = false;

                        btnScan.setText("START SCANNING");
                    }
                } else {
                    isScanning = false;
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // initialize and connect to MX/Phone Camera here
        initDevice();


    }

    @Override
    protected void onStop() {
        // call disconnect if device was already connected
        if (readerDevice != null) {
            readerDevice.disconnect();
        }

        // stop listening to device availability to avoid resource leaks
        try {
            readerDevice.stopAvailabilityListening();
        } catch (Exception e) {
            // if availability listening was already stopped or wasn't started
        }

        listeningForUSB = false;

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scanner_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();

            return true;
        }

        if (id == R.id.action_device) {
            pickDevice(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Check result from permission request. If it is allowed by the user, connect to readerDevice

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (readerDevice != null && readerDevice.getConnectionState() != ConnectionState.Connected)
                    readerDevice.connect(ScannerActivity.this);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(((ScannerActivity) this), Manifest.permission.CAMERA)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setMessage("You need to allow access to the Camera")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface,
                                        int i) {
                                    ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA},
                                            REQUEST_PERMISSION_CODE);
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    }

    private void initDevice() {
        if (!isDevicePicked) {
            if (!dialogAppeared) {
                pickDevice(false);
            }

            return;
        }

        switch (param_deviceType.ordinal()) {
            // init connection to MX device
            default:
            case 0:
                readerDevice = ReaderDevice.getMXDevice(this);

                if (!listeningForUSB) {
                    readerDevice.startAvailabilityListening();

                    listeningForUSB = true;
                }

                selectedDevice = "MX Mobile Terminal";
                break;

            // init connection to Phone Camera
            case 1:
                readerDevice = ReaderDevice.getPhoneCameraDevice(this,
                        CameraMode.NO_AIMER, PreviewOption.DEFAULTS,
                        rlPreviewContainer);

                selectedDevice = "Mobile Camera";
                break;
        }

        // set listeners and connect to device
        readerDevice.setReaderDeviceListener(this);
        readerDevice.enableImage(true);
        readerDevice.connect(ScannerActivity.this);

    }

    private void pickDevice(boolean cancelable) {
        if (listeningForUSB) {
            readerDevice.stopAvailabilityListening();

            listeningForUSB = false;
        }

        if (readerDevice != null) {
            readerDevice.disconnect();

            readerDevice = null;
        }


        AlertDialog.Builder devicePickerBuilder = new AlertDialog.Builder(this);
        devicePickerBuilder.setTitle("Select device");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        arrayAdapter.add("MX Scanner");
        arrayAdapter.add("Mobile Camera");

        if (cancelable) {
            devicePickerBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    initDevice();
                }

            });
        }

        devicePickerBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                param_deviceType = deviceTypeFromInt(which);
                isDevicePicked = true;
                dialogAppeared = false;
                initDevice();
            }
            });

        AlertDialog devicePicker = devicePickerBuilder.create();
        devicePicker.setCancelable(false);
        devicePicker.setCanceledOnTouchOutside(false);
        devicePicker.show();

        dialogAppeared = true;
    }

    private void readerDisconnected() {
        Log.d(getClass().getSimpleName(), "onDisconnected");

        tvConnectionStatus.setText("Disconnected");
        tvConnectionStatus.setBackgroundResource(R.drawable.connection_status_bg_disconnected);

        btnScan.setEnabled(false);

        isScanning = false;

        btnScan.setText("START SCANNING");
    }

    private void readerConnected() {
        Log.d(getClass().getSimpleName(), "onConnected");

        tvConnectionStatus.setText(selectedDevice + "\nConnected");
        tvConnectionStatus.setBackgroundResource(R.drawable.connection_status_bg);
        btnScan.setEnabled(true);

        isScanning = false;

        btnScan.setText("START SCANNING");

        readerDevice.setSymbologyEnabled(Symbology.C128, true, null);
        readerDevice.setSymbologyEnabled(Symbology.DATAMATRIX, true, null);
        readerDevice.setSymbologyEnabled(Symbology.UPC_EAN, true, null);
        readerDevice.setSymbologyEnabled(Symbology.QR, true, null);

        // example on using DataManSystem.sendCommand
        readerDevice.getDataManSystem().sendCommand("SET SYMBOL.MICROPDF417 ON");
        readerDevice.getDataManSystem().sendCommand("SET IMAGE.SIZE 0");
    }

    // ReaderDevice listener implementations

    @Override
    public void onConnectionCompleted(ReaderDevice readerDevice, Throwable error) {
        if (error != null) {

            // ask for Camera Permission if necessary
            if (error instanceof CameraPermissionException)
                ActivityCompat.requestPermissions(((ScannerActivity) this), new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);

            readerDisconnected();
        }
    }

    @Override
    public void onConnectionStateChanged(ReaderDevice reader) {
        if (reader.getConnectionState() == ConnectionState.Connected) {
            readerConnected();
        } else if (reader.getConnectionState() == ConnectionState.Disconnected) {
            readerDisconnected();
        }
    }

    @Override
    public void onReadResultReceived(ReaderDevice readerDevice, ReadResults results) {
        readerDevice.enableImageGraphics(true);
        if (results.getCount() > 0) {
            ReadResult result = results.getResultAt(0);
            if (result.isGoodRead()) {
                Symbology sym = result.getSymbology();
                //alertDialog.dismiss();
                if (sym != null) {
                    tvSymbology.setText(sym.getDMCC());
                    /*String type="exists";
                    BackgroundWorker backgroundWorker=new BackgroundWorker(this);
                   backgroundWorker.execute(type,result.getReadString().replaceAll("[^a-zA-Z0-9]", ""));*/
                   // backgroundWorker.execute(type,"5767543");

                } else {
                    tvSymbology.setText("UNKNOWN SYMBOLOGY");
                }

                tvCode.setText(result.getReadString());
            } else {
                tvSymbology.setText("NO READ");

                    String type="exists";
                    BackgroundWorker backgroundWorker=new BackgroundWorker(this);
                    //backgroundWorker.execute(type,result.getReadString().replaceAll("[^a-zA-Z0-9]", ""));
                    backgroundWorker.execute(type,"2222");
                tvCode.setText("");
            }
          Bitmap frame = result.getImage();
           ivPreview.setImageBitmap(frame);
           }
        isScanning = false;
        btnScan.setText("START SCANNING");
    }


    @Override
    public void onAvailabilityChanged(ReaderDevice reader) {
        if (reader.getAvailability() == Availability.AVAILABLE) {
            readerDevice.connect(ScannerActivity.this);
        } else {
            readerDisconnected();
        }
    }
}