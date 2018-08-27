package com.followme;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.R;
import com.followme.BD.MotoristaDA;
import com.followme.grupoTrajetoActivity.BuscarGrupoTrajetoActivity;
import com.followme.grupoTrajetoActivity.CriarGrupoTrajetoActivity;
import com.followme.grupoTrajetoActivity.EditarGrupoTrajetoActivity;
import com.followme.library.MarkerList;
import com.followme.motoristaActivity.MotoristaActivity;
import com.followme.utilidades.HttpConnection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, 
	GooglePlayServicesClient.OnConnectionFailedListener, OnMyLocationChangeListener{
    
	//imagens
	private Handler handler = new Handler();
	private LinearLayout ll;
	private View carregarGrupoButton;
	
    //my location
    private LocationClient mLocationClient;
    private int flagAtualizacao;
    final int TEMPO_ATUALIZACAO = 1;
    
    //bd
    private MotoristaDA bd;
    
    //mapa
    private GoogleMap map;
    
    //trajeto
    private String json;
    private MarkerList listMotoristas;
    private String imei;
    private int id_logado;
    
    //dados vindo da MainListCarregarGrupoTrajetoActivity
    private String idGrupoTrajeto = null;
    private String nomeGrupoTrajeto;
    
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	verificaStatusGPS();    	
    	
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
    	mLocationClient = new LocationClient(this, this, this);
    	
    	//verifica login
  		bd = new MotoristaDA(getApplicationContext());
    	bd.open();
    	if(bd.getMotorista() == null){
    		bd.close();
    		Intent itLogin = new Intent(this, LoginActivity.class);
			startActivity(itLogin);
			finish();
    	}
    	else{
    		//tenta setar os dados da MainListCarregarGrupoTrajetoActivity
    		try{
    			Intent dadosRecebidosParametro = getIntent();
    	        String status=dadosRecebidosParametro.getStringExtra("flag");
    	        if(status.equals("true"))
    	        {
    	        	String idGrupoTrajeto=dadosRecebidosParametro.getStringExtra("idGrupoTrajeto");
    	        	String nomeGrupoTrajeto=dadosRecebidosParametro.getStringExtra("nomeGrupoTrajeto");
    	        	this.idGrupoTrajeto=idGrupoTrajeto;
    	        	this.nomeGrupoTrajeto=nomeGrupoTrajeto;
    	        
    	        }
    	        	
    			
    		}catch (Exception erro)
    		{
    			Log.e("Script","erro");
    		}
    		
	    	id_logado = bd.getMotorista().getId();
	    	bd.close();
	    	
	        //inicialização do mapa
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();  
			
			//tratamento de exception para emulador
			try{
				map.setMyLocationEnabled(true);
				map.setTrafficEnabled(true);
				
				if(!(idGrupoTrajeto==null))
				{
					iniciaTrajeto(Integer.parseInt(idGrupoTrajeto));
					Toast.makeText(
							getBaseContext(),
							"Grupo de Trajeto " +nomeGrupoTrajeto+ " carregado",
							Toast.LENGTH_SHORT).show();
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
    	}
    }

    private void verificaStatusGPS() {
    	
    	String provider = Settings.Secure.getString(getContentResolver(), 
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED); 
    	
    	if(!provider.contains("gps"))
    	{
    		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS); 
			startActivityForResult(intent, 1); 
    	}

	
	}

	/*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        if(isGooglePlayServicesAvailable()){
            mLocationClient.connect();
        }

    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
             
        SubMenu subDadosMotorista=menu.addSubMenu("Seus dados");
        subDadosMotorista.add(0,4,0,"Exibir Dados");
        subDadosMotorista.add(0,5,0,"Editar Dados");
        
        SubMenu subGrupoTrajeto=menu.addSubMenu("Grupo de Trajeto");
        subGrupoTrajeto.add(0,6,0,"Criar Grupo de Trajeto");
        subGrupoTrajeto.add(0,7,0,"Editar Grupo de Trajeto");
        subGrupoTrajeto.add(0,8,0,"Buscar Grupo de Trajeto");
        subGrupoTrajeto.add(0,9,0,"Finalizar Grupo de Trajeto");
        
        menu.add(0,10,0,"Logoff");
        menu.add(0,11,0,"Sair");
        
       
        
        
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case 4:
			Intent itMotorista = new Intent(this, MotoristaActivity.class);
			startActivity(itMotorista);
			break;
		case 5:
			Intent itEdicao = new Intent(this, EdicaoActivity.class);
			startActivity(itEdicao);
			break;
		case 6:
			startActivity(new Intent(MainActivity.this,CriarGrupoTrajetoActivity.class));
			break;
		case 7:
			startActivity(new Intent(MainActivity.this,EditarGrupoTrajetoActivity.class));
			break;
		case 8:
			startActivity(new Intent(MainActivity.this,BuscarGrupoTrajetoActivity.class));
			break;
		case 9:
			finalizaTrajeto();
			
			break;
		case 10:
			bd.open();
			bd.logoffMotorista();
			bd.close();
			
			Intent itLogoff = new Intent(this, LoginActivity.class);
			startActivity(itLogoff);
			break;
		
		case 11:
			finish();
			break;
		
					
			default:
				break;
		}
		return true;
		
	}
    
    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        }
        else{
        	Toast.makeText(
					getBaseContext(),
					"Google Play services is not available.",
					Toast.LENGTH_SHORT).show();
        	return false;
        }
        
    }
    
    //--------------------------------------------funcões de controle de trajeto--------------------------------------------
    
	public void carregarGrupos (View v){
    	Intent it = new Intent(MainActivity.this,MainListCarregarGrupoTrajetoActivity.class);
		startActivity(it);
		finalizaTrajeto();
		finish();
    }
    
    private void iniciaTrajeto(int id_grupo){
    	
    	if(Conectado(getApplicationContext()) == true){
    		//remove botão de carregar grupos
    		carregarGrupoButton = findViewById(R.id.imageButtonCarregaGrupos);
    		((LinearLayout)carregarGrupoButton.getParent()).removeView(carregarGrupoButton);
    		
    		
    		flagAtualizacao = 0;
            map.setOnMyLocationChangeListener(this);
    		
    		//cadastra celular
    		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
    		imei = mngr.getDeviceId();
    		
    		String smartphoneJson = generateSmartphoneJSON(id_logado, id_grupo);
    		new PutPostAsyncTask().execute("trajeto", "post", smartphoneJson);
    		
    		//carrega grupo
    		listMotoristas = new MarkerList();
    		json = generateGetJSON(id_grupo);
			new GetPosicoesAsyncTask().execute(json);
			
		}
    	else{
    		Toast.makeText(
					getBaseContext(),
					"Erro ao iniciar o grupo. Verifique sua conexão e tente novamente.",
					Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void finalizaTrajeto(){
    	try{
	    	map.setOnMyLocationChangeListener(null);
	    	map.clear();
	    	listMotoristas.clear();
	    	ll.removeAllViews();
	    	ll.addView(carregarGrupoButton);
    	}
    	catch(Exception e){}
    	
    }
    
    private boolean Conectado(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                    return true;
            } else if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                    return true;
            } else {
                    return false;
            }
        } catch (Exception e) {
                return false;
        }
    }
    
    private String generateSmartphoneJSON(int id_logado, int id_grupo)
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("id_grupo", id_grupo);
    		jo.put("id_motorista", id_logado);
      		jo.put("api_key",chave);
      		jo.put("imei",imei);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Json");
    	}
    	return jo.toString();
    }
    
    private String generateGetJSON(int id_grupo)
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("id_grupo", id_grupo);
      		jo.put("api_key",chave);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Json");
    	}
    	return jo.toString();
    }
    
    private String generateSendJSON(double lat, double lng)
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("api_key",chave);
    		jo.put("imei", imei);
      		jo.put("lat",lat);
      		jo.put("lng",lng);
      		
      		//data
      		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      		String currentDateandTime = sdf.format(new Date());
      		
      		jo.put("data",currentDateandTime);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Json");
    	}
    	return jo.toString();
    }
    
  //--------------------------------------------acesso a web services--------------------------------------------
    
    private void loadImg(String foto, int color){
		final String param = foto;
		final int id = color;
		final LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		
		new Thread(){
			public void run(){	
				try{
					URL url = new URL(param);
					HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
					InputStream input = conexao.getInputStream();
					final Bitmap img = BitmapFactory.decodeStream(input);
					final Bitmap colorBar = BitmapFactory.decodeResource(getResources(), id);
					
					handler.post(new Runnable(){
			 			public void run(){
			 				ll = (LinearLayout) findViewById(R.id.fotos_layout);
			 				
			 				ImageView ivBar = new ImageView(getBaseContext());
			 				ivBar.setImageBitmap(colorBar);
			 				ivBar.setLayoutParams(params);
			 				
			 				ImageView ivFoto = new ImageView(getBaseContext());
			 				ivFoto.setImageBitmap(img);
			 				ivFoto.setAdjustViewBounds(true);
			 				ivFoto.setLayoutParams(params);
			 				
			 				ll.addView(ivBar);
			 				ll.addView(ivFoto);
			 			}
			 		});
				}
				catch(Exception e2){
					e2.printStackTrace();
				}
			}
		}.start();
	}
    
    private class PutPostAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String url = "http://186.202.184.109/tcc2014/sistema/api/" + params[0] + "/" + params[1];
			return HttpConnection.getSetDataWeb(url, "send-json", params[2]);
		}

		protected void onPostExecute(String result) {
			try {
			
				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("posts");
				JSONObject jSubObj = jArray.getJSONObject(0);
				JSONObject post = jSubObj.getJSONObject("post");
			
				Log.e("envio", post.getString("sucesso"));
			}
			catch(IndexOutOfBoundsException e1){
				e1.printStackTrace();
				
				String erro = getResources().getString(R.string.erro_conexao);
				
				Toast.makeText(
					getBaseContext(),
					erro,
					Toast.LENGTH_SHORT).show();
			}
			catch(JSONException e2){
				e2.printStackTrace();
				
				String erro = getResources().getString(R.string.erro_conexao);
				
				Toast.makeText(
					getBaseContext(),
					erro,
					Toast.LENGTH_SHORT).show();
				
			}
			catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				Toast.makeText(
					getBaseContext(),
					e3.getLocalizedMessage(),
					Toast.LENGTH_SHORT).show();
				
			}
		}
	}
    
    /*
     * Chamada pelo onMyLocationChange
     * faz chamada ao servidor para pegar a última posição do motorista
     * caso motorista não esteja na arrayList listMotorista, adciona, se não, atualiza a posição
     */
    private class GetPosicoesAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/trajeto/get", "send-json", params[0]);
		}
		
			
		protected void onPostExecute(String result) {
			try {
			
				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("posts");
				
				int qtdRegistros = jArray.length();
				for(int i=0; i<qtdRegistros; i++)
				{
					JSONObject jSubObj = jArray.getJSONObject(i);
					JSONObject post = jSubObj.getJSONObject("post");
					
					try{
						//se o motorista já está contido no trajeto, atualiza posição, se não, insere.
						if(listMotoristas.contains(post.getInt("motorista_id"))){
							listMotoristas.get(post.getInt("motorista_id"))
								.setPosition(new LatLng(post.getDouble("latitude"), post.getDouble("longitude")));
						}
						else{
							//verifica se motorista é o logado
							if(post.getInt("motorista_id") != id_logado){
								//define cor do marcador
						    	float color;
						    	int barId;
						    	int size = listMotoristas.size();
								switch (size) {
								case 0:
									color = BitmapDescriptorFactory.HUE_ORANGE;
									barId = R.drawable.orange_bar;
									break;
								case 1:
									color = BitmapDescriptorFactory.HUE_CYAN;
									barId = R.drawable.cyan_bar;
									break;
								case 2:
									color = BitmapDescriptorFactory.HUE_GREEN;
									barId = R.drawable.green_bar;
									break;
								case 3:
									color = BitmapDescriptorFactory.HUE_MAGENTA;
									barId = R.drawable.magenta_bar;
									break;
								case 4:
									color = BitmapDescriptorFactory.HUE_AZURE;
									barId = R.drawable.azure_bar;
									break;
								case 5:
									color = BitmapDescriptorFactory.HUE_YELLOW;
									barId = R.drawable.yellow_bar;
									break;
								case 6:
									color = BitmapDescriptorFactory.HUE_ROSE;
									barId = R.drawable.rose_bar;
									break;
								case 7:
									color = BitmapDescriptorFactory.HUE_VIOLET;
									barId = R.drawable.violet_bar;
									break;
	
								default:
									color = BitmapDescriptorFactory.HUE_RED;
									barId = R.drawable.red_bar;
									break;
								}
								
								Marker marker = map.addMarker(new MarkerOptions()
								     .position(new LatLng(post.getDouble("latitude"), post.getDouble("longitude")))
								     .title(post.getString("nome_motorista"))
								     .icon(BitmapDescriptorFactory.defaultMarker(color)));
								
								listMotoristas.add(post.getInt("motorista_id"), marker);
								
								//imagem
								loadImg(post.getString("nome_foto"), barId);
							}
							else{
								Marker marker = map.addMarker(new MarkerOptions()
							     	.position(new LatLng(post.getDouble("latitude"), post.getDouble("longitude")))
							     	.visible(false));
							
								listMotoristas.add(post.getInt("motorista_id"), marker);
								
								loadImg(post.getString("nome_foto"), R.drawable.blue_bar);
							}
						}
						
						Log.e("recebimento", "OK");
					}
					catch(Exception e2){
						Log.e("position", e2.getMessage());
					}
				}
				
			}
			catch(IndexOutOfBoundsException e1){
				e1.printStackTrace();
				
				String erro = getResources().getString(R.string.erro_conexao);
				
				Toast.makeText(
					getBaseContext(),
					erro,
					Toast.LENGTH_SHORT).show();
			}
			catch(JSONException e2){
				e2.printStackTrace();
				
				String erro = getResources().getString(R.string.erro_conexao);
				
				Toast.makeText(
					getBaseContext(),
					erro,
					Toast.LENGTH_SHORT).show();
				
			}
			catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				Toast.makeText(
					getBaseContext(),
					e3.getLocalizedMessage(),
					Toast.LENGTH_SHORT).show();
				
			}
		}

    }

    
    
    //--------------------------------------------métodos das interfaces--------------------------------------------

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.e("location", "conectado");
        Location location = mLocationClient.getLastLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        
        CameraPosition currentPlace = new CameraPosition.Builder()
	    	.target(latLng)
	    	.bearing(location.getBearing())
	        .tilt(65.5f)
	        .zoom(17)
	        .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
           Toast.makeText(getApplicationContext(), "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub
		if(location!=null){
	        CameraPosition currentPlace = new CameraPosition.Builder()
		    	.target(new LatLng(location.getLatitude(), location.getLongitude()))
		    	.bearing(location.getBearing())
		        .tilt(65.5f)
		        .zoom(17)
		        .build();
	
			map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

			//faz envio de coordenadas somente se Accuracy for menor que 100m 
			if(location.getAccuracy() < 100){
				flagAtualizacao ++;
	
				if(flagAtualizacao == TEMPO_ATUALIZACAO){
					//envia localização
					String sendJson = generateSendJSON(location.getLatitude(), location.getLongitude());
					new PutPostAsyncTask().execute("trajeto", "put", sendJson);
					
					//recebe localização
					new GetPosicoesAsyncTask().execute(json);
					flagAtualizacao = 0;
				}
			}
			
		}
		
	}
	
	public void turnGPSOn()
	{
		Context ctx=getBaseContext();
	     Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
	     intent.putExtra("enabled", true);
	     
	     ctx.sendBroadcast(intent);

	    String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        ctx.sendBroadcast(poke);


	    }
	}
	
	
}
    