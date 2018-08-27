<?php

class Application_Model_Smartphone
{
	public function __construct() {
		$this->smartphone = new Application_Model_DbTable_Smartphone();
	}

	public function addSmartphone($imei, $id_motorista){
		//se houver smartphone cadastrado, atualiza, se não, cadastra.
		if($row = $this->smartphone->fetchRow("imei = '$imei'")){
		
			$row->motorista_id = $id_motorista;
			$row->save();
		}
		else{
			$data = array(
					"imei" => $imei,
					"motorista_id" => $id_motorista
			);
			$newRow = $this->smartphone->createRow($data);
			$newRow->save();
		}
	}
	
}

