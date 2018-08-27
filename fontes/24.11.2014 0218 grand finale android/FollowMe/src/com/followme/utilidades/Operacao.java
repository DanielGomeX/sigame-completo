package com.followme.utilidades;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.followme.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;


public class Operacao extends AsyncTask<Void, Void, Void> {
	
	private ProgressDialog progressDialog;
	private Context ct;
	String j;
	
	
	
	public Operacao(Context ct) {
		super();
		this.ct = ct;
	}

	@Override
	protected void onPreExecute()
	{
		progressDialog = new ProgressDialog(ct);
		progressDialog.setMessage("Aguarde...");
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		JSONObject jo = new JSONObject();
		String chave = "afae92e4fb8ca1258431f1a709910ca2dbd2f0e0";
		
				try
    	{
    		jo.put("nome","s");
    		//jo.put("id_grupo", "1");
    		jo.put("api_key",chave);
    		j=jo.toString();
    		
    		
    		
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Jason");
    	}
		//for(int i=0;i<50000000;i++)
		{
			//>>caminho
			//pasta/modulo/controller/action
			String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/getbynome","send-json", j);//servidor remoto;
			Log.e("Script",resposta);
			try {
				JSONObject jObj = new JSONObject(resposta);
				JSONArray jArray = jObj.getJSONArray("posts");
				int qtd=jArray.length();
				JSONObject jSubObj = jArray.getJSONObject(0);
				JSONObject post = jSubObj.getJSONObject("post");
				
				
				String id=post.getString("id");
				//JSONArray post = jSubObj.getJSONArray("id");
				Log.e("Script",id);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		progressDialog.dismiss();
	}
	
	
	
	

}
