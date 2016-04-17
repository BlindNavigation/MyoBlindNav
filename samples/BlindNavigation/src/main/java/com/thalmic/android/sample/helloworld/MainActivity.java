/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.sample.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

public class MainActivity extends Activity {

    private TextView mLockStateView;
    private TextView mTextViewOne;
    private TextView mTextViewTwo;
    private final String MYO_RIGHT = "MyoRight";
    private final String MYO_LEFT = "MyoLeft";
    private Button leftBuzzButton;
    private Button rightBuzzButton;

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            if(myo.getName().toString().equals(MYO_LEFT)) {
                mTextViewOne.setText(MYO_LEFT + " connected");
                addLeftBuzzButtonListener(myo);
            }

            if(myo.getName().toString().equals(MYO_RIGHT)) {
                mTextViewTwo.setText(MYO_RIGHT + " connected");
                addRightBuzzButtonListener(myo);
            }
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {

            if(myo.getName().toString().equals(MYO_LEFT)) {
                mTextViewOne.setText(MYO_LEFT + " disconnected");
            }

            if(myo.getName().toString().equals(MYO_RIGHT)) {
                mTextViewTwo.setText(MYO_RIGHT + " disconnected");
            }

        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            if(myo.getArm() == Arm.LEFT) {
                mTextViewOne.setText(R.string.arm_left);
            }
            if(myo.getArm() == Arm.RIGHT) {
                mTextViewTwo.setText(R.string.arm_right);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextViewOne = (TextView) findViewById(R.id.myoone);
        mTextViewTwo = (TextView) findViewById(R.id.myotwo);

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        hub.setLockingPolicy(Hub.LockingPolicy.NONE);
        hub.setMyoAttachAllowance(2);
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public void addLeftBuzzButtonListener(final Myo myoLeft) {
        leftBuzzButton = (Button) findViewById(R.id.buzzleft);
        leftBuzzButton.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (myoLeft != null) {
                        myoLeft.vibrate(Myo.VibrationType.SHORT);
                    }
                }
            });
    }

    public void addRightBuzzButtonListener(final Myo myoRight) {
        leftBuzzButton = (Button) findViewById(R.id.buzzright);
        leftBuzzButton.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if(myoRight != null) {
                        myoRight.vibrate(Myo.VibrationType.SHORT);
                    }
                }
            });
    }
}
