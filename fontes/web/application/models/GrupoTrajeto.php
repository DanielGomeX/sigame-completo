<?php
//http://framework.zend.com/manual/1.12/en/zend.db.table.relationships.html
class Application_Model_GrupoTrajeto
{
	public function __construct() {
		$this->db = Zend_Db_Table::getDefaultAdapter();
		$this->grupoTrajeto = new Application_Model_DbTable_GrupoTrajeto();
		$this->grupoTrajetoHasMotorista = new Application_Model_DbTable_GrupoTrajetoHasMotorista();
	}
	
	public function getLider($id_grupo){
		$row = $this->grupoTrajeto->find ($id_grupo);
		$grupo = $row->current();
		return $grupo->findDependentRowset('Application_Model_DbTable_Motorista');
	}
	
	public function getMotoristas($id_grupo, $autorizado = 2, $fetchMode = 1){
		//se autorizado == 2, retorna todos os motoristas, se não, retorna baseado no parametro autorizado: 0 (não autorizado) ou 1 (autorizado)
		$where = $autorizado == 2 ? "i.grupo_trajeto_id = $id_grupo" : "i.grupo_trajeto_id = $id_grupo AND i.autorizado = $autorizado";
		
		$select = $this->db->select()
			->from(array('i' => 'grupo_trajeto_has_motorista'), 'autorizado')
			->joinInner(array('m' => 'motorista'), 'i.motorista_id = m.id_motorista')
			//->joinInner(array('s' => 'smartphone'), 'i.motorista_id = s.motorista_id', array('imei'))
			->where($where);

		
		return $select->query($fetchMode);
	}
	
	public function cadastrar($id_lider, $nome, $local_encontro, $local_destino, $data_saida, $hora){
	
		$data = array(
				"lider" => $id_lider,
				"nome_grupo_trajeto" => $nome,
				"local_encontro" => $local_encontro,
				"local_destino" => $local_destino,
				"data_saida" => $data_saida,
				"hora_saida" => $hora
	
		);
		$newRow = $this->grupoTrajeto->createRow($data);
		$newRow->save();
		return $newRow->id_grupo_trajeto;
	}
	
	public function editar($id_grupo, $nome, $local_encontro, $local_destino, $data, $hora){
		$row = $this->grupoTrajeto->find($id_grupo);
		
		$row->current()->nome_grupo_trajeto = $nome;
		$row->current()->local_encontro = $local_encontro;
		$row->current()->local_destino = $local_destino;
		$row->current()->data_saida = $data;
		$row->current()->hora_saida = $hora;
		
		$row->current()->save();
	}
	
	public function getGruposTrajeto(){
		return $this->grupoTrajeto->fetchAll();	
	}
	
	
	public function getGrupoTrajeto($id_grupo){
		$row = $this->grupoTrajeto->find($id_grupo);
		return $row->current();
	}
	
	public function getGruposTrajetoByMotoristaId($id_motorista, $fetchMode = 1){
		$select = $this->db->select()
			->from(array('i' => 'grupo_trajeto_has_motorista'))
			->joinInner(array('g' => 'grupo_trajeto'), 'i.grupo_trajeto_id = g.id_grupo_trajeto')
			->joinInner(array('m' => 'motorista'), 'g.lider = m.id_motorista', array('email'))
			->where("i.motorista_id = $id_motorista");
		
		return $select->query($fetchMode);
	}
	
	public function getGruposTrajetoByNome($nome, $fetchMode = 1){
		$select = $this->db->select()
			->from(array('g' => 'grupo_trajeto'), '*')
			->joinInner(array('m' => 'motorista'), 'g.lider = m.id_motorista', array('email'))
			->where("g.nome_grupo_trajeto LIKE '%$nome%'");
		
		return $select->query($fetchMode);
	}
	
	public function getGruposTrajetoByLider($id_lider, $fetchMode = 1){	
		$select = $this->db->select()
			->from(array('g' => 'grupo_trajeto'), '*')
			->joinInner(array('m' => 'motorista'), 'g.lider = m.id_motorista', array('email'))
			->where("g.lider = $id_lider");
		
		return $select->query($fetchMode);
	}
	
	public function getGruposTrajetosByMotoristaEmail($email, $fetchMode = 1){
		$motorista = new Application_Model_Motorista();
		$row = $motorista->getMotoristaByEmail($email);
		return $this->getGruposTrajetoByLider($row->id_motorista, $fetchMode);
	}
	
	public function getGruposTrajetosHasMotorista($id_motorista, $id_grupo_trajeto){
		return $this->grupoTrajetoHasMotorista->fetchRow("motorista_id=$id_motorista AND grupo_trajeto_id=$id_grupo_trajeto");
	}
	
	public function setMotorista($id_motorista, $id_grupo_trajeto){
		$data = array (
				"grupo_trajeto_id" => $id_grupo_trajeto,
				"motorista_id" => $id_motorista
		);
		$newRow = $this->grupoTrajetoHasMotorista->createRow ( $data );
		$newRow->save ();
	}
	
	public function excluirMotorista($id_motorista, $id_grupo_trajeto){
		$this->getGruposTrajetosHasMotorista($id_motorista, $id_grupo_trajeto)->delete();
	}
	
	public function excluirGrupo($id_grupo){
		$this->getGrupoTrajeto($id_grupo)->delete();
	}
	
	public function autorizarMotorista($id_motorista, $id_grupo){
		$row = $this->grupoTrajetoHasMotorista->fetchRow("motorista_id = $id_motorista AND grupo_trajeto_id = $id_grupo");
		$autorizado = $row->autorizado == 0 ? 1 : 0;
		$row->autorizado = $autorizado;
		$row->save();
	}

}

