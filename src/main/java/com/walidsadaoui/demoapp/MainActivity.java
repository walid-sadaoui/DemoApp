package com.walidsadaoui.demoapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity  {

    /**
     * Request codes for permissions
     */
    private static final int REQUEST_CODE_FINE_LOCATION = 1;
    private static final int REQUEST_CODE_COARSE_LOCATION = 2;

    TelephonyManager mTelephonyManager;
    PhoneStateListener mPhoneStateListener;
    CellInfo servingCell;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2g);

        //UI Widgets
        TextView arfcn = (TextView) findViewById(R.id.channelValue);
        TextView mcc = (TextView) findViewById(R.id.mccValue);
        TextView mnc = (TextView) findViewById(R.id.mncValue);
        TextView lac = (TextView) findViewById(R.id.lacValue);
        TextView cid = (TextView) findViewById(R.id.cidValue);
        TextView networkType = (TextView) findViewById(R.id.networkType);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener();

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO
                | PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_SERVICE_STATE | PhoneStateListener.LISTEN_NONE);
        Log.i("GEND'BTS", "Network type = " + getNetworkClass(this));

        networkType.setText(getNetworkClass(this));

        //TODO : create method getServingCell()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_COARSE_LOCATION);
        } else  {
            servingCell = mTelephonyManager.getAllCellInfo().get(0);
        }


        switch (getNetworkClass(this)) {
            case "2G":
                mcc.setText(String.valueOf(((CellInfoGsm) servingCell).getCellIdentity().getMcc()));
                mnc.setText(String.valueOf(((CellInfoGsm) servingCell).getCellIdentity().getMnc()));
                lac.setText(String.valueOf(((CellInfoGsm) servingCell).getCellIdentity().getLac()));
                cid.setText(String.valueOf(((CellInfoGsm) servingCell).getCellIdentity().getCid()));
                //((CellInfoGsm) servingCell).getCellSignalStrength();
                return;
            case "3G":
                servingCell = (CellInfoWcdma) servingCell;
                ((CellInfoWcdma) servingCell).getCellIdentity();
                ((CellInfoWcdma) servingCell).getCellSignalStrength();
                return;
            case "4G":
                servingCell = (CellInfoLte) servingCell;
                ((CellInfoLte) servingCell).getCellIdentity();
                ((CellInfoLte) servingCell).getCellSignalStrength();
                return;
        }



    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("GEND'BTS", "Test onResume()");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS && result != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(this, "Are you running in Emulator ? try a real device.", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
            Log.i("GEND'BTS", "Test ask permission on resume");

        } else {
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_COARSE_LOCATION);
            Log.i("GEND'BTS", "Test ask permission on resume");

        } else {
        }

    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_COARSE_LOCATION);
                    Log.i("GEND'BTS", "Test ask permission");
                }
                return;
            }

            case REQUEST_CODE_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
                    Log.i("GEND'BTS", "Test ask permission");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }


    }

    public CellInfo getServingCell() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_COARSE_LOCATION);
        } else  {
            servingCell = mTelephonyManager.getAllCellInfo().get(0);
        }
        return servingCell;
    }

    public void updateUI() {

    }
}
