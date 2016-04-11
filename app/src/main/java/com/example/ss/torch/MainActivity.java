package com.example.ss.torch;


import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity {
    ToggleButton btnSwitch;

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;

    private int seconds=0;
    private int minutes=0;
    private boolean wasRunning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runtimer();
        btnSwitch = (ToggleButton) findViewById(R.id.on_off);

        //check device support flash light or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature
                        (PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // if device doesn't support flash,show alert message

            AlertDialog alert = new AlertDialog.Builder
                    (MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE,
                    "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            alert.show();
            return;
        }
        getCamera();

        // create button click event to toggle flash on/off
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    turnOnFlash();
                }
            }
        });
    }


    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error ", e.getMessage());
            }
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            btnSwitch.setChecked(false);
            btnSwitch.setTextOff("OFF");
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
            seconds=0;

        }

    }


    // Turning Off flash
    private void turnOffFlash()
    {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            btnSwitch.setChecked(true);
            btnSwitch.setTextOn("ON");
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            seconds=0;



        }
    }

    private void runtimer(){
        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                minutes=(seconds%3600)/60;
                if(minutes==1)
                {
                    minutes=0;
                    seconds=0;
                    alertmessage();
                }
                if(isFlashOn)
                {
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }
        });
    }
    public void alertmessage(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                MainActivity.this);

// Setting Dialog Title
        alertDialog2.setTitle("Alert Message...");

// Setting Dialog Message
        alertDialog2.setMessage("Are you want to turn off the flash light?");
        alertDialog2.setIcon(android.R.drawable.ic_dialog_alert);


// Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                        turnOffFlash();
                        Toast.makeText(getApplicationContext(),
                                "flash light off", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });

// Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Write your code here to execute after dialog
                        turnOnFlash();
                        dialog.cancel();
                    }
                });

// Showing Alert Dialog
        alertDialog2.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning=isFlashOn;

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(wasRunning){
            turnOnFlash();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
