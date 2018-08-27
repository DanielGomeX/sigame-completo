<?php

class Application_Model_Gestor
{
	public function __construct(){
		$this->gestor = new Application_Model_DbTable_Gestor();
	}

	public function getGestor($id){
		$row = $this->gestor->find($id);
		
		return $row->current();
	}
	
	public function getGestorByLogin($id, $senha){
		return $this->gestor->fetchRow("id_gestor=$id AND senha='$senha'");
	}
}

