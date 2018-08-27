<?php

class LoginController extends Zend_Controller_Action
{

    public function init()
    {
    	// atribui idioma
    	require_once ('Sigame/Language/pt-br/Login.php');
    	
        //atribui model
		$this->model = new Application_Model_Motorista();
		
		//atribui session
		$this->motorista = new Sigame_Session_Motorista();
		
		//communs texts
		$this->view->assign ( 'tituloLogin', $this->tituloLogin );
		$this->view->assign ( 'textoSubtitulo', $this->textoSubtitulo );
		$this->view->assign ( 'textoTitulo', $this->textoTitulo );
    }

    private function validarLogin()
    {
    	// recupera post email
    	$this->view->assign ( 'postLoginEmail', $_POST ['loginEmail'] );
    	
    	//login
    	$resposta = $this->motorista->login($_POST ['loginEmail'], $_POST ['loginSenha']);//0=login inválido, 1=bloqueado, 2=sucesso
    	if($resposta == 2){
    		//sucesso!
    		$url = SYSTEM_URL;
    		header ( "location:$url" );
    	}
    	elseif($resposta == 0){
    		//$url = $this->view->link('login', 'index/erro/true');
    		$this->view->assign ( 'erroLogin', $this->erroLogin );
    	}
    	elseif($resposta == 1){
    		//$url = $this->view->link('login', 'index/erro/true');
    		$this->view->assign ( 'erroLogin', 'Seu acesso está bloqueado.' );
    	}
    	
    	
    }

    private function validarFormulario()
    {
    	//validação de formulários
    	$nome = $_POST ['nome'];
    	$nascimento = $_POST ['ano']."-".$_POST ['mes']."-".$_POST ['dia'];
    	$email = $_POST ['email'];
    	$confEmail = $_POST ['confEmail'];
    	$senha = $_POST ['senha'];
    	$confSenha = $_POST['confSenha'];

    	// verificações comuns para cadastro e edição
    	if (strlen ( $nome ) < 1 || strlen ( $nome ) > 45) {
    		$erro = $this->erroNome;
    		$this->view->assign ( 'erroNome', $erro );
    	}
    	
    	if (! filter_var ( $email, FILTER_VALIDATE_EMAIL ) || strlen ( $email ) < 1 || strlen ( $email ) > 45) {
    		$erro = $this->erroEmail;
    		$this->view->assign ( 'erroEmail', $erro );
    	}
    	
    	if (strcmp ( $email, $confEmail )) {
    		$erro = $this->erroConfEmail;
    		$this->view->assign ( 'erroConfEmail', $erro );
    	}
    	
    	if (strcmp ( $senha, $confSenha )) {
    		$erro = $this->erroConfSenha;
    		$this->view->assign ( 'erroConfSenha', $erro );
    	}
    	
	
    	if (! preg_match ( '#(\d{4})-(\d{2})-(\d{2})#', $nascimento )) {
    		$erro = $this->erroNascimento;
    		$this->view->assign ( 'erroNascimento', $erro );
    	}
    			
    	// verifica se e-mail já está cadastrado	
    	$row = $this->model->getMotoristaByEmail ( $email );
    	if ($row) {
    		$erro = $this->erroEmailExiste;
    		$this->view->assign ( 'erroEmail', $erro );
    	}
    	//
    			
    	if (strlen ( $senha ) < 4 || strlen ( $senha ) > 16) {
    		$erro = $this->erroSenha;
    		$this->view->assign ( 'erroSenha', $erro );
    	}
    	//
    	
    	if ($erro) {
    		return false;
    	}
    	return true;
    	
    }

    private function validarCadastro()
    {
    	
    	if ($_POST) { // Se submetido formulário, faz tratamento.
    		$this->view->assign ( 'tituloCadastrar', $this->tituloCadastrar );
    		$this->view->assign ( 'textoCadastrar', $this->textoCadastrar );
    		// recupera dados entrados
    		$this->view->assign ( 'postNome', $_POST ['nome'] );
    		$this->view->assign ( 'postDia', $_POST ['dia'] );
    		$this->view->assign ( 'postMes', $_POST ['mes'] );
    		$this->view->assign ( 'postAno', $_POST ['ano'] );
    		$this->view->assign ( 'postEmail', $_POST ['email'] );
    		$this->view->assign ( 'postConfEmail', $_POST ['confEmail'] );
    			
    		if ($this->validarFormulario ()) {
    			// transforma data de padrão br para padrão sql
    			// converte data
    			$nascimento = $_POST ['ano']."-".$_POST ['mes']."-".$_POST ['dia'];
    	
    			try {
    				$id = $this->model->cadastrar ( $_POST ['nome'], $nascimento, $_POST ['email'], sha1 ( $_POST ['senha'] ));
    				$this->motorista->login($_POST ['email'], $_POST ['senha']);
    				$url = SYSTEM_URL;
    				header ( "location:$url" );
    			} catch ( Exception $e ) {
    				//$mensagem = $e->getMessage ();
    			}
    		}
    	}
    }

    public function indexAction()
    {
    	if ($_POST['loginEmail'] && $_POST['loginSenha']) {
    		$this->validarLogin();
    	}
    	else{
    		$this->validarCadastro();
    	}
    	
		$this->view->assign ( 'textoEmail', $this->textoEmail );
		$this->view->assign ( 'textoSenha', $this->textoSenha );
		$this->view->assign ( 'textoEntrar', $this->textoEntrar );
		$this->view->assign ( 'textoPrimeiraVez', $this->textoPrimeiraVez );
		$this->view->assign ( 'textoCadastrar', $this->textoCadastrar );
		$this->view->assign ( 'textoReucuperar', $this->textoReucuperar );
		
		$this->view->assign ( 'hrefCadastrar', $this->view->link ( 'motorista', 'cadastrar' ) );
		$this->view->assign ( 'hrefRecuperar', $this->view->link ( 'login', 'recuperar' ) );
		
		if($this->getParam('erro')){
			$this->view->assign ( 'erroLogin', $this->erroLogin );
		}
    }

    public function recuperarAction()
    {
        $this->view->assign ( 'hrefLogin', $this->view->link ( 'login' ) );
        
        if ($_POST) {
        	//recupera email digitado
        	$this->view->assign ( 'postEmail', $_POST['email'] );
        	
        	if($row = $this->model->getMotoristaByEmail(filter_var($_POST['email']),FILTER_VALIDATE_EMAIL)){
        		
        		$email = $row->email;
        		
        		//gera e salva chave
        		$key = sha1(substr ( sha1 ( uniqid ( mt_rand (), true ) ), 0, 10 ));
        		$row->chave_recuperacao = $key;
        		$row->save();

        		$url = SYSTEM_URL. "login/recuperacao?email=$email&key=$key";
        		
        		require("PHPMailer/PHPMailerAutoload.php");
        		
        		$mail = new PHPMailer();
        		
        		$mail->IsSMTP();  // telling the class to use SMTP
        		$mail->SMTPAuth = true;
        		$mail->Host = "smtp.multskill.com.br"; // SMTP server
        		$mail->Port = 587;
        		$mail->Username = "andre@multskill.com.br";
        		$mail->Password = "qwe78532147-";
        		
        		$mail->From = "andre@multskill.com.br";
        		$mail->AddAddress($email);
        		
        		$mail->Subject  = "Recuperar senha Siga-me";
        		$mail->Body     = "Link para troca de senha: $url";
        		
        		// send mail
        		if(!$mail->Send()){
        			$info = $mail->ErrorInfo;
        			$this->view->assign ( 'mensagem', "Houve uma falha interna o e-mail de troca de senha. Tente novamente mais tarde. $info" );
        		}
        		else{
        			$this->view->assign ( 'mensagem', 'Um e-mail com instruções para troca de senha foi enviado.' );
        		}
        		
        	}
        	else{
        		$this->view->assign ( 'mensagem', 'E-mail não cadastrado ou inválido.' );
        	}
        }
    }

    public function editarsenhaAction()
    {
    	//recupera sessão
    	require_once ('Zend/Session/Namespace.php');
    	$session = new Zend_Session_Namespace ( 'recuperacao' );
    	Zend_Session::start ();
    	
    	$this->view->assign ( 'hrefLogin', $this->view->link ( 'login' ) );
    	
    	if($session->motorista){
    		if ($_POST) {
    			
    			//valida nova senha
    			if (strlen($_POST['senha']) >= 4 && strlen($_POST['senha']) <= 16 && strcmp($_POST['senha'], $_POST['confSenha']) == 0) {
    				$row = $this->model->getMotorista($session->motorista);
    				$row->senha = sha1($_POST['senha']);
    				$row->save();
    				
    				Zend_Session::namespaceUnset ( "recuperacao" );
    				
    				$url = $this->view->link('login');
    				header("location:$url");
    			}
    			else{
    				$this->view->assign ( 'mensagem', 'Erro. Verifique se a senha digitada possui entre 4 e 16 caracteres ou se a confirmação está correta.' );
    			}
    		}
    	}
    	else{
    		$url = $this->view->link('login');
    		header("location:$url");
    	}
    }

    public function recuperacaoAction()
    {
     	if ($_GET) {
        	try {
        		$row = $this->model->getMotoristaByEmail($_GET['email']);
        		
        		if($row->chave_recuperacao != $_GET['key']){
        			$this->view->assign ( 'mensagem', 'A chave de recuperação é inválida.' );
        		}
        		else{
        			//cria sessão para troca de senha
        			require_once ('Zend/Session/Namespace.php');
        			$session = new Zend_Session_Namespace ( 'recuperacao' );
        			$session->motorista = $row->id_motorista;
        			
					$url = $this->view->link('login', 'editarsenha');
					header("location:$url");
        		}
        	} catch (Exception $e) {
        		$this->view->assign ( 'mensagem', 'ULR de recuperação inválida.' );
        	}
        }
    }


}









