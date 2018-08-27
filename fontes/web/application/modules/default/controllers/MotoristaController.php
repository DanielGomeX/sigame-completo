<?php
class MotoristaController extends Zend_Controller_Action
{

    public function init()
    {
    	//atribui session
    	include_once 'Sigame/Session/init_session.php';
    	
		// atribui idioma
		require_once ('Sigame/Language/pt-br/Motorista.php');
		
		//atribui model
		$this->model = new Application_Model_Motorista();
		
		// comuns
		$this->view->assign ( 'textoRetornar', $this->textoRetornar );
		$this->view->assign ( 'textoNome', $this->textoNome );
		$this->view->assign ( 'textoEmail', $this->textoEmail );
		$this->view->assign ( 'textoConfEmail', $this->textoConfEmail );
		$this->view->assign ( 'textoSenha', $this->textoSenha );
		$this->view->assign ( 'textoConfSenha', $this->textoConfSenha );
    }

    private function validarEmail($email)
    {
		if (filter_var ( $email, FILTER_VALIDATE_EMAIL ) && strlen ( $_POST ['email'] ) > 0 && strlen ( $_POST ['email'] ) < 46) {
			$row = $this->model->getMotoristaByEmail ( $email );
			if ($row) {
				return true;
			} else {
				$this->view->assign ( 'erroEmail', $this->erroEmailNaoExiste );
				return false;
			}
		}
		$this->view->assign ( 'erroEmail', $this->erroEmail );
		return false;
    }

    private function validar()
    {
		//validação de formulários
		$nome = $_POST ['nome'];
		$email = $_POST ['email'];
		$confEmail = $_POST ['confEmail'];
		$senha = $_POST ['senha'];
		$confSenha = $_POST['confSenha'];
		$senhaAtual = $_POST ['senhaAtual'];
		
		$emailCadastro = $this->motorista->getEmail();
		$senhaCadastro = $this->motorista->getSenha();
		
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
	
		if ($senha && strlen ( $senha ) < 4 || strlen ( $senha ) > 16) {
			$erro = $this->erroSenha;
			$this->view->assign ( 'erroSenha', $erro );
		}
			
		if (strcmp ( $emailCadastro, $email )) {
			// verifica se e-mail já está cadastrado
				
			$row = $this->model->getMotoristaByEmail ( $email );
			if ($row) {
				$erro = $this->erroEmailExiste;
				$this->view->assign ( 'erroEmail', $erro );
			}
			//
		}
			
		if (strcmp ( $senhaCadastro, sha1 ( $senhaAtual ) )) {
			$erro = $this->erroSenhaAtual;
			$this->view->assign ( 'erroSenhaAtual', $erro );
		}
		
		if ($erro) {
			return false;
		}
		return true;
    }

    public function indexAction()
    {
		//
    }

    public function editarAction()
    {
		// recupera id
		$id = $this->motorista->getId();
		
		$this->view->assign ( 'action', $this->view->link ( 'motorista', 'editar' ) );
		
		$this->view->assign ( 'tituloEditar', $this->tituloEditar );
		$this->view->assign ( 'textoSalvar', $this->textoSalvar );
		$this->view->assign ( 'textoSenhaAtual', $this->textoSenhaAtual );
		
		// recupera dados do motorista
		$this->view->assign ( 'postNome', $this->motorista->getNome() );
		$this->view->assign ( 'postEmail', $this->motorista->getEmail() );
		$this->view->assign ( 'postConfEmail', $this->motorista->getEmail() );
		
		if ($_POST) { // Se submetido formulário, faz tratamento.
		              
			// recupera dados entrados
			$this->view->assign ( 'postNome', $_POST ['nome'] );
			$this->view->assign ( 'postEmail', $_POST ['email'] );
			$this->view->assign ( 'postConfEmail', $_POST ['confEmail'] );
			
			if ($this->validar ()) {
				
				
				$mensagem = $this->msgEditarSucesso;
				
				$senha = $this->motorista->getSenha();
				if (! empty ( $_POST ['senha'] )) {
					$senha = sha1 ( $_POST ['senha'] );
				}
				
				try {
					$this->model->editar ( $id, $_POST ['nome'], $_POST ['email'], $senha );
					
					$this->motorista->setNome($_POST ['nome']);
					$this->motorista->setEmail($_POST ['email']);
					$this->motorista->setSenha($senha);
				} catch ( Exception $e ) {
					$mensagem = $e->getMessage ();
				}
				
				$this->view->assign ( 'mensagem', $mensagem );
			}
		}
    }

    public function bloquearAction()
    {
		// action body
    }

    public function recuperarAction()
    {
		$this->view->assign ( 'hrefRetornar', $this->view->link ( 'motorista', 'index' ) );
		
		$this->view->assign ( 'tituloRecuperar', $this->tituloRecuperar );
		$this->view->assign ( 'msgRecuperarInstrucao', $this->textoInstrucao );
		
		$this->view->assign ( 'action', $this->view->link ( 'motorista', 'recuperar' ) );
		
		if ($_POST) {
			$string = substr ( sha1 ( uniqid ( mt_rand (), true ) ), 0, 10 ); // gera senha randômica
			$senha = sha1 ( $string );
			
			if ($this->validarEmail ( $_POST ['email'] )) {
				$mensagem = $this->msgRecuperarSucesso;
				
				try {
					$this->model->redefinirSenha ( $_POST ['email'], $senha );
					
					// inserir código para envio de e-mail aqui
				} catch ( Exception $e ) {
					$mensagem = $e->getMessage ();
				}
				// pesquisar e inserir exeção de e-mail aqui
				
				$this->view->assign ( 'msgRecuperarSucesso', $mensagem );
			}
		}
    }

    public function logoutAction()
    {
		$this->motorista->logout();
		
		$url = $this->view->link('login');
		header ( "location:$url" );
    }

    public function editarfotoAction()
    {
    	$id = sha1($this->motorista->getId());
    		
	    $this->view->headScript()->appendFile ( $this->getFrontController ()->getBaseUrl () . '/public/js/default/foto.js' );
	    $this->view->assign ( 'generica', $this->motorista->getFotoUrl() );
	    $this->view->assign ( 'hrefExcluir', $this->view->link ( 'motorista', 'excluirfoto' ) );
	    	
	    //cria formulário
		$form = new Zend_Form;
		
		    
		$element = new Zend_Form_Element_File('fileUpload');
		$element->setLabel('Escolha uma foto em seu computador:')
			    ->addValidator('Extension', false, array('jpg', 'png', 'gif'))
			    ->addValidator('Size', false, 102400)
		    	->setAttrib('onchange', 'visualizarFoto();');
		    
	    $submit = new Zend_Form_Element_Submit('Subir');

	    $form->addElements(array($element,$submit));
	    
		$this->view->form = $form;
		
		//valida e executa
		if($this->getRequest()->isPost() and $form->isValid($_POST)){
				
			//exclui foto anterior
			if($fotoAtual = $this->motorista->getFotoPatch()){
				unlink($fotoAtual);
			}
				
			$imageAdapter = new Zend_File_Transfer_Adapter_Http();
			$imageAdapter->setDestination(APPLICATION_PATH.'/../public/fotos');
				
			$extension = pathinfo($imageAdapter->getFileName('fileUpload'), PATHINFO_EXTENSION);
			$name = $id.'.'.$extension;
			$imageAdapter->addFilter('Rename', $name, 'fileUpload');
				
			$this->model->editarFoto($this->motorista->getId(), $name);
				
			if(is_uploaded_file($_FILES['fileUpload']['tmp_name'])){
				if (!$imageAdapter->receive('fileUpload')){
					$messages = $imageAdapter->getMessages['fileUpload'];
					$this->view->assign('mensagem', $messages);
				}else{
					//carrega nova foto
					$this->motorista->setFotoUrl(FOTO_URL.$name);
					$this->view->assign ( 'generica', $this->motorista->getFotoUrl() );
					$this->view->assign ( 'layoutFoto', $this->motorista->getFotoUrl() );
					
					$this->view->assign('mensagem', 'Foto gravada com sucesso!');
				}
			}else{
				$this->view->assign('mensagem', 'Falha ao enviar imagem.');
			}
		}

    }

    public function excluirfotoAction()
    {
    	$this->view->assign ( 'voltarIcon', SYSTEM_URL.'/public/imagens/ic_menu_back.png' );
    	$this->view->assign ( 'hrefRetornar', $this->view->link ( 'motorista', 'editarfoto' ) );
    	
		if($fotoAtual = $this->motorista->getFotoPatch()){
			if(unlink($fotoAtual)){
				$this->model->editarFoto($this->motorista->getId());
				$this->view->assign('mensagem', 'Foto removida com sucesso!');
			}
			else{
				$this->view->assign('mensagem', 'Falha ao excluir foto');
			}
		}
		else{
			$this->view->assign('mensagem', 'Nenhuma foto encontrada.');
		}
    }


}





