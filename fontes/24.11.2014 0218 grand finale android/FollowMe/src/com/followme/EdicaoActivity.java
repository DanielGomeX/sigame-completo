package com.followme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import com.followme.BD.MotoristaDA;
import com.followme.library.Encrypt;
import com.followme.model.MotoristaModel;
import com.followme.utilidades.HttpConnection;

public class EdicaoActivity extends Activity {

	SQLiteDatabase bancoDeDados = null;
	Cursor cursor;

	private AlertDialog alerta;
	
	private ProgressBar progress;
	
	EditText txtNome, txtEmail, txtConfirmacaoEmail, txtSenha,
			txtConfirmacaoSenha, txtSenhaAtual;

	Button btnSalvar, btnVoltar;
	
	private MotoristaDA bd;
	private MotoristaModel motorista;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edicao);

		bd = new MotoristaDA(getApplicationContext());
				
		inicializaComponentes();



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
		bd.open();
		motorista = bd.getMotorista();
		
		txtNome = (EditText) findViewById(R.id.txtNome);
		txtNome.setText(motorista.getNome());
		
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtEmail.setText(motorista.getEmail());
		
		txtConfirmacaoEmail = (EditText) findViewById(R.id.txtConfirmacaoEmail);
		txtConfirmacaoEmail.setText(motorista.getEmail());
		
		txtSenha = (EditText) findViewById(R.id.txtSenha);
		txtConfirmacaoSenha = (EditText) findViewById(R.id.txtConfirmacaoSenha);
		txtSenhaAtual = (EditText) findViewById(R.id.txtSenhaAtual);
		
		progress = (ProgressBar) findViewById(R.id.edicaoProgressBar);
		progress.setVisibility(View.INVISIBLE);


	}

	public void mostraMotoristas() {
		txtNome.setText(cursor.getString(cursor.getColumnIndex("nome")));

	}

	private void salvar() {

		if (txtNome.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					EdicaoActivity.this);
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
					EdicaoActivity.this);
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
					EdicaoActivity.this);
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
					EdicaoActivity.this);
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

		motorista.setSenha("null");
		if (!txtSenha.getText().toString().equals("")) {
			if (!(txtSenha.getText().toString().equals(txtConfirmacaoSenha
					.getText().toString()))) {
				mensagem("aviso", "Senhas não correspondentes.");
				return;
			}
			motorista.setSenha(Encrypt.sha1Hash(txtSenha.getText().toString()));
		}
		
        String senha=txtSenha.getText().toString();
        String confirmacaoSenha=txtConfirmacaoSenha.getText().toString();
		
		Integer quantidadeCaracteresSenha=quantidadeCaracteres(senha);
		Integer quantidadeCaracteresConfirmarSenha=quantidadeCaracteres(confirmacaoSenha);
		
		if((quantidadeCaracteresConfirmarSenha==0)&&(quantidadeCaracteresSenha==0))//não passou senha nova
		{
			
		}
		else
		{
			if(quantidadeCaracteresSenha<4)
			{
				mensagem("aviso",
						"Senha muito curta. A senha deve possuir no mínimo 4 caracteres");
				return;
			}
			if(quantidadeCaracteresConfirmarSenha<4)
			{
				mensagem("aviso",
						"Senha muito curta. A senha deve possuir no mínimo 4 caracteres");
				return;
			}
			
		}
		
		

		if (txtSenhaAtual.getText().toString().equals("")) {
			mensagem(
					"aviso",
					"Preencher o campo ENTRE COM SUA SENHA ATUAL PARA SALVAR com no mínimo 4 caracteres e no máximo 16 caracteres");
			return;
		}
		
		// atribui os dados nos atributos do objeto motorista
		motorista.setNome(txtNome.getText().toString());
		motorista.setEmail(txtEmail.getText().toString());
		
		
		// gera o json
		String json = geraJSON(motorista.getId(), motorista.getNome(), motorista.getNascimento(), motorista.getEmail(), motorista.getSenha(), Encrypt.sha1Hash(txtSenhaAtual.getText().toString()));

		progress.setVisibility(View.VISIBLE);
		
		//executa a thread
		new ReadJsonAsyncTask().execute(json);
		

	}

	
	public void mensagem(String titulo, String texto) {
		AlertDialog.Builder caixaAlerta = new AlertDialog.Builder(
				EdicaoActivity.this).setMessage(texto).setTitle(titulo)
				.setNeutralButton("ok", null);

		caixaAlerta.show();
	}
	private Integer quantidadeCaracteres(String senha)
	{
		return senha.length();
	}
	
	private String geraJSON(int id, String nome, String nascimento, String email, String senha, String senhaAtual)
    {
    	JSONObject jo = new JSONObject();
    	String chave = getResources().getString(R.string.api_key);
    	try
    	{
    		jo.put("id", id);
    		jo.put("nome", nome);
    		jo.put("nascimento",nascimento);
    		jo.put("email", email);
    		jo.put("senha",senha);
    		jo.put("senhaAtual", senhaAtual);
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
		

			return HttpConnection.getSetDataWeb("http://186.202.184.109/tcc2014/sistema/api/motorista/put", "send-json", params[0]);
		}

		protected void onPostExecute(String result) {
			MotoristaDA bd = new MotoristaDA(getApplicationContext());

			try {
				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("posts");
				JSONObject jSubObj = jArray.getJSONObject(0);
				JSONObject post = jSubObj.getJSONObject("post");

				try{
					post.getString("sucesso");

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
