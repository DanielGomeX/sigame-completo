package com.followme;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.BD.MotoristaDA;
import com.followme.library.Encrypt;
import com.followme.model.MotoristaModel;
import com.followme.utilidades.HttpConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends Activity {

	ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		selecionarTipoTrafego();
		progress = (ProgressBar) findViewById(R.id.loginProgressBar);
		progress.setVisibility(View.INVISIBLE);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
	
	//MENU
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_cadastrar:
			Intent it = new Intent(this, CadastroActivity.class);
			startActivity(it);
			break;
		case R.id.menu_entrar:
						
		    processaLogin();
					
			break;
		default:
			break;
		}
		return true;
		
	}
    
    //GERA JSON
    private String geraJSON(String email, String senha)
    {
    	JSONObject jo = new JSONObject();
    	try
    	{
    		jo.put("email", email);
    		jo.put("senha",senha);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Json");
    	}
    	return jo.toString();
    }

    
	private void processaLogin() {

		EditText editEmail = (EditText) findViewById(R.id.emailLogin);
		EditText editSenha = (EditText) findViewById(R.id.senhaLogin);

		String email = editEmail.getText().toString();
		String senha = editSenha.getText().toString();
		
		if(email.equals("") || senha.equals("")){
			Toast.makeText(
					getBaseContext(),
					"Digite um email e senha.",
					Toast.LENGTH_SHORT).show();
		}
		else{
			try {
				progress.setVisibility(View.VISIBLE);
				
				String json=geraJSON(email, Encrypt.sha1Hash(senha));
				new ReadJsonAsyncTask().execute(json);
				
			} catch (Exception e) {
			
				e.printStackTrace();
			} 
		}
	}
	
	private class ReadJsonAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			
			return HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/motorista/login", "send-json", params[0]);//servidor remoto
			
		}

		protected void onPostExecute(String result) {
			Log.e("string", result);
			MotoristaDA bd = new MotoristaDA(getApplicationContext());
			
			try {
				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("posts");
				JSONObject jSubObj = jArray.getJSONObject(0);
				JSONObject post = jSubObj.getJSONObject("post");

				try{
					MotoristaModel motorista = new MotoristaModel(Integer.parseInt(post
							.getString("id_motorista")),
							post.getString("nome_motorista"),
							post.getString("nascimento"), 
							post.getString("email"),
							post.getString("senha"),
							1);

					bd.open();
					bd.gravaMotorista(motorista);
					bd.close();
				}
				catch(JSONException e)
				{
					if(post.getString("erro") != null){
						switch (post.getInt("codigo")) {
						case 1:
							throw new Exception("E-mail ou senha inválidos");
						//
						case 2:
							Toast.makeText(
												getBaseContext(),
												"Seu acesso está bloqueado.",
												Toast.LENGTH_SHORT).show();
							
							
							//
						default:
							throw new Exception("Erro interno da API");
			
						}
					}
				}
				
				Intent it = new Intent(getBaseContext(), MainActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(it);
				finish();
				
			}
			catch(JSONException e1){
				e1.printStackTrace();
				
				String erro = getResources().getString(R.string.erro_conexao);
				
				Toast.makeText(
					getBaseContext(),
					erro,
					Toast.LENGTH_SHORT).show();
				
				selecionarTipoTrafego();
				progress.setVisibility(View.INVISIBLE);
			}
			catch (Exception e2) {
				
				e2.printStackTrace();
				Toast.makeText(
					getBaseContext(),
					e2.getLocalizedMessage(),
					Toast.LENGTH_SHORT).show();
				
				progress.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	//desabilita rede de dados
	private Boolean desabilitaRedeDados(Context context, boolean enabled) throws Exception {
	    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class<?> conmanClass;
	    conmanClass = Class.forName(conman.getClass().getName());
	    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
	    iConnectivityManagerField.setAccessible(true);
	    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
	    final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
	    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
	    setMobileDataEnabledMethod.setAccessible(true);
	 
	    if (setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled) != null){
	        return true;
	    } else {
	        return false;
	    }
	}
	
	//habilita rede de dados
	private Boolean habilitaRedeDados(Context context, boolean enabled) throws Exception {
	    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class<?> conmanClass;
	    conmanClass = Class.forName(conman.getClass().getName());
	    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
	    iConnectivityManagerField.setAccessible(true);
	    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
	    final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
	    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
	    setMobileDataEnabledMethod.setAccessible(true);
	 
	    if (setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled) != null){
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private void habilitaWiFi()
	{
		final WifiManager wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		
		if (!wifi.isWifiEnabled()) {
			
			wifi.setWifiEnabled(true);
			
		}
				
	}
	private void desabilitaWiFi()
	{
		final WifiManager wifi = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		//
		if (wifi.isWifiEnabled()) {
			
			wifi.setWifiEnabled(false);
			
		}
						
		
	}
	
	//MÉTODO PARA ESCOLHA DE QUAL TIPO DE REDE(WI-FI OU DADOS MÓVEIS) O MOTORISTA VAI UTILIZAR
	private void selecionarTipoTrafego()
	{
		final CharSequence[] items={"Wi-Fi","Rede de Dados"};
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
    	builder.setTitle("Selecione a opção desejada");
    	builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			
				if("Wi-Fi".equals(items[which]))
				{
					habilitaWiFi();
					try {
						desabilitaRedeDados(getBaseContext(), false);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				
				}else if("Rede de Dados".equals(items[which]))
				{
					desabilitaWiFi();
					try {
						habilitaRedeDados(getBaseContext(), true);
					} catch (Exception e) {
					
						e.printStackTrace();
					}
					
				}
				
			}
		});
    	builder.show();

		
	}


}
