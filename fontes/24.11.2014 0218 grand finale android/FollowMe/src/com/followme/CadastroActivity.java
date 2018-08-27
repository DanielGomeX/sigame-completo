package com.followme;

import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.followme.BD.MotoristaDA;
import com.followme.library.Encrypt;
import com.followme.model.MotoristaModel;
import com.followme.utilidades.HttpConnection;

public class CadastroActivity extends Activity {

	SQLiteDatabase bancoDeDados = null;
	Cursor cursor;

	private AlertDialog alerta;

	MotoristaModel motorista;
	String dia, mes, ano;

	EditText txtNome, txtEmail, txtConfirmacaoEmail, txtSenha,
			txtConfirmacaoSenha;
	Spinner comboDia, comboMes, comboAno;

	DatePicker Dp;
	Spinner spinnerDia, spinnerMes, spinnerAno;
	String[] vetorDia, vetorMes, vetorAno;
	
	private ProgressBar progress;

	Button btnSalvar, btnProcessa;

	Button btnLogin, btnCadastra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro);

		inicializaComponentes();
		CarregaSpinnerDia();
		CarregaSpinnerMes();
		CarregaSpinnerAno();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.form_voltar:
			finish();
			break;
		case R.id.form_salvar:
			salvar();
			break;
		default:
			break;
		}
		return true;

	}

	public void inicializaComponentes() {
		txtNome = (EditText) findViewById(R.id.txtNome);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtConfirmacaoEmail = (EditText) findViewById(R.id.txtConfirmacaoEmail);
		txtSenha = (EditText) findViewById(R.id.txtSenha);
		txtConfirmacaoSenha = (EditText) findViewById(R.id.txtConfirmacaoSenha);
		comboDia = (Spinner) findViewById(R.id.comboDia);
		comboMes = (Spinner) findViewById(R.id.comboMes);
		comboAno = (Spinner) findViewById(R.id.comboAno);
		spinnerDia = (Spinner) findViewById(R.id.comboDia);
		spinnerMes = (Spinner) findViewById(R.id.comboMes);
		spinnerAno = (Spinner) findViewById(R.id.comboAno);
		vetorDia = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
				"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
				"30", "31" };

		vetorMes = new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09",
				"10", "11", "12" };
		vetorAno = new String[] { "1960", "1961", "1962", "1963", "1964",
				"1965", "1966", "1967", "1968", "1969", "1970", "1971", "1972",
				"1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980",
				"1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988",
				"1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996",
				"1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004",
				"2005", "2006", "2007", "2008", "2009", "2010", "2011",
				"2012", "2013", "2014" };
		
		progress = (ProgressBar) findViewById(R.id.cadastroProgressBar);
		progress.setVisibility(View.INVISIBLE);

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

	private void salvar() {
		motorista = new MotoristaModel();

		if (txtNome.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CadastroActivity.this);
			builder.setTitle("aviso");
			builder.setMessage("Preencher o campo nome");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			alerta = builder.create();
			alerta.show();
			return;
		}

		if (txtEmail.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CadastroActivity.this);
			builder.setTitle("aviso");
			builder.setMessage("Preencher o campo email");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						

						}
					});
			alerta = builder.create();
			alerta.show();
			return;
		}
		if (txtConfirmacaoEmail.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CadastroActivity.this);
			builder.setTitle("aviso");
			builder.setMessage("Preencher o campo confirme seu e-mail");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							

						}
					});
			alerta = builder.create();
			alerta.show();
			return;
		}

		if (!(txtEmail.getText().toString().equals(txtConfirmacaoEmail
				.getText().toString()))) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CadastroActivity.this);
			builder.setTitle("Titulo");
			builder.setMessage("Emails não correspondentes");
			txtConfirmacaoEmail.requestFocus();
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							

						}
					});
			alerta = builder.create();
			alerta.show();
			return;

		}

		if (txtSenha.getText().toString().equals("")) {
			mensagem("aviso",
					"Preencher o campo senha com no mínimo 4 caracteres e no máximo 16 caracteres");
			return;
		}

		if (txtConfirmacaoSenha.getText().toString().equals("")) {
			mensagem(
					"aviso",
					"Preencher o campo confirme sua senha com no mínimo 4 caracteres e no máximo 16 caracteres");
			return;
		}

		if (!(txtSenha.getText().toString().equals(txtConfirmacaoSenha
				.getText().toString()))) {
			mensagem("aviso", "Senhas não correspondentes");
			return;
		}
		
		String senha=txtSenha.getText().toString();
		
		Integer quantidadeCaracteres=quantidadeCaracteres(senha);
		
		if(quantidadeCaracteres<4)
		{
			mensagem("aviso",
					"Senha muito curta. A senha deve possuir no mínimo 4 caracteres");
			return;
		}
		
		

		dia = comboDia.getSelectedItem().toString();
		mes = comboMes.getSelectedItem().toString();
		ano = comboAno.getSelectedItem().toString();
		String dataNasc = dia + "/" + mes + "/" + ano;
		
		//seta os atributos do motorista
		motorista.setNome(txtNome.getText().toString());
		motorista.setEmail(txtEmail.getText().toString());
		motorista.setSenha(Encrypt.sha1Hash(txtSenha.getText().toString()));
		motorista.setNascimento(dataNasc);
		motorista.setLogado(1);


		//gera o json
		String json = geraJSON(motorista.getNome(), motorista.getNascimento(), motorista.getEmail(), motorista.getSenha());

		progress.setVisibility(View.VISIBLE);
		
		//thread para executar o cadastro
		new ReadJsonAsyncTask().execute(json);
		
	}

	

	public void mensagem(String titulo, String texto) {
		AlertDialog.Builder caixaAlerta = new AlertDialog.Builder(
				CadastroActivity.this).setMessage(texto).setTitle(titulo)
				.setNeutralButton("ok", null);

		caixaAlerta.show();
	}
	
	private Integer quantidadeCaracteres(String senha)
	{
		return senha.length();
	}
	
	private String geraJSON(String nome, String nascimento, String email, String senha)
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("nome", nome);
    		jo.put("nascimento",nascimento);
    		jo.put("email", email);
    		jo.put("senha",senha);
    		jo.put("api_key",chave);
        		
    	}catch(JSONException e1)
    	{
    		Log.e("Script","erro Json");
    	}
    	return jo.toString();
    }

	private class ReadJsonAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			return HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/motorista/post", "send-json", params[0]);
		}

		protected void onPostExecute(String result) {
			Log.e("json", result);
			MotoristaDA bd = new MotoristaDA(getApplicationContext());

			try {
				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("posts");
				JSONObject jSubObj = jArray.getJSONObject(0);
				JSONObject post = jSubObj.getJSONObject("post");

				try{
					motorista.setId(post.getInt("id"));

					bd.open();
					bd.gravaMotorista(motorista);
					bd.close();
				}
				catch(JSONException e){
					if(post.getString("erro") != null){
						switch (post.getInt("codigo")) {
						case 1:
							throw new Exception("Erro interno da API");
						case 2:
							throw new Exception("Nome inválido");
						case 3:
							throw new Exception("Formato de e-mail inválido");
						case 4:
							throw new Exception("Data de nascimento inválida");
						case 5:
							throw new Exception("E-mail já cadastrado");
						case 6:
							throw new Exception("Senha atual inválida");
			
						}
					}
				}

				Intent it = new Intent(getBaseContext(), MainActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(it);//aqui chama o mainActivity
				finish();
			} catch (JSONException e1) {
				progress.setVisibility(View.INVISIBLE);
				
				e1.printStackTrace();

				String erro = getResources().getString(R.string.erro_conexao);

				Toast.makeText(getBaseContext(), erro, Toast.LENGTH_SHORT)
						.show();
			} catch (Exception e2) {
			
				progress.setVisibility(View.INVISIBLE);
				
				e2.printStackTrace();
				Toast.makeText(getBaseContext(), e2.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
