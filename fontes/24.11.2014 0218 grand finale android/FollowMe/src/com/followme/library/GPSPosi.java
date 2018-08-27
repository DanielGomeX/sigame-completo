package com.followme.library;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.GpsStatus; // novo
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSPosi extends Service implements LocationListener , GpsStatus.NmeaListener {
 
    public class java {

	}

	private final Context mContext;
 
    // flag for GPS status
    boolean isGPSEnabled = false;
	 
    // flag for network status
    boolean isNetworkEnabled = false;
	 
    // flag for GPS status
    boolean canGetLocation = false;
	 
    Location location;       // location
    double   latitude;       // latitude
    double   longitude;      // longitude
    boolean  fixado = false; // gps fixado
    
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
	 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 2 segundos
	 
    // Declaring a Location Manager
    protected LocationManager locationManager;
	 
    public GPSPosi(Context context) {
        this.mContext = context;
        getLocation();
    }
	 
    public Location getLocation() {
        try {

        	locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

        	locationManager.addNmeaListener(this);
        	
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	 
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	 
            isNetworkEnabled = false;   // Ot�vio inseriu para desativar gps pela internet
            
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) 
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) 
                            {
                               // latitude = location.getLatitude();
                               // longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	 
        return location;
    }
	     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSPosi.this);
        }       
    }
	    
    /**
     * Function to refresh coordenada
     * */
    public void resetCoordenada(){
        if(location != null){
            if (isGPSEnabled) 
            {
            	 locationManager.requestLocationUpdates(
                         LocationManager.GPS_PROVIDER,
                         MIN_TIME_BW_UPDATES,
                         MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
    }
    
    
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
//        if(location != null){
//        	latitude = location.getLatitude();
//        }
	         
        // return latitude
        return latitude;
    }
	     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
//        if(location != null){
//            longitude = location.getLongitude();
//        }
	         
        // return longitude
        return longitude;
    }
	     

    /**
     * Function to get fix coordenada
     * */
    public boolean getFixado(){
        return fixado;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
	     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
	  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
	  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                // clica no botao parar da mainactivity
                mContext.startActivity(intent);
            }
        });
	  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // realiza um click na outra classe
            	dialog.cancel();
            }
        });
	  
        // Showing Alert Message
        alertDialog.show();
    }
	 
    @Override
    public void onLocationChanged(Location location) {


    	// longitude = location.getLongitude();
    	// latitude = location.getLatitude();
    }
	 
    @Override
    public void onProviderDisabled(String provider) {
    }
	 
    @Override
    public void onProviderEnabled(String provider) {
    }
	 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) 
    {
    }
	 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(this);
		locationManager.removeNmeaListener(this);
		super.onDestroy();
	}

	@Override
	public void onNmeaReceived(long timestamp, String nmea) {
        int i,x;
        int c = 0;
        String la  = "";
        String laS = "";
        String lo  = "";
        String loS = "";
        String s = nmea;
        int pos = nmea.indexOf("$GPGGA");
        if (pos > -1){
            Log.d("Nemea",nmea);
        	for (i=0; i < s.length(); i+=1){
        		if (s.charAt(i) == ',') {c+=1;}
        		// achou fixado
        		if (c==6 && s.charAt(i+1) >= '1')
        		{
        			String nSat = String.valueOf(s.charAt(i+3));
        	           if (! String.valueOf(s.charAt(i+4)).equals(","))
        	            nSat = nSat + String.valueOf(s.charAt(i+4));
        			try 
        			{
        			    int nSt = Integer.parseInt(nSat);
        			    if (nSt >= 4)
        			    {
        			    	fixado = true;
        			    	Log.d("Gps fix","fixado "+nSat);
        			    	return;
        			    }
        			    else
        			    {
        			    	fixado = false;
        			    }
        			}
        			catch(NumberFormatException nfe) 
        			{ 
        	            fixado = false;
        			}
        		} 
        		if (c >= 6) {break;}
        	}
            // n�o encontrou retorna falso
            fixado = false;
            Log.d("Gps fix","não fixado  ");
            return;
        }
        // $GPRMC,225446,A,4916.45,N,12311.12,W,000.5,054.7,191194,020.3,E*68
        if (fixado)
        {
	        pos = nmea.indexOf("$GPRMC");
	        if (pos > -1){
	            Log.d("Nemea",nmea);
	            int len = s.length();
	            i = 0;
	            while (i <= len) 
	            {
	            	if (s.charAt(i) == ',') {c+=1;}
	        		// Coordenadas
	        		if (c==3)
	        		{
	        			// latitude
	        			for (x=i+1; x < s.length(); x+=1)
	        			{
	        				if (s.charAt(x) == ',') {break;}
	        				la = la + s.charAt(x);
	        			}
	        			i = x + 1;
	        			// Norte ou Sul
	        			laS =  laS + s.charAt(i);
	        			i+=1;
	        			// Longitude
	        			for (x=i+1; x < s.length(); x+=1)
	        			{
	        				if (s.charAt(x) == ',') {break;}
	        				lo = lo + s.charAt(x);
	        			}
	        			i = x + 1;
	        			// Norte ou Sul
	        			loS =  loS + s.charAt(i);
	        			break;
	        		}
	        		i+=1;
	        	}
	            // n�o encontrou retorna falso
	            latitude  = GpsEncodingToDegrees(la,laS);
	            longitude = GpsEncodingToDegrees(lo,loS);
	            
	            Log.d("Latitude",String.valueOf(latitude));
	        	Log.d("Longitude",String.valueOf(longitude));
	        	
	        	return;
	        }
        }
	}

	double GpsEncodingToDegrees(  String gpsencoding, String sign )
	{
		try 
		{
			if (!gpsencoding.equals(""))
			{
				Double gpsC = Double.parseDouble(gpsencoding);
				int    a = gpsC.intValue(); // inteiros
				double b = gpsC - a;        // decimais
	
				double g = (int)a / 100 ;
				a -= g * 100 ;
				b = (b + a) /60;
				g = g + b;
				if (sign.equals("S"))
				{
					g = g*(-1);
				}
				if (sign.equals("W"))
				{
					g = g*(-1);
				}
				return g;
			}
			else
			{
				return 0;
			}
		} 
		catch (Exception e) 
		{
			 return 0;
        }			
	}
	 
}




