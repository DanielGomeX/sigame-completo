<?php

class Application_Model_ControleTrajeto
{
	public function __construct() {
		$this->controleTrajeto = new Application_Model_DbTable_ControleTrajeto();
		$this->smartphone = new Application_Model_DbTable_Smartphone();
		$this->db = Zend_Db_Table::getDefaultAdapter();
	}
	
	public function getSmartphones($id_grupo){
		$select = $this->db->select()
			->from(array('i' => 'controle_trajeto'))
			->joinInner(array('s' => 'smartphone'), 's.imei = i.imei')
			->joinInner(array('m' => 'motorista'), 's.motorista_id = m.id_motorista', array('nome_motorista', 'nome_foto'))
			->where("i.grupo_trajeto_id = $id_grupo");
		
		return $select->query(0);
	}
	
	public function getFotos($id_grupo){
		$select = $this->db->select()
		->from(array('i' => 'controle_trajeto'))
			->joinInner(array('s' => 'smartphone'), 's.imei = i.imei')
			->joinInner(array('m' => 'motorista'), 's.motorista_id = m.id_motorista', array('nome_foto'))
			->where("i.grupo_trajeto_id = $id_grupo");
		
		return $select->query(0);
	}
	
	public function addControleTrajeto($id_grupo, $imei){
		$data = array(
				"grupo_trajeto_id" => $id_grupo,
				"imei" => $imei,
		
		);
		$newRow = $this->controleTrajeto->createRow($data);
		$newRow->save();
		
	}
	
	public function setPosicao($imei, $lat, $lng, $data){
		$row = $this->smartphone->find($imei);
	
		$row->current()->latitude = $lat;
		$row->current()->longitude = $lng;
		$row->current()->data_posicao = $data;
	
		$row->current()->save();
	}

}

