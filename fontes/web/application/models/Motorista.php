<?php
class Application_Model_Motorista {
	public function __construct() {
		$this->motorista =  new Application_Model_DbTable_Motorista ();
	}
	public function getMotoristas() {
		return $this->motorista->fetchAll ();
	}
	public function getMotorista($id) {
		return $this->motorista->fetchRow ( "id_motorista=$id" );
	}
	public function getMotoristasByNome($nome) {
		return $this->motorista->fetchAll ("nome_motorista LIKE '%$nome%'");
	}
	public function getMotoristaByBuscarEmail($email) {
		return $this->motorista->fetchAll ( "email='$email'" );
	}
	public function getMotoristaByEmail($email) {
		return $this->motorista->fetchRow ( "email='$email'" );
	}
	public function getMotoristaByLogin($email, $senha) {
		return $this->motorista->fetchRow ( "email='$email' AND senha='$senha'" );
	}
	public function cadastrar($nome, $nascimento, $email, $senha) {
		$data = array (
				"nome_motorista" => $nome,
				"nascimento" => $nascimento,
				"email" => $email,
				"senha" => $senha
		);
		$newRow = $this->motorista->createRow ( $data );
		$newRow->save ();
		return $newRow->id_motorista;
	}
	public function editar($id, $nome, $email, $senha) {
		$row = $this->motorista->fetchRow ( "id_motorista=$id" );
		
		$row->nome_motorista = $nome;
		$row->email = $email;
		$row->senha = $senha;
		
		$row->save ();
	}
	public function editarFoto($id, $nome_foto = FOTO_GENERICA){
		$row = $this->motorista->find($id);
		$row->current()->nome_foto = $nome_foto;
		$row->current()->save();
	}
	public function bloquear($id) {
		$row = $this->motorista->find ($id);
		
		$bloqueado = $row->current()->bloqueado == 0 ? 1 : 0;

		$row->current()->bloqueado = $bloqueado;
		
		$row->current()->save ();
	}
	public function redefinirSenha($email, $senha) {
		$row = $this->motorista->fetchRow ( "email='$email'" );
		
		$row->senha = $senha;
		
		$row->save ();
	}
	public function getGruposTrajeto($id_motorista){
		$row = $this->motorista->find ($id_motorista);
		$motorista = $row->current();
		return $motorista->findManyToManyRowset('Application_Model_DbTable_GrupoTrajeto','Application_Model_DbTable_GrupoTrajetoHasMotorista');
	}
}

