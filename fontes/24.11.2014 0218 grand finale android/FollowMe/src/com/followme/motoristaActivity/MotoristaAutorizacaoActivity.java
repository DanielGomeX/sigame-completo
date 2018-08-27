package com.followme.motoristaActivity;


import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.followme.R;
import com.followme.R.id;
import com.followme.R.menu;
import com.followme.R.string;
import com.followme.adapter.MotoristaAdapter;
import com.followme.grupoTrajetoActivity.EditarGrupoTrajetoActivity;
import com.followme.grupoTrajetoActivity.GrupoTrajetoSelecionadoActivity;
import com.followme.model.GrupoTrajetoModel;
import com.followme.model.MotoristaModel;
import com.followme.utilidades.HttpConnection;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MotoristaAutorizacaoActivity extends ListActivity implements 
Response.Listener<JSONObject>, 
Response.ErrorListener{
	
	
	private GrupoTrajetoModel grupoTrajetoModel;
	private MotoristaModel motoristaModel;
	private String flag;
	private JSONObject jObj;
	private List<MotoristaModel> motoristasModel;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //traz o objeto (grupoTrajeto) passado da activity EditarGrupoTrajeto
        setarObjetosRecebidos();
        //gerar o json resposnsável em trazer os motoristas do grupo de trajeto
        String json=gerarJSONgetMotoristas();
        //executa a Thread
        new Operacao(this).execute(json);    
       
        
    }
public class Operacao extends AsyncTask<String, Void, Void> {
		
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
		protected Void doInBackground(String... params) {
			
			if((params.length)==1)//para processar a busca dos motoristas referente ao grupo de trajeto
			{
				String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/getmotoristas","send-json", params[0]);
				
				
				try{
					
					jObj = new JSONObject(resposta);
							
				}catch(Exception erro)
				{
					Log.e("Script", erro.toString());
				}
			}
			if((params.length)==2)//para processar a autorização ou não para um motorista
			{
				String url=params[1];
				String dados=params[0];
				String resposta = HttpConnection.getSetDataWeb(url,"send-json", dados);
				if(resposta != null)
				{
					//traz o objeto passado da activity EditarGrupoTrajeto
			        setarObjetosRecebidos();
			        //gerar o json resposnsável em trazer os motoristas do grupo de trajeto
			        String json=gerarJSONgetMotoristas();
			        //executa a Thread
			         
				}
				
			}				
				
				return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			onResponse(jObj);
			progressDialog.dismiss();
			
		        
		}
		
	
	    
   }

	private String generationJSON(String idGrupo)
	{
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try
		{
			jo.put("id_grupo", idGrupo);
			jo.put("api_key",chave);
			jo.put("autorizado","0");
    		
		}catch(JSONException e1)
		{
			Log.e("Script","erro Jason");
		}
		return jo.toString();
	}
	private String generationJSON(MotoristaModel motorista,GrupoTrajetoModel grupoTrajeto)
	{
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try
		{
			jo.put("id_motorista", motorista.getId());
			jo.put("id_grupo",grupoTrajeto.getId());
			jo.put("api_key",chave);
			
    		
		}catch(JSONException e1)
		{
			Log.e("Script","erro Jason");
		}
		return jo.toString();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.grupo, menu);
	    
	    if(flag.equals("busca"))
	    {
	    	MenuItem itemEditar=menu.getItem(1);
		    MenuItem itemVoltar=menu.getItem(0);
		    itemEditar.setVisible(false);
	    }
	    
	    
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		
	
		switch (item.getItemId()) {
		
		case R.id.grupo_editar:		
			
			Intent it = new Intent(MotoristaAutorizacaoActivity.this,GrupoTrajetoSelecionadoActivity.class);
			it.putExtra("objeto",grupoTrajetoModel);
			startActivity(it);
			
			break;
		case R.id.grupo_voltar:
			if(flag.equals("busca"))
			{
				
				finish();
				break;
			}
			else
			{
				Intent itGrupo = new Intent(this, EditarGrupoTrajetoActivity.class);
				startActivity(itGrupo);
				finish();
			
				break;
			}
						
		default:
			break;
		}
		return true;
		
	}
	
	
	public void onResponse(JSONObject response) {
		
		motoristasModel = new ArrayList<MotoristaModel>();

		  try {
		   
			  JSONObject jsonMotoristas = new JSONObject(response.toString());
		      JSONArray jsonMotorista = jsonMotoristas.getJSONArray("posts");
		 
		      for (int i = 0; i < jsonMotorista.length(); i++) {
		    	
		      JSONObject jSubObj = jsonMotorista.getJSONObject(i);
			  JSONObject jsonMotoristaItem = jSubObj.getJSONObject("post");		      
		      String url = jsonMotoristaItem.getString("nome_foto");
		      String email = jsonMotoristaItem.getString("email");
		      String statusAutorizado = jsonMotoristaItem.getString("autorizado");
		      String idMotorista = jsonMotoristaItem.getString("id_motorista");
		      if(statusAutorizado.equals("0"))
		      {
		    	  statusAutorizado="bloqueado";
		      }
		      else
		      {
		    	  statusAutorizado="liberado";
		      }
		    
		  
		      motoristaModel=new MotoristaModel();
		      motoristaModel.setUrl(url);
		      motoristaModel.setEmail(email);
		      motoristaModel.setAutorizado(statusAutorizado);
		      motoristaModel.setId(Integer.parseInt(idMotorista));
		      //adiciona à lista
		      motoristasModel.add(motoristaModel);
		    }
		  } catch (Exception e){
		    e.printStackTrace();
		  }
		 
		  setListAdapter(new MotoristaAdapter(this, motoristasModel));
		

		
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		
		Toast.makeText(this, "Erro!", 
			    Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		
		MotoristaModel motoristaModel=new MotoristaModel();
		motoristaModel=motoristasModel.get(position);

		
		String url="http://186.202.184.109/tcc2014/sistema/api/grupo/autorizar";
		String json=generationJSON(motoristaModel,grupoTrajetoModel);
		new Operacao(this).execute(json,url);
		new Operacao(MotoristaAutorizacaoActivity.this).execute(json); //ver uma forma melhor de atualizar a lista
		
		
		
		}
	
	private void setarObjetosRecebidos()
	{
		Intent dadosRecebidosParametro=getIntent();
		String tipo=dadosRecebidosParametro.getType();
		try
		{
		
	        Object obj=(Object)dadosRecebidosParametro.getSerializableExtra("objeto");
	        flag=dadosRecebidosParametro.getStringExtra("flag");
	        if(flag.equals("busca"));
	        {
	        	
	        }
	        grupoTrajetoModel=new GrupoTrajetoModel();
	        
	        grupoTrajetoModel=(GrupoTrajetoModel) obj;
	        Log.e("Script","vindo do EditarGrupoTrajetoActivity");
		}
		catch(Exception erro){
			Log.e("Script","erro");
			
		}
		
		
		
	}
	private String gerarJSONgetMotoristas()
	{
        String idGrupoTrajeto=grupoTrajetoModel.getId().toString();
          
        
        String json =generationJSON(idGrupoTrajeto);
        return json;
	}
}


	




