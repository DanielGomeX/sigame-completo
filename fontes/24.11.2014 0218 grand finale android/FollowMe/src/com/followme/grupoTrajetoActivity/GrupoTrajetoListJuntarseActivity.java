package com.followme.grupoTrajetoActivity;

/**
 * @Activity que irá mostrar o lider de um determinado trajeto
 * Para executar essa activity:
 * 
 * >MainActivity>
 * >BuscarGrupoTrajetoActivity>
 * >GrupoTrajetoListBuscaActivity>
 * >GrupoTrajetoListJuntarseActivity
 * 				.
 * 				.
 * 
 */
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.followme.R;
import com.followme.BD.MotoristaDA;
import com.followme.R.id;
import com.followme.R.menu;
import com.followme.R.string;
import com.followme.adapter.GrupoTrajetoAdapter;
import com.followme.adapter.GrupoTrajetoBuscaAdapter;
import com.followme.adapter.MotoristaAdapter;
import com.followme.model.GrupoTrajetoModel;
import com.followme.model.MotoristaModel;
import com.followme.motoristaActivity.MotoristaAutorizacaoActivity;
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


public class GrupoTrajetoListJuntarseActivity extends ListActivity implements 
Response.Listener<JSONObject>, 
Response.ErrorListener{
	

	
	private MotoristaModel logado;
	private MotoristaModel motoristaModel;
	private GrupoTrajetoModel grupoTrajetoModel;
	private JSONObject jObj;
	private List<MotoristaModel> motoristasModel;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        instanciaMotoristaLogado();
        //traz o objeto (grupoTrajeto) passado da activity EditarGrupoTrajeto
        setarObjetosRecebidos();
        //gerar o json resposnsável em trazer os motoristas do grupo de trajeto
        
        String json=gerarJSONgetMotoristas(grupoTrajetoModel.getIdLider().toString());
        Log.e("Script", String.valueOf("codigo do grupo de trajeto: "+grupoTrajetoModel.getId()));
        Log.e("Script", String.valueOf("nome do grupo de trajeto: "+grupoTrajetoModel.getNomeGrupoTrajeto()));
        Log.e("Script", String.valueOf("id do lider de trajeto: "+grupoTrajetoModel.getIdLider()));
        
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
				String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/motorista/get","send-json", params[0]);
				
				
				Log.e("Script", resposta);
				
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
			        //String json=gerarJSONgetMotoristas();
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

	private String generationJSON(String idLider)
	{
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try
		{
			jo.put("id_motorista", idLider);
			jo.put("api_key",chave);
			
    		
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
	    getMenuInflater().inflate(R.menu.menu_grupo_trajeto_list_juntarse, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.menu_grupo_trajeto_juntarse_voltar:
			
			//Intent itGrupo = new Intent(this, EditarGrupoTrajetoActivity.class);
			//startActivity(itGrupo);
			finish();
			
			break;
        case R.id.menu_grupo_trajeto_juntarse_ajuda:
			
			//Intent itGrupo = new Intent(this, EditarGrupoTrajetoActivity.class);
			//startActivity(itGrupo);
        	Toast.makeText(
					getBaseContext(),
					"Selecione um item da lista para visualizar os motoristas pertecentes a esse grupo",
					Toast.LENGTH_LONG).show();
			
			break;
						
		default:
			break;
		}
		return true;
		
	}
	
	
	public void onResponse(JSONObject response) {
		
		motoristasModel = new ArrayList<MotoristaModel>();

		  try {
			  
			  JSONObject jsonLideres = new JSONObject(response.toString());
		      JSONArray jsonLider = jsonLideres.getJSONArray("posts");
		 
		      for (int i = 0; i < jsonLider.length(); i++) {
		    	
		      JSONObject jSubObj = jsonLider.getJSONObject(i);
			  JSONObject jsonMotoristaItem = jSubObj.getJSONObject("post");		      
		      String url = jsonMotoristaItem.getString("nome_foto");
		      String email = jsonMotoristaItem.getString("email");
		      String nome_motorista=jsonMotoristaItem.getString("nome_motorista");
		      String idMotorista = jsonMotoristaItem.getString("id_motorista");
		      
		      
		      MotoristaModel motoristaModel=new MotoristaModel();
		      motoristaModel.setUrl(url);
		      motoristaModel.setEmail(email);
		      motoristaModel.setNome(nome_motorista);
		      motoristaModel.setId(Integer.parseInt(idMotorista));
		      /*
		      if(statusAutorizado.equals("0"))
		      {
		    	  statusAutorizado="bloqueado";
		      }
		      else
		      {
		    	  statusAutorizado="liberado";
		      }

		   */
			      
		    //adiciona à lista
		      motoristasModel.add(motoristaModel);
		      
		    
		      setListAdapter(new GrupoTrajetoBuscaAdapter(this,motoristasModel));
		  
		  
		    }
		  } catch (Exception e){
		    e.printStackTrace();
		  }
		 
		  
		 // setListAdapter(new GrupoTrajetoAdapter(this, gruposTajetoModel));
		

		
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
		Log.e("Script", motoristaModel.getEmail());
		Log.e("Script", motoristaModel.getNome());
		Log.e("Script", motoristaModel.getUrl());
		
		Intent it = new Intent(GrupoTrajetoListJuntarseActivity.this,GrupoTrajetoListMotoristasActivity.class);
		it.putExtra("objeto",motoristaModel);
		String idGrupoTrajeto=grupoTrajetoModel.getId().toString();
		it.putExtra("idGrupoTrajeto", idGrupoTrajeto);
		
		startActivity(it);
		
		//aqui vou chamar a activity GrupoTrajetoListMotoristaActivity
		
		
		
		/*
		if(grupoTrajetoModel.getIdLider()==logado.getId())
		{
			Log.e("Script","criador");
			Intent it = new Intent(GrupoTrajetoListJuntarseActivity.this,MotoristaAutorizacaoActivity.class);
			it.putExtra("objeto",grupoTrajetoModel);
			it.putExtra("flag", "busca");//envia um flag para poder desabilitar o menu editar do MotoristaAutorizacaoActivity
			startActivity(it);
		}
		else
		{
			Log.e("Script","posso participar");
		}
		*/
		//Log.e("Script",grupoTrajetoModel.getNomeGrupoTrajeto());
		//motoristaModel=motoristasModel.get(position);

		
		//String url="http://186.202.184.109/tcc2014/sistema/api/grupo/autorizar";
		//String json=generationJSON(motoristaModel,grupoTrajetoModel);
		//new Operacao(this).execute(json,url);
		//new Operacao(GrupoTrajetoListBuscaActivity.this).execute(json); //ver uma forma melhor de atualizar a lista
		
		
		
		}
	
	private void setarObjetosRecebidos()
	{
		
		Intent dadosRecebidosParametro=getIntent();
        Object obj=(Object)dadosRecebidosParametro.getSerializableExtra("objeto");
        grupoTrajetoModel=new GrupoTrajetoModel();
        grupoTrajetoModel=(GrupoTrajetoModel) obj;	
	
	}
	private String gerarJSONgetMotoristas(String nomeGrupoPesquisa)
	{
       
          
        
        String json =generationJSON(nomeGrupoPesquisa);
        return json;
	}
	
	private void  instanciaMotoristaLogado()
	{
		
		MotoristaDA bd = new MotoristaDA(getApplicationContext());
		bd.open();
		logado = bd.getMotorista();
		bd.close();
		
	}
}


	




