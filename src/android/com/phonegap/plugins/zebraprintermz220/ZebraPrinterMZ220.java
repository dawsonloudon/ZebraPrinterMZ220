/**
* Phonegap Zebra Printer Model MZ220 Interface Plugin
* Copyright (c) KPS3 2013
*
*/

package com.phonegap.plugins.zebraprintermz220;

import com.plascotrac.app.hero.Hero;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.os.Looper;

import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.discovery.BluetoothDiscoverer;
import com.zebra.android.discovery.DiscoveredPrinter;
import com.zebra.android.discovery.DiscoveredPrinterBluetooth;
import com.zebra.android.discovery.DiscoveryHandler;

public class ZebraPrinterMZ220 extends CordovaPlugin {

    private static final String FIND = "findPrinters";
    private static final String DEVICES = "getDevices";
    private static final String OPENCONN = "openConnection";
    private static final String CLOSECONN = "closeConnection";
    private static final String PRINT = "print";

    public CallbackContext cbContext;

    private static DiscoveryHandler btDiscoveryHandler = null;

    public ZebraPrinterMZ220() {
        //Log.d("ZebraPrinterMZ220", "Plugin created");
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        this.cbContext = callbackContext;

        if(action.equals(OPENCONN)) {
            try{
                ZebraPrinterConnection thePrinterConn = new BluetoothPrinterConnection(args.getString(0));
                ((Hero) cordova.getActivity()).setConnection(thePrinterConn);
                thePrinterConn.open();
            }
            catch(Exception e) {
                this.cbContext.success("There was an error connecting to the printer");
            }
        }
        else if(action.equals(CLOSECONN)) {
            try{
                ZebraPrinterConnection thePrinterConn = ((Hero) cordova.getActivity()).getConnection();
                thePrinterConn.close();
                this.cbContext.success("true");
            }
            catch(Exception e) {
                //nothing yet
            }
        }
        else if(action.equals(FIND)) {
            find();
        }
        else if(action.equals(DEVICES)) {
            this.cbContext.success(((Hero) cordova.getActivity()).getDeviceList());
        }
        else if(action.equals(PRINT)) {
            print(args);
        }

        return true;
    }

    public void results(String name, String address) {
        try {
            ((Hero) cordova.getActivity()).addDevice(name, address);
        } catch(Exception e) {
            //nothing for now
        }
    }

    public void finished() {
        this.cbContext.success("true");
    }

    public void find(){
        final CallbackContext cbContext = this.cbContext;

        new Thread(new Runnable() {
            public void run() {
                btDiscoveryHandler = new DiscoveryHandler() {

                    public void discoveryError(String message) {
                        cbContext.success("false");
                    }

                    public void discoveryFinished() {
                        cbContext.success("true");
                    }

                    public void foundPrinter(final DiscoveredPrinter printer) {
                        DiscoveredPrinterBluetooth p = (DiscoveredPrinterBluetooth) printer;
                        ((Hero) cordova.getActivity()).addDevice(p.friendlyName, p.address);
                    }
                };

                Looper.prepare();
                try {
                    BluetoothDiscoverer.findPrinters(cordova.getActivity(), btDiscoveryHandler);
                } catch (ZebraPrinterConnectionException e) {
                    //cbContext.success(e.getMessage());
                } catch (InterruptedException e) {
                    //cbContext.success(e.getMessage());
                } finally {
                    Looper.myLooper().quit();
                }
            }
        }).start();
    }

    public void shipIt() {
        this.cbContext.success();
    }

    public void print(JSONArray args){
      final JSONArray stuff = args;
      final ZebraPrinterConnection thePrinterConn = ((Hero) cordova.getActivity()).getConnection();
      new Thread(new Runnable() {
          public void run() {
              try {

                  // Initialize
                  Looper.prepare();

                  String cpclData = stuff.getString(0);

                  // Send the data to printer as a byte array.
                  thePrinterConn.write(cpclData.getBytes());

              } catch (Exception e) {

                  // Handle communications error here.
                  e.printStackTrace();

              } finally {

                    Looper.myLooper().quit();

                    shipIt();
              }
          }
      }).start();
    }

}