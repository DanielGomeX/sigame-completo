package com.followme;

/**
 * @Activity que irá mostrar uma lista de grupo de trajeto que retorna
 * todos os grupos que o motorista é lider ou participa
 * é acionado quando pressiona o botão "carregar grupo" no rodapé do mapa
 * 
 * >MainActivity>
 * >MainListCarregarGrupoTrajetoActivity	.
 * 				.
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
import com.followme.adapter.GrupoTrajetoListMotoristaAdapter;
import com.followme.adapter.MainListGrupoTrajetoAdapter;
import com.followme.adapter.MotoristaAdapter;
import com.followme.grupoTrajetoActivity.GrupoTrajetoListBuscaActivity;
import com.followme.grupoTrajetoActivity.GrupoTrajetoListJuntarseActivity;
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


public class MainListCarregarGrupoTrajetoActivity extends ListActivity implements 
Response.Listener<JSONObject>, 
Response.ErrorListener{
	

	private List<GrupoTrajetoModel> gruposTrajetoModel;
	private MotoristaModel logado;
	private MotoristaModel motoristaModel;
	private GrupoTrajetoModel grupoTrajetoModel;
	private JSONObject jObj;
	private List<MotoristaModel> motoristasModel;
	private String idGrupoTrajeto=null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        instanciaMotoristaLogado();
        
        
        
        //gerar o json resposnsável em trazer os grupos de trajeto
        String json=gerarJSONgetMotoristas(String.valueOf(logado.getId()));
        
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
			
			//carrega o grupo de trajeto que o motorista participa
			if((params.length)==1)///
			{
				String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/get-by-participante","send-json", params[0]);
				
				
				Log.e("Script", resposta);
				
				try{
					
					jObj = new JSONObject(resposta);
							
				}catch(Exception erro)
				{
					Log.e("Script", erro.toString());
				}
			}
			
			//juntar-se ao grupo
			if((params.length)==2)
			{
				String url=params[1];
				
				
				String dados=params[0];
				String resposta = HttpConnection.getSetDataWeb(url,"send-json", dados);
				String json=gerarJSONgetMotoristas(idGrupoTrajeto);
				String resposta2=HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/getmotoristas","send-json", json);
				
				if(resposta != null)
				{
					//traz o objeto passado da activity EditarGrupoTrajeto
			        setarObjetosRecebidos();////
			       
			      //gerar o json resposnsável em trazer os motoristas do grupo de trajeto			        
			        try {
						jObj = new JSONObject(resposta2);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        Log.e("Script", json);
			        
			         
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
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.menu_main_list_carregar_grupo_trajeto, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_list_carregar_grupo_trajeto_voltar:
			finish();
			Intent it = new Intent(MainListCarregarGrupoTrajetoActivity.this,MainActivity.class);
			startActivity(it);
			
			break;
		
		
		
		case R.id.lblNomeGrupoActMainList:		
			
			finish();
					
			break;
		case R.id.menu_grupo_trajeto_list_motorista_juntar:
			
			String idMotorista=String.valueOf(logado.getId());
			String json=generationJSON(idGrupoTrajeto,idMotorista);
			String url="http://186.202.184.109/tcc2014/sistema/api/grupo/join";
						
			new Operacao(this).execute(json,url);
			
			
			
			
			
			break;
        case R.id.menu_grupo_trajeto_list_motorista_sair:
			
			
        	String idMotoristaSair=String.valueOf(logado.getId());
			String jsonSair=generationJSON(idGrupoTrajeto,idMotoristaSair);
			String urlSair="http://186.202.184.109/tcc2014/sistema/api/grupo/unjoin";
			new Operacao(this).execute(jsonSair,urlSair);
        	
			
			break;
						
		default:
			break;
		}
		return true;
		
	}
	
	
	public void onResponse(JSONObject response) {
		
		gruposTrajetoModel = new ArrayList<GrupoTrajetoModel>();
		

		 try {
			   
			  JSONObject jsonBuscaGrupos = new JSONObject(response.toString());
		      JSONArray jsonBuscaGrupo = jsonBuscaGrupos.getJSONArray("posts");
		 
		      for (int i = 0; i < jsonBuscaGrupo.length(); i++) {
		    	
		      JSONObject jSubObj = jsonBuscaGrupo.getJSONObject(i);
			  JSONObject jsonGrupoItem = jSubObj.getJSONObject("post");		      
		      String idLider = jsonGrupoItem.getString("lider");
		      String autorizado=jsonGrupoItem.getString("autorizado");
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
		      grupoTrajetoModel.setAutorizado(autorizado);
		      
		      
		    //adiciona à lista
		      gruposTrajetoModel.add(grupoTrajetoModel);
		     	      
		  
		    }
		  } catch (Exception e){
		    e.printStackTrace();
		  }
		 
		  //seta a lista no MainListGrupoTrajetoAdapter
		  setListAdapter(new MainListGrupoTrajetoAdapter(this, gruposTrajetoModel));
		

		
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		
		Toast.makeText(this, "Erro!", 
			    Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		
		GrupoTrajetoModel grupoTrajetoModel=new GrupoTrajetoModel();
		grupoTrajetoModel=gruposTrajetoModel.get(position);
		
		Log.e("Script",grupoTrajetoModel.getNomeGrupoTrajeto());
		Log.e("Script",grupoTrajetoModel.getId().toString());
		
		if(grupoTrajetoModel.getAutorizado().equals("0"))
		{
			Toast.makeText(this, "Grupo de Trajeto bloqueado. Aguardando liberação do líder", 
				    Toast.LENGTH_SHORT).show();
		}else
		{
			Intent it = new Intent(MainListCarregarGrupoTrajetoActivity.this,MainActivity.class);
			it.putExtra("idGrupoTrajeto",grupoTrajetoModel.getId().toString());
			it.putExtra("nomeGrupoTrajeto", grupoTrajetoModel.getNomeGrupoTrajeto());
			it.putExtra("flag", "true");//envia um flag para poder desabilitar o menu editar do MotoristaAutorizacaoActivity
			startActivity(it);
			finish();
			
		}
		
			
					
		
		}
	
	private void setarObjetosRecebidos()
	{
		
		Intent dadosRecebidosParametro=getIntent();
        Object obj=(Object)dadosRecebidosParametro.getSerializableExtra("objeto");
        motoristaModel=new MotoristaModel();
        motoristaModel=(MotoristaModel) obj;
        
        idGrupoTrajeto=dadosRecebidosParametro.getStringExtra("idGrupoTrajeto");
        
        Log.e("Script", motoristaModel.getEmail());
        Log.e("Script",String.valueOf(motoristaModel.getId()));
        Log.e("Script",motoristaModel.getNome());
        Log.e("Script",idGrupoTrajeto);
        
        
	
	}
	private String gerarJSONgetMotoristas(String idMotorista)
	{        
        String json =generationJSON(idMotorista);
        return json;
	}
	private String generationJSON(String idMotorista)
	{
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try
		{
			jo.put("id_motorista", idMotorista);
			jo.put("api_key",chave);
			
    		
		}catch(JSONException e1)
		{
			Log.e("Script","erro Jason");
		}
		return jo.toString();
	}
	private String generationJSON(String idGrupo, String idMotorista)
	{
		JSONObject jo = new JSONObject();
		String chave = getResources().getString(R.string.api_key);
		try
		{
			jo.put("id_grupo_trajeto", idGrupo);
			jo.put("api_key",chave);
			jo.put("id_motorista",idMotorista);
    		
		}catch(JSONException e1)
		{
			Log.e("Script","erro Jason");
		}
		return jo.toString();
	}
	
	private void  instanciaMotoristaLogado()
	{
		
		MotoristaDA bd = new MotoristaDA(getApplicationContext());
		bd.open();
		logado = bd.getMotorista();
		bd.close();
		
	}
}


	




