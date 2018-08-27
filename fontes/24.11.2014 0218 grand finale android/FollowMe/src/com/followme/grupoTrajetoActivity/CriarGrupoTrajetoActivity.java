package com.followme.grupoTrajetoActivity;

/**
 * @Activity para criar um grupo de trajeto
 * 
 * MainActivity>CriarGrupoTrajetoActivity
 * 
 * 
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.followme.R;
import com.followme.BD.MotoristaDA;
import com.followme.MainListCarregarGrupoTrajetoActivity.Operacao;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.R.string;
import com.followme.adapter.GrupoTrajetoAdapter;
import com.followme.model.GrupoTrajetoModel;
import com.followme.model.MotoristaModel;
import com.followme.motoristaActivity.MotoristaAutorizacaoActivity;
import com.followme.utilidades.BD;
import com.followme.utilidades.HttpConnection;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class CriarGrupoTrajetoActivity extends Activity{
	
	private Spinner spinnerDia, spinnerMes, spinnerAno;
	private String[] vetorDia,vetorMes,vetorAno;
	private Button btnVoltar, btnSalvar;
	private EditText txtNomeGrupo,txtLocalEncontro,txtLocalDestino;
	private AlertDialog alerta;
	private AlertDialog.Builder builder = null;
	private String mensagem=null;
	private String dataSaidaAndroid, dataSaidaMysql;
	private String horaSaida;
	private String dia, mes,ano;
	private TimePicker timePickerHoraSaida=null;
	private String answer;
	
	
	private MotoristaModel logado;
	private Integer idMotorista;
	private List<GrupoTrajetoModel> list;
	private List<GrupoTrajetoModel> gruposTajetoModel;
	private GrupoTrajetoModel grupoTrajetoModel;
	private JSONObject jObj;
	private Integer idGrupoTrajetoGravado;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.criar_grupo_trajeto_activity);
        
       
        
        
        inicializaComponentes();
        CarregaSpinnerDia();
        CarregaSpinnerMes();
        CarregaSpinnerAno();
        
        setarDataSpinner();
        
        //temporario
        txtNomeGrupo.setText("Sao paulo");
        txtLocalEncontro.setText("Igreja Matriz");
        txtLocalDestino.setText("Poços de Caldas");
        
        //fim temporario
        
      
        
        
        
        
        
    }
	
	public void CarregaSpinnerDia() {
        try {
            ArrayAdapter adaptador = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, vetorDia);
            adaptador
                    .setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerDia.setAdapter(adaptador);

        } catch (Exception e) {
            Log.e("Erro", "Erro Spinner Dia");
        }
    }
    public void CarregaSpinnerMes() {
        try {
            ArrayAdapter adaptador = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, vetorMes);
            adaptador
                    .setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerMes.setAdapter(adaptador);

        } catch (Exception e) {
            Log.e("Erro", "Erro Spinner Mes");
        }
    }
    public void CarregaSpinnerAno() {
        try {
            ArrayAdapter adaptador = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, vetorAno);
            adaptador
                    .setDropDownViewResource(android.R.layout.simple_spinner_item);
            spinnerAno.setAdapter(adaptador);
            

        } catch (Exception e) {
            Log.e("Erro", "Erro Spinner Ano");
        }
    }
    

    public void inicializaComponentes()
    {
    	 timePickerHoraSaida=(TimePicker)findViewById(R.id.timePickerActGrupoTrajetoHoraSaida);
         timePickerHoraSaida.setIs24HourView(false);
         
         
         
         builder = new AlertDialog.Builder(CriarGrupoTrajetoActivity.this);
         txtNomeGrupo=(EditText)findViewById(R.id.txtActGrupoTrajetoNomeGrupo);
         txtLocalEncontro=(EditText)findViewById(R.id.txtActGrupoTrajetoLocalEncontro);
         txtLocalDestino=(EditText)findViewById(R.id.txtActGrupoTrajetoGrupoLocalDestino);
    	 spinnerDia=(Spinner)findViewById(R.id.cboActGrupoTrajetoDia);
         spinnerMes=(Spinner)findViewById(R.id.cboActGrupoTrajetoMes);
         spinnerAno=(Spinner)findViewById(R.id.cboActGrupoTrajetoAno);
         vetorDia=new String[]{"01","02","03","04","05","06","07","08","09","10",
         						"11","12","13","14","15","16","17","18","19","20",
         						"21","22","23","24","25","26","27","28","29","30","31"};
         
         vetorMes=new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};
         
         vetorAno=new String[4];//range de 4 anos 
         Calendar c=Calendar.getInstance();
         int ano=c.get(Calendar.YEAR);
         for(int i=0;i<4;i++)
         {
        	 vetorAno[i]=String.valueOf(ano);
        	 ano++;
         }
         
         
             
    }
    
    public void mensagem(String titulo, String mensagem)
    {
    	
		builder.setTitle(titulo);
		builder.setMessage(mensagem);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		
		alerta = builder.create();
		alerta.show();
    }
    
    private String retornaDataSaidaAndroid()
    {
        dia=spinnerDia.getSelectedItem().toString();
        mes=spinnerMes.getSelectedItem().toString();
        ano=spinnerAno.getSelectedItem().toString();
		String dataSaidaAndroid=dia+"/"+mes+"/"+ano;//formato android
		return dataSaidaAndroid;
    }
    
    private String retornaDataSaidaMysql()
    {
    	dia=spinnerDia.getSelectedItem().toString();
    	mes=spinnerMes.getSelectedItem().toString();
    	ano=spinnerAno.getSelectedItem().toString();
		String dataSaidaMysql=ano+"-"+mes+"-"+dia;//formato mysql
		return dataSaidaMysql;
    }
    
    
    
    private String retornaHoraSaida()
    {
    	String hora=timePickerHoraSaida.getCurrentHour().toString();
    	String minuto=timePickerHoraSaida.getCurrentMinute().toString();
    	
    	if(hora.length()==1)
    	{
    		hora="0"+hora;
    	}
    	if(minuto.length()==1)
    	{
    		minuto="0"+minuto;
    	}
    	
    	
    	
    	String horaSaida=hora+":"+minuto;
    	return horaSaida;
    	
    	
    }
    
    private void setarDataSpinner()
    {
    	Calendar c=Calendar.getInstance();
        String dia=String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String mes=String.valueOf(c.get(Calendar.MONTH)+1);
        String ano=String.valueOf(c.get(Calendar.YEAR));
        
        if(dia.length()==1)
        {
        	dia="0"+dia;
        
        }
        
        if(mes.length()==1)
        {
        	mes="0"+mes;//
        
        }
        
        
        
        
             
        
        for (int i = 0; i < spinnerDia.getCount(); i++) {  
            if (spinnerDia.getItemAtPosition(i).toString().equals(String.valueOf(dia))) {  
                spinnerDia.setSelection(i);  
            }  
        } 
        for (int i = 0; i < spinnerMes.getCount(); i++) {  
            if (spinnerMes.getItemAtPosition(i).toString().equals(String.valueOf(mes))) {  
                spinnerMes.setSelection(i);  
            }  
        } 
        for (int i = 0; i < spinnerAno.getCount(); i++) {  
            if (spinnerAno.getItemAtPosition(i).toString().equals(String.valueOf(ano))) {  
                spinnerAno.setSelection(i);  
            }  
        }
   
    }
    
    private String generationJSON(GrupoTrajetoModel grupoTrajetoModel)//transforma o objeto grupoTrajetoModel no formato Json
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("lider", grupoTrajetoModel.getIdLider());
    		jo.put("nome_grupo_trajeto",grupoTrajetoModel.getNomeGrupoTrajeto());
    		jo.put("local_encontro", grupoTrajetoModel.getLocalEncontro());
    		jo.put("local_destino", grupoTrajetoModel.getLocalDestino());
    		jo.put("data_saida",grupoTrajetoModel.getDataSaidaMysql());
    		jo.put("hora_saida", grupoTrajetoModel.getHoraSaida());
    		jo.put("api_key",chave);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Jason");
    	}
    	return jo.toString();
    }
    
    private GrupoTrajetoModel degenerationJSON(String data)//recebe a string no formato Json
    {
    	GrupoTrajetoModel grupoTrajetoModel=new GrupoTrajetoModel();
    	
    	
    	try
    	{
    		JSONObject jo = new JSONObject(data);
        	JSONArray ja=new JSONArray();
        	
        	grupoTrajetoModel.setNomeGrupoTrajeto(jo.getString("nome_grupo_trajeto"));
        	grupoTrajetoModel.setLocalEncontro(jo.getString("local_encontro"));
        	grupoTrajetoModel.setLocalDestino(jo.getString("local_destino"));
        	grupoTrajetoModel.setDataSaidaMysql(jo.getString("data_saida"));
        	grupoTrajetoModel.setHoraSaida(jo.getString("hora_saida"));
        	
        	
        	
    	
    		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro receber Jason");
    	}
    	return grupoTrajetoModel;
    }
    //inicio thread
    
public class Operacao extends AsyncTask<String, Void, String> {
		
		private ProgressDialog progressDialog;
		private Context ct;
		private String resposta;
		private GrupoTrajetoModel grupoTrajetoModel;
		private String tipoRetorno;
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
		protected String doInBackground(String... params) {
			
				//String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/get-by-participante","send-json", params[0]);
			
			if(params[1].toString().equals("salvar"))
			{
				resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/post","send-json",params[0]);
				try
				{
					JSONObject jsonBuscaGrupos = new JSONObject(resposta);
	    		    JSONArray jsonBuscaGrupo = jsonBuscaGrupos.getJSONArray("posts");
	    		    JSONObject jSubObj = jsonBuscaGrupo.getJSONObject(0);
	   			    JSONObject jsonGrupoItem = jSubObj.getJSONObject("post");	
	   			    idGrupoTrajetoGravado=jsonGrupoItem.getInt("id");
	   			    
	   			//traz o motorista (logado) referente ao que esta persistido no SQLite
			        instanciaMotoristaLogado();
			  
			        String email=logado.getEmail();
			        
			        idMotorista=logado.getId();
			        
			        //gera a string JSON
			       
					String json=generationJSON(email);
					
					resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/get","send-json", json);
					
				}catch(Exception erro)
				{
					
				}
				
				
				
				
				
                 try{
					
					jObj = new JSONObject(resposta);
					
					
							
				}catch(Exception erro)
				{
					Log.e("Script", erro.toString());
				}
				
				
				
				
                 
				Log.e("Script", resposta);
				tipoRetorno="salvar";
				
						
				
				
			}
			if(params[1].toString().equals("abrirTela"))
			{
				String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/get","send-json", params[0]);
				
				try {
					JSONObject jObj = new JSONObject(resposta);
					JSONArray jArray = jObj.getJSONArray("posts");
					
					list = new ArrayList<GrupoTrajetoModel>();//cria uma lista de grupo de trajeto
					int qtdRegistros=jArray.length();
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
				tipoRetorno="abrirTela";
			}

			return tipoRetorno;
		}

		@Override
		protected void onPostExecute(String result)
		{
			if(result.equals("salvar"))
			{
				progressDialog.dismiss();
				if(resposta.equals("3"))
				{
					Toast.makeText(getBaseContext(), "Erro com a conexão de dados. Verifique sua rede",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					
					Toast.makeText(getBaseContext(), "Grupo criado com sucesso",
							Toast.LENGTH_SHORT).show();
					
					onResponse(jObj,"salvar");
					
					 //traz o motorista (logado) referente ao que esta persistido no SQLite
			        //instanciaMotoristaLogado();
			  
			       // String email=logado.getEmail();
			        
			        //idMotorista=logado.getId();
			        
			      //gera a string JSON
			       
					//String json=generationJSON(email);
					
					 
					//new Operacao(CriarGrupoTrajetoActivity.this).execute(json,"abrirTela"); 
					/*
					Intent it = new Intent(CriarGrupoTrajetoActivity.this,MotoristaAutorizacaoActivity.class);
					it.putExtra("objeto",grupoTrajetoModel);
					it.putExtra("flag", "editar");
					startActivity(it);
					finish();
					*/
				}
				
				
				
		
			}
			if(result.equals("abrirTela"))
			{
				onResponse(jObj,"salvar");
			}
			
				        
		}
		
	
	    
   }
	    
    
    
    
    
    
    
    //fim thread
    
     
   
     public String horaAtual() {  
        try {  
   
            String formato = "yyyy";  
            Date agora = new java.util.Date();  
            SimpleDateFormat formata = new SimpleDateFormat(formato);  
            String hora = formata.format(agora);  
   
            return hora;  
        } catch (Exception e) {  
            return ("Não foi possível capturar a hora" + e);  
        }  
    }  
     
     private void  instanciaMotoristaLogado()
 	{
 		MotoristaDA bd = new MotoristaDA(getApplicationContext());
 		bd.open();
 		logado = bd.getMotorista();
 		bd.close();
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
     
     public void onResponse(JSONObject response,String retorno) {
 		
    	if(retorno.equals("salvar")) 
    	{
    		
    		try {
				JSONObject jObj = new JSONObject(response.toString());
				JSONArray jArray = jObj.getJSONArray("posts");
				
				list = new ArrayList<GrupoTrajetoModel>();//cria uma lista de grupo de trajeto
				int qtdRegistros=jArray.length();
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
				
				GrupoTrajetoModel aux;
				for(int i=0;i<list.size();i++)
				{
					aux=list.get(i);
					Integer idCorrente=aux.getId();
					if(idCorrente.equals(idGrupoTrajetoGravado))
					{
						aux=new GrupoTrajetoModel();
						GrupoTrajetoModel grupoTrajetoModel=new GrupoTrajetoModel();
						grupoTrajetoModel=list.get(i);
						Intent it = new Intent(CriarGrupoTrajetoActivity.this,MotoristaAutorizacaoActivity.class);
						it.putExtra("objeto",grupoTrajetoModel);
						it.putExtra("flag", "editar");
						startActivity(it);
						finish();
						
						
					}
				}
				
				
				
				
			} catch (JSONException e) {
			
				e.printStackTrace();
			}
    		
    				 
				//new Operacao(CriarGrupoTrajetoActivity.this).execute(json,"abrirTela"); 
				/*
				Intent it = new Intent(CriarGrupoTrajetoActivity.this,MotoristaAutorizacaoActivity.class);
				it.putExtra("objeto",grupoTrajetoModel);
				it.putExtra("flag", "editar");
				startActivity(it);
				finish();
				*/
			
   			    
   			
    		
    		
    	}
    	if(retorno.equals("abrirTela"))
    	{
    		
    	}
    	 
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
 		 
 		  
 		 
 		

 		
 	}
     
     //
     @Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 	    getMenuInflater().inflate(R.menu.menu_grupo_trajeto_cadastrar, menu);
 	    return true;
 	}
 	public boolean onOptionsItemSelected(MenuItem item) {
 		switch (item.getItemId()) {
 		
 		case R.id.grupo_trajeto_cadastrar_voltar:
 		
 			finish();
 			
 			break;
 		case R.id.grupo_trajeto_cadastrar_salvar:
 			
 			
 			//
 			if(txtNomeGrupo.getText().toString().equals(""))
			{
				mensagem="Preencher o campo " + "\"Nome do Grupo de Trajeto\"";
				mensagem("aviso", mensagem );
				txtNomeGrupo.requestFocus();
				break;
			}
			
			if(txtLocalEncontro.getText().toString().equals(""))
			{
				mensagem="Preencher o campo " + "\"Local de Encontro\"";
				mensagem("aviso", mensagem );
				txtLocalEncontro.requestFocus();
				break;
			}
			if(txtLocalDestino.getText().toString().equals(""))
			{
				mensagem="Preencher o campo " + "\"Local de Destino\"";
				mensagem("aviso", mensagem );
				txtLocalDestino.requestFocus();
				break;
			}
			
			
			
			horaSaida=retornaHoraSaida(); 
			dataSaidaAndroid=retornaDataSaidaAndroid();
			dataSaidaMysql=retornaDataSaidaMysql();
			
			MotoristaDA bd = new MotoristaDA(getApplicationContext());
			bd.open();
			MotoristaModel logado = bd.getMotorista();
			bd.close();
			
			GrupoTrajetoModel grupoTrajetoModel=new GrupoTrajetoModel();
			grupoTrajetoModel.setIdLider(logado.getId());//esse valor tem que vim do banco de dados
			grupoTrajetoModel.setNomeGrupoTrajeto(txtNomeGrupo.getText().toString());
			grupoTrajetoModel.setLocalEncontro(txtLocalEncontro.getText().toString());
			grupoTrajetoModel.setLocalDestino(txtLocalDestino.getText().toString());
			grupoTrajetoModel.setDataSaidaAndroid(dataSaidaAndroid);
			grupoTrajetoModel.setDataSaidaMysql(dataSaidaMysql);
			grupoTrajetoModel.setHoraSaida(horaSaida);
			
			//gera a string JSON
			String json=generationJSON(grupoTrajetoModel);
			
			 new Operacao(CriarGrupoTrajetoActivity.this).execute(json,"salvar");  
			
			//callServer("send-json", json);
			
			//se gravar no servidor, então grava no android
			//if(answer=="ok")
	
 			
 			
 			//
 			
 			
 			break;
 						
 		default:
 			break;
 		}
 		return true;
 		
 	}

     
     
     
     //


    



}
