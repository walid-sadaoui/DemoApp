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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    /**
     * Request codes for permissions
     */
    private static final int REQUEST_CODE_FINE_LOCATION = 1;
    private static final int REQUEST_CODE_COARSE_LOCATION = 2;

    /**
     * RecyclerView elements
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * UI Widgets
     */
    TextView arfcn;
    TextView mcc;
    TextView mnc;
    TextView lac;
    TextView cid;
    TextView networkType;

    TelephonyManager mTelephonyManager;
    PhoneStateListener mPhoneStateListener;
    CellInfo servingCell;
    List<CellInfo> neighbouringCells;

    //@SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2g);

        //Locate the UI Widgets
        arfcn = (TextView) findViewById(R.id.channelValue);
        mcc = (TextView) findViewById(R.id.mccValue);
        mnc = (TextView) findViewById(R.id.mncValue);
        lac = (TextView) findViewById(R.id.lacValue);
        cid = (TextView) findViewById(R.id.cidValue);
        networkType = (TextView) findViewById(R.id.networkType);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onCellInfoChanged(List<CellInfo> cellInfo) {
                super.onCellInfoChanged(cellInfo);
                // TODO : update servingCell
                // TODO : update neighbouringCells
                Log.i("GEND'BTS", "Serving Cell updated : "); //+ cellInfo.toString());
                updateUI();
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                // TODO : update servingCell
                // TODO : update neighbouringCells
                Log.i("GEND'BTS", "Signal Strength Changed" + signalStrength.toString());
                updateUI();
            }

            @Override
            public void onCellLocationChanged(CellLocation location) {
                super.onCellLocationChanged(location);
                @SuppressLint("MissingPermission") List<NeighboringCellInfo> neighbouringCellList = mTelephonyManager.getNeighboringCellInfo();
                Log.i("GEND'BTS", "Cell Location Changed" + location.toString());
            }

            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                super.onServiceStateChanged(serviceState);
                Log.i("GEND'BTS", "Service State Changed" + serviceState.toString());
            }
        };

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO
                | PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                | PhoneStateListener.LISTEN_SERVICE_STATE);
        Log.i("GEND'BTS", "Network type = " + getNetworkClass(this));


        getServingCell();
        getNeighbouringCells();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(neighbouringCells);
        mRecyclerView.setAdapter(mAdapter);
        Log.i("GEND'BTS", "Serving Cell : " + servingCell.toString());
        updateUI();


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
        } else {
            servingCell = mTelephonyManager.getAllCellInfo().get(0);
        }
        return servingCell;
    }

    public List<CellInfo> getNeighbouringCells() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_COARSE_LOCATION);
        } else {
            neighbouringCells = mTelephonyManager.getAllCellInfo();
        }
        return neighbouringCells;
    }

    public void updateUI() {

        networkType.setText(getNetworkClass(this));

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
                mcc.setText(String.valueOf(((CellInfoWcdma) servingCell).getCellIdentity().getMcc()));
                mnc.setText(String.valueOf(((CellInfoWcdma) servingCell).getCellIdentity().getMnc()));
                lac.setText(String.valueOf(((CellInfoWcdma) servingCell).getCellIdentity().getLac()));
                cid.setText(String.valueOf(((CellInfoWcdma) servingCell).getCellIdentity().getCid()));
                return;
            case "4G":
                servingCell = (CellInfoLte) servingCell;
                ((CellInfoLte) servingCell).getCellIdentity();
                ((CellInfoLte) servingCell).getCellSignalStrength();
                return;
        }

    }
}
