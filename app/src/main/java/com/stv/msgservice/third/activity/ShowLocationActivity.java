package com.stv.msgservice.third.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.utils.NativeFunctionUtil;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.Circle;
import com.tencent.mapsdk.raster.model.CircleOptions;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class ShowLocationActivity extends BaseActivity<IMyLocationAtView, MyLocationAtPresenter> implements IMyLocationAtView, TencentLocationListener, SensorEventListener {

    private TencentMap mTencentMap;
    private double mLat;
    private double mLong;
    private String mTitle;

    @BindView(R2.id.confirmButton)
    Button mBtnToolbarSend;
    @BindView(R2.id.navigationButton)
    ImageButton mNavigationButton;
    @BindView(R2.id.rlMap)
    RelativeLayout mRlMap;
    @BindView(R2.id.map)
    MapView mMap;
    @BindView(R2.id.ibShowLocation)
    ImageButton mIbShowLocation;
    private Marker myLocation;
    private Circle accuracy;
    private TencentLocationManager mLocationManager;
    private TencentLocationRequest mLocationRequest;

    @Override
    public void initView() {
//        mBtnToolbarSend.setVisibility(View.VISIBLE);
        mTencentMap = mMap.getMap();
        mBtnToolbarSend.setVisibility(View.INVISIBLE);
        mNavigationButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        mLat = getIntent().getDoubleExtra("Lat", 0);
        mLong = getIntent().getDoubleExtra("Long", 0);
        mTitle = getIntent().getStringExtra("title");
//        setToolbarTitle(mTitle);
        mTencentMap.setCenter(new LatLng(mLat, mLong));
        mTencentMap.setZoom(16);
        mLocationManager = TencentLocationManager.getInstance(this);
        mLocationRequest = TencentLocationRequest.create();

        Marker marker = mTencentMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLat, mLong))
                .title(mTitle)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .defaultMarker())
                .draggable(false));
        marker.showInfoWindow();
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mNavigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NativeFunctionUtil.open3rdMapNavigation(mTitle, mLat, mLong, ShowLocationActivity.this);
            }
        });
        mIbShowLocation.setOnClickListener(v -> requestLocationUpdate());

    }

    private void requestLocationUpdate() {
        //????????????
        int error = mLocationManager.requestLocationUpdates(mLocationRequest, ShowLocationActivity.this);
        switch (error) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    @Override
    protected MyLocationAtPresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.location_activity_show_location;
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if (isFinishing()) {
            return;
        }
        if (i == tencentLocation.ERROR_OK) {
            LatLng latLng = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
            if (myLocation == null) {
                myLocation = mTencentMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.arm)).anchor(0.5f, 0.8f));
            }
            if (accuracy == null) {
                accuracy = mTencentMap.addCircle(new CircleOptions().center(latLng).radius(tencentLocation.getAccuracy()).fillColor(0x440000ff).strokeWidth(0f));
            }
            myLocation.setPosition(latLng);
            accuracy.setCenter(latLng);
            accuracy.setRadius(tencentLocation.getAccuracy());
            mTencentMap.animateTo(latLng);
            mTencentMap.setZoom(16);
            //????????????
            mLocationManager.removeUpdates(this);
        } else {
        }

    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        if (isFinishing()) {
            return;
        }
        String desc = "";
        switch (i) {
            case STATUS_DENIED:
                desc = "???????????????";
                break;
            case STATUS_DISABLED:
                desc = "????????????";
                break;
            case STATUS_ENABLED:
                desc = "????????????";
                break;
            case STATUS_GPS_AVAILABLE:
                desc = "GPS???????????????GPS????????????????????????????????????";
                break;
            case STATUS_GPS_UNAVAILABLE:
                desc = "GPS?????????????????? gps ????????????????????????????????????";
                break;
            case STATUS_LOCATION_SWITCH_OFF:
                desc = "??????????????????????????????android M??????????????????????????????wifi??????";
                break;
            case STATUS_UNKNOWN:
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_navigation) {
//            showConversationInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (myLocation != null) {
//            myLocation.setRotation(event.values[0]);
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public RecyclerView getRvPOI() {
        return null;
    }
}
