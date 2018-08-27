package com.followme.grupoTrajetoActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.R;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.R.menu;
import com.followme.R.string;
import com.followme.grupoTrajetoActivity.GrupoTrajetoListBuscaActivity.Operacao;
import com.followme.model.GrupoTrajetoModel;
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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class GrupoTrajetoSelecionadoActivity extends Activity{
	
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
	private GrupoTrajetoModel grupoTrajetoModel;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grupo_trajeto_selecionado_activity);
        
        Intent dadosRecebidosParametro=getIntent();
        Object obj=(Object)dadosRecebidosParametro.getSerializableExtra("objeto");
        grupoTrajetoModel=new GrupoTrajetoModel();
        grupoTrajetoModel=(GrupoTrajetoModel) obj;
        
        
       
        
        
        inicializaComponentes();
        CarregaSpinnerDia();
        CarregaSpinnerMes();
        CarregaSpinnerAno();
        setarDataSpinner();
        
        //temporario
        txtNomeGrupo.setText(grupoTrajetoModel.getNomeGrupoTrajeto().toString());
        txtLocalEncontro.setText(grupoTrajetoModel.getLocalEncontro().toString());
        txtLocalDestino.setText(grupoTrajetoModel.getLocalDestino().toString());
        
        
        //define a data nos spinners
        String temp=grupoTrajetoModel.getDataSaidaAndroid();
    	StringTokenizer st = new StringTokenizer(temp, "/"); 
    	Integer dia=Integer.parseInt(st.nextToken());
    	Integer mes=Integer.parseInt(st.nextToken());
    	Integer ano=Integer.parseInt(st.nextToken());
    	setarDataSpinner(dia, mes, ano);
    	
        temp=grupoTrajetoModel.getHoraSaida().toString();
        st = new StringTokenizer(temp, ":"); 
        Integer hora=Integer.parseInt(st.nextToken());
    	Integer minuto=Integer.parseInt(st.nextToken());
    	
    	timePickerHoraSaida.setCurrentHour(hora);
    	timePickerHoraSaida.setCurrentMinute(minuto);
    	
    	
    	
    	
    	
      
        
        
        //fim temporario
        
      
        
        
        
        
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.grupo_trajeto_selecionado, menu);
	    return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.grupo_trajeto_selecionado_voltar:
		
			finish();
			
			break;
		case R.id.grupo_trajeto_selecionado_salvar:
			
			
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
			
			GrupoTrajetoModel grupoAux=new GrupoTrajetoModel();
			grupoAux.setIdLider(grupoTrajetoModel.getIdLider());
			grupoAux.setId(grupoTrajetoModel.getId());
			grupoAux.setNomeGrupoTrajeto(txtNomeGrupo.getText().toString());
			grupoAux.setLocalEncontro(txtLocalEncontro.getText().toString());
			grupoAux.setLocalDestino(txtLocalDestino.getText().toString());
			grupoAux.setDataSaidaAndroid(dataSaidaAndroid);
			grupoAux.setDataSaidaMysql(dataSaidaMysql);
			grupoAux.setHoraSaida(horaSaida);
			
			//gera a string JSON
			String json=generationJSON(grupoAux);
			
			new Operacao(this).execute(json);  
			//callServer("send-json", json);
			
			//se gravar no servidor, então grava no android
			//if(answer=="ok")
			{
				BD bd = new BD(GrupoTrajetoSelecionadoActivity.this);
				bd.inserir(grupoTrajetoModel);
			}
			
			
			//
			
			
			
			break;
						
		default:
			break;
		}
		return true;
		
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
    	 timePickerHoraSaida=(TimePicker)findViewById(R.id.timePickerActGrupoTrajetoSelHoraSaida);
         timePickerHoraSaida.setIs24HourView(false);
         
         
         
         builder = new AlertDialog.Builder(GrupoTrajetoSelecionadoActivity.this);
         txtNomeGrupo=(EditText)findViewById(R.id.txtActGrupoTrajetoSelNomeGrupo);
         txtLocalEncontro=(EditText)findViewById(R.id.txtActGrupoTrajetoSelLocalEncontro);
         txtLocalDestino=(EditText)findViewById(R.id.txtActGrupoTrajetoSelGrupoLocalDestino);
    	 spinnerDia=(Spinner)findViewById(R.id.cboActGrupoTrajetoSelDia);
         spinnerMes=(Spinner)findViewById(R.id.cboActGrupoTrajetoSelMes);
         spinnerAno=(Spinner)findViewById(R.id.cboActGrupoTrajetoSelAno);
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
    
    private void setarDataSpinner(int dia, Integer mes, Integer ano)
    {
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
    
    private void setarDataSpinner()
    {
    	Calendar c=Calendar.getInstance();
        int dia=c.get(Calendar.DAY_OF_MONTH);
        int mes=c.get(Calendar.MONTH)+1;
        int ano=c.get(Calendar.YEAR);
        
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
    
  //transforma o objeto grupoTrajetoModel no formato Json
    private String generationJSON(GrupoTrajetoModel grupoTrajetoModel)
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		
    		jo.put("id_grupo_trajeto", grupoTrajetoModel.getId());
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
    
    
public class Operacao extends AsyncTask<String, Void, String> {
		
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
		protected String doInBackground(String... params) {
			
			
				
				String resposta = HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/grupo/put","send-json", params[0]);	
				return resposta;
		}

		@Override
		protected void onPostExecute(String result)
		{
			
			
			String resposta=result;
			if(resposta.contains("true"))
			{
				Toast.makeText(
						getBaseContext(),
						"Grupo de Trajeto editado com sucesso",
						Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(
						getBaseContext(),
						"Erro ao editar Grupo de Trajeto",
						Toast.LENGTH_SHORT).show();
			}
			
			progressDialog.dismiss();
			
		        
		}
		
	
	    
   }
    
    
    
     
  
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

    



}
