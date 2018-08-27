<?php

class Sigame_Session_Gestor {
	private $id;
	private $nome;
	private $senha;
	private $logado;
	
	public function __construct(){
		//include do Zend Session
		require_once ('Zend/Session/Namespace.php');
		
		//obtem a sessao referente ao namespace Login
		$this->session = new Zend_Session_Namespace ( 'login_gestor' );
		$this->id = $this->session->id;
		
		//verifica login
		if (isset( $this->id )) {
			$model = new Application_Model_Gestor();
			$gestor = $model->getGestor($this->id);
			
			$this->id = $gestor->id_gestor;
			$this->nome = $gestor->nome_gestor;
			$this->senha = $gestor->senha;
			$this->logado = TRUE;
		}
		else{
			$this->logado = FALSE;
		}
	}
	
	public function login($id, $senha){
		require_once ('Zend/Session/Namespace.php');
		
		$model = new Application_Model_Gestor();
		
		try {
			$gestor = $model->getGestorByLogin($id, sha1($senha));
		}
		catch (Exception $e){
			//
		}
		
		if($gestor == null){
			return FALSE;
		}
		
		$this->id = $gestor->id_gestor;
		$this->nome = $gestor->nome_gestor;
		$this->senha = $gestor->senha;
		
		$this->session->id = $this->id;
		$this->logado = TRUE;
		
		return TRUE;
	}
	
	public function logout(){
		require_once ('Zend/Session/Namespace.php');
		
		Zend_Session::start ();
		Zend_Session::namespaceUnset ( "login_gestor" );
		$this->logado = FALSE;
	}
	
	public function getId(){
		return $this->id;
	}
	
	public function getNome(){
		return $this->nome;
	}

	public function getSenha(){
		return $this->senha;
	}
	
	public function getLogado(){
		return $this->logado;
	}
	
	public function setId($id_motorista){
		 $this->id= $id_motorista;
	}
	
	public function setNome($nome_motorista){
		 $this->nome = $nome_motorista;
	}
	
	public function setSenha($senha){
		 $this->senha = $senha;
	}
	
}

?>