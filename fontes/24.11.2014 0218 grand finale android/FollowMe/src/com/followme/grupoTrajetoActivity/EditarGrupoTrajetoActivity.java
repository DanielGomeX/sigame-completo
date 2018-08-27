package com.followme.grupoTrajetoActivity;

/**
 * @Activity para editar um grupo de trajeto. Irá aparecer uma lista de todos os 
 * grupos de trajetos criados pelo motorista logado.
 * 
 * Quando é clicado um grupo de trajeto, aparece uma lista 
 * de motoristas bloqueados e desbloqueados, onde se clicado, libera ou bloqueia 
 * um determinado motorista
 * 
 * 
 * 
 * 
 * MainActivity>EditarGrupoTrajetoActivity>GrupoTrajetoListMotoristaActivity
 * 
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.followme.R;
import com.followme.BD.MotoristaDA;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.R.menu;
import com.followme.R.string;
import com.followme.model.GrupoTrajetoModel;
import com.followme.model.MotoristaModel;
import com.followme.motoristaActivity.MotoristaAutorizacaoActivity;
import com.followme.utilidades.HttpConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EditarGrupoTrajetoActivity extends Activity{
	
	
	private ListView lv;
	private GrupoTrajetoModel grupoTrajetoModel;
	private List<GrupoTrajetoModel> list;
	private int qtdRegistros;
	private Integer idMotorista;
	private MotoristaModel logado;
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_grupo_trajeto_activity);
        
        //traz o motorista (logado) referente ao que esta persistido no SQLite
        instanciaMotoristaLogado();
  
        String email=logado.getEmail();
        
        idMotorista=logado.getId();
        
      //gera a string JSON
		String json=generationJSON(email);
		
		//executa a thread para buscar todos os grupos de trajeto que o motorista (logado) criou
        new Operacao(this).execute(json);
              
        
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.lista_grupo_trajeto, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.lista_grupo_trajeto_voltar:
		
			finish();
			
			break;
						
		default:
			break;
		}
		return true;
		
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
			
				
			String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/get","send-json", params[0]);
			
				try {
					JSONObject jObj = new JSONObject(resposta);
					JSONArray jArray = jObj.getJSONArray("posts");
					
					list = new ArrayList<GrupoTrajetoModel>();//cria uma lista de grupo de trajeto
					qtdRegistros=jArray.length();
					for(int i=0;i<qtdRegistros;i++)
					{
						JSONObject jSubObj = jArray.getJSONObject(i);
						JSONObject post = jSubObj.getJSONObject("post");
					
						
						grupoTrajetoModel=new GrupoTrajetoModel();
						grupoTrajetoModel.setId(post.getInt("id_grupo_trajeto"));
						grupoTrajetoModel.setNomeGrupoTrajeto(post.getString("nome_grupo_trajeto"));
			        	grupoTrajetoModel.setLocalEncontro(post.getString("local_encontro"));
			        	grupoTrajetoModel.setLocalDestino(post.getString("local_destino"));
			        	grupoTrajetoModel.setDataSaidaMysql(post.getString("data_saida"));
			       
			        	String temp=grupoTrajetoModel.getDataSaidaMysql();
			        	
			        	StringTokenizer st = new StringTokenizer(temp, "-"); 
			        	
			            String ano=st.nextToken();
			        	String mes=st.nextToken();
			        	String dia=st.nextToken();
			        	temp=dia+"/"+mes+"/"+ano;
			        	grupoTrajetoModel.setDataSaidaAndroid(temp);
			        	grupoTrajetoModel.setHoraSaida(post.getString("hora_saida"));
						
						list.add(grupoTrajetoModel);
						
						
					}
									
					
					
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
				
      			return null;
			
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			 String[] atividades=new String[qtdRegistros];
			 
			 lv = (ListView)findViewById(R.id.lstActEdtGrupoTrajeto);
			 try{
				 for(int i=0;i<list.size();i++)
			        {
			        	grupoTrajetoModel=list.get(i);
			        	atividades[i]=grupoTrajetoModel.getNomeGrupoTrajeto()+" "+grupoTrajetoModel.getDataSaidaAndroid();
			        }
			        
			        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditarGrupoTrajetoActivity.this, android.R.layout.simple_list_item_1,atividades);
			        lv.setAdapter(adapter);
			        lv.setOnItemClickListener(chamaActivity());
			        progressDialog.dismiss();
				 
			 }catch(Exception erro)
			 {
				 Toast.makeText(EditarGrupoTrajetoActivity.this, "Erro!", 
						    Toast.LENGTH_SHORT).show();
				 progressDialog.dismiss();
			 }
		       
			
		}	

	}
	public OnItemClickListener chamaActivity(){
		return (new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
				//seta os dados do grupo de trajeto para passar por parâmetro para a activity MotoristaAutorizacaoActivity
				GrupoTrajetoModel grupoTrajeto=new GrupoTrajetoModel();				
				grupoTrajeto.setId(list.get(position).getId());
				grupoTrajeto.setNomeGrupoTrajeto(list.get(position).getNomeGrupoTrajeto());
				grupoTrajeto.setLocalEncontro(list.get(position).getLocalEncontro());
				grupoTrajeto.setLocalDestino(list.get(position).getLocalDestino());
				grupoTrajeto.setDataSaidaAndroid(list.get(position).getDataSaidaAndroid());
				grupoTrajeto.setDataSaidaMysql(list.get(position).getDataSaidaMysql());
				grupoTrajeto.setHoraSaida(list.get(position).getHoraSaida());
				grupoTrajeto.setIdLider(idMotorista);
				
				Intent it = new Intent(EditarGrupoTrajetoActivity.this,MotoristaAutorizacaoActivity.class);
				it.putExtra("objeto",grupoTrajeto);
				it.putExtra("flag", "editar");
				startActivity(it);
				finish();
				
			}
		});
	}
	
		
	private String generationJSON(String email)//transforma o objeto grupoTrajetoModel no formato Json
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("email_lider", email);
      		jo.put("api_key",chave);
        		
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
