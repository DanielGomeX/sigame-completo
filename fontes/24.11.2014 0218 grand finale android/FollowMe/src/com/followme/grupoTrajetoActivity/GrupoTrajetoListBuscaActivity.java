package com.followme.grupoTrajetoActivity;
/**
 * @Activity que irá mostrar uma lista de trajetos de acordo com os parametros de pesquisa.
 * Aqui tem duas alternativas:
 * 1º caso o lider do grupo de trajeto é o mesmo do logado, aí abre a 
 * GrupoTrajetoListBuscaActivity>MotoristaAutorizacaoActivity
 * 
 * 2º caso o lider do grupo de trajeto é diferente do logado, aí abre a
 * GrupoTrajetoListBuscaActivity>GrupoTrajetoListJuntarseActivity
 * 
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


public class GrupoTrajetoListBuscaActivity extends ListActivity implements 
Response.Listener<JSONObject>, 
Response.ErrorListener{
	
	private String nomeGrupoPesquisar;
	private JSONObject jObj;
	private GrupoTrajetoModel grupoTrajetoModel;
	private List<GrupoTrajetoModel> gruposTajetoModel;
	private MotoristaModel logado;
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        instanciaMotoristaLogado();
        //traz o objeto (grupoTrajeto) passado da activity EditarGrupoTrajeto
        setarObjetosRecebidos();
        //gerar o json resposnsável em trazer os motoristas do grupo de trajeto
        String json=gerarJSONgetMotoristas(nomeGrupoPesquisar);
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
				String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/getbynome","send-json", params[0]);
				Log.e("Script", "");
				
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

	private String generationJSON(String nomeGrupoPesquisa)
	{
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try
		{
			jo.put("nome", nomeGrupoPesquisa);
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
	    getMenuInflater().inflate(R.menu.menu_grupo_trajeto_list_busca, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_grupo_trajeto_list_busca_ajuda:	
			
			Toast.makeText(
					getBaseContext(),
					"Selecione um item da lista para visualizar o líder referente a esse grupo",
					Toast.LENGTH_LONG).show();
			
			
			
			break;
		case R.id.menu_grupo_trajeto_list_busca_voltar:
			
			
			finish();
			
			break;
						
		default:
			break;
		}
		return true;
		
	}
	
	
	public void onResponse(JSONObject response) {
		
		gruposTajetoModel = new ArrayList<GrupoTrajetoModel>();

		  try {
		   
			  JSONObject jsonBuscaGrupos = new JSONObject(response.toString());
		      JSONArray jsonBuscaGrupo = jsonBuscaGrupos.getJSONArray("posts");
		 
		      for (int i = 0; i < jsonBuscaGrupo.length(); i++) {
		    	
		      JSONObject jSubObj = jsonBuscaGrupo.getJSONObject(i);
			  JSONObject jsonGrupoItem = jSubObj.getJSONObject("post");		      
		      String idLider = jsonGrupoItem.getString("lider");
		      String horaSaida=jsonGrupoItem.getString("hora_saida");
		      String dataSaida=jsonGrupoItem.getString("data_saida");
		      String nomeGrupoTrajeto=jsonGrupoItem.getString("nome_grupo_trajeto");
		      String email=jsonGrupoItem.getString("email");
		      String localEncontro=jsonGrupoItem.getString("local_encontro");
		      String localDestino=jsonGrupoItem.getString("local_destino");
		      String idGrupoTrajeto=jsonGrupoItem.getString("id_grupo_trajeto");
		      
		      Log.e("Script","teste");
		      
		      grupoTrajetoModel=new GrupoTrajetoModel();
		      grupoTrajetoModel.setIdLider(Integer.parseInt(idLider));
		      grupoTrajetoModel.setHoraSaida(horaSaida);
		      grupoTrajetoModel.setDataSaidaMysql(dataSaida);
		      grupoTrajetoModel.setNomeGrupoTrajeto(nomeGrupoTrajeto);
		      grupoTrajetoModel.setEmail(email);
		      grupoTrajetoModel.setLocalEncontro(localEncontro);
		      grupoTrajetoModel.setLocalDestino(localDestino);
		      grupoTrajetoModel.setId(Integer.parseInt(idGrupoTrajeto));
		      
		    //adiciona à lista
		      gruposTajetoModel.add(grupoTrajetoModel);
		      
		    
		      //setListAdapter(new GrupoTrajetoAdapter(this,gruposTajetoModel));
		  
		  
		    }
		  } catch (Exception e){
		    e.printStackTrace();
		  }
		 
		  
		  setListAdapter(new GrupoTrajetoAdapter(this, gruposTajetoModel));
		

		
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		
		Toast.makeText(this, "Erro!", 
			    Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		
		GrupoTrajetoModel grupoTrajetoModel=new GrupoTrajetoModel();
		grupoTrajetoModel=gruposTajetoModel.get(position);
		
		if(grupoTrajetoModel.getIdLider()==logado.getId())
		{
			Log.e("Script","criador");
			Intent it = new Intent(GrupoTrajetoListBuscaActivity.this,MotoristaAutorizacaoActivity.class);
			it.putExtra("objeto",grupoTrajetoModel);
			it.putExtra("flag", "busca");//envia um flag para poder desabilitar o menu editar do MotoristaAutorizacaoActivity
			startActivity(it);
		}
		else
		{
			Log.e("Script","posso participar");
			Intent it = new Intent(GrupoTrajetoListBuscaActivity.this,GrupoTrajetoListJuntarseActivity.class);
			it.putExtra("objeto",grupoTrajetoModel);
			
			startActivity(it);
		}
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
		nomeGrupoPesquisar=dadosRecebidosParametro.getStringExtra("nomeGrupoTrajeto");
     		
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


	




