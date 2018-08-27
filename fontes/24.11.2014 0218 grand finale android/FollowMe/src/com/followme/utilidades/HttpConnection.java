package com.followme.utilidades;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.followme.MainActivity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class HttpConnection {
	
	
    
	
	
    public static  String getSetDataWeb(String url, String method, String data){
	
    	//codigo temporario
    	/*JSONObject jo = new JSONObject();
    	try {
			jo.put("email_lider","g@g.com.br");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	String j=jo.toString();
    	data=j;*/
    	//codigo temporario
	    
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost(url);
		String answer = "";
		
		try{
			ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
			valores.add(new BasicNameValuePair("method", method));
			valores.add(new BasicNameValuePair("json", data));
			
			httpPost.setEntity(new UrlEncodedFormEntity(valores));
			HttpResponse resposta = httpClient.execute(httpPost);
			answer = EntityUtils.toString(resposta.getEntity());
			
			
		}
		catch(NullPointerException e)
		{ 
			e.printStackTrace(); 
			Log.e("Script", "e>>"+e);
			
		}
		catch(ClientProtocolException e)
		{ 
			e.printStackTrace(); 
			Log.e("Script", "e>>"+e);
			
		}
		catch(IOException e)
		{ 
			e.printStackTrace();
			Log.e("Script", "e>>"+e);
			return "3";
			
			
			
		}
		
		return answer;
	}


}
