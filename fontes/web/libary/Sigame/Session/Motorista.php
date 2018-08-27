<?php

class Sigame_Session_Motorista {
	private $id;
	private $nome;
	private $nascimento;
	private $email;
	private $senha;
	private $foto_url;
	private $session;
	private $logado;
	
	public function __construct(){
		//include do Zend Session
		require_once ('Zend/Session/Namespace.php');
		
		//obtem a sessao referente ao namespace Login
		$this->session = new Zend_Session_Namespace ( 'Login' );
		$this->id = $this->session->id;
		
		//verifica login
		if (isset( $this->id )) {
			$model = new Application_Model_Motorista();
			$motorista = $model->getMotorista($this->id);
			
			$this->nome = $motorista->nome_motorista;
			$this->nascimento = $motorista->nascimento;
			$this->email = $motorista->email;
			$this->senha = $motorista->senha;
			$this->foto_url = FOTO_URL.$motorista->nome_foto;
			
			if($motorista->bloqueado == 1){
				$this->logout();
			}
			else{
				$this->logado = TRUE;
			}
		}
		else{
			$this->logado = FALSE;
		}
	}
	
	public function login($email, $senha){
		require_once ('Zend/Session/Namespace.php');
		
		$model = new Application_Model_Motorista();
		
		try {
			$motorista = $model->getMotoristaByLogin($email, sha1($senha));
		}
		catch (Exception $e){
			//
		}
		
		if($motorista == null){
			return 0;
		}
		
		if($motorista->bloqueado == 1){
			return 1;
		}
		
		$this->id = $motorista->id_motorista;
		$this->nome = $motorista->nome_motorista;
		$this->nascimento = $motorista->nascimento;
		$this->email = $motorista->email;
		$this->senha = $motorista->senha;
		
		$this->session->id = $this->id;
		$this->logado = TRUE;
		
		return 2;
	}
	
	public function logout(){
		require_once ('Zend/Session/Namespace.php');
		
		Zend_Session::start ();
		Zend_Session::namespaceUnset ( "Login" );
		$this->logado = FALSE;
	}
	
	public function getId(){
		return $this->id;
	}
	
	public function getNome(){
		return $this->nome;
	}
	
	public function getNascimento(){
		return $this->nascimento;
	}
	
	public function getEmail(){
		return $this->email;
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
	
	public function setNascimento($nascimento){
		 $this->nascimento = $nascimento;
	}
	
	public function setEmail($email){
		 $this->email = $email;
	}
	
	public function setSenha($senha){
		 $this->senha = $senha;
	}
	
	public function setFotoUrl($foto_url){
		$this->foto_url = $foto_url;
	}
	
	public function getFotoUrl(){
		return $this->foto_url;
	}
	
	public function getFotoPatch(){
		// Pesquisa a foto pelo hash sha1 do id do contato
		$foto = glob(APPLICATION_PATH.'/../public/fotos/' . sha1($this->getId()) . "*.{jpg,gif,png}", GLOB_BRACE);
	
		// Se encontrou, retorna caminho da foto do motorista, se não, retorna caminho da foto genérica
		return $foto ? $foto[0] : NULL;
	}

}

?>