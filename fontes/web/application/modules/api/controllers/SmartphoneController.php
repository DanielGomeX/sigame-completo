<?php

class Api_SmartphoneController extends Zend_Controller_Action
{

    public function init()
    {
    	$this->model = new Application_Model_Smartphone();
    }

    public function indexAction()
    {
        // action body
    }
    
    private function validarKey($api_key)
    {
    	if ($api_key == API_KEY) {
    		return TRUE;
    	}
    	 
    	return FALSE;
    }

    public function postAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    		 
    		if ($this->validarKey($params->api_key)) {
		    	try {
		    		$this->model->addSmartphone($params->imei, $params->id_motorista);
		    		 
		    		$posts [] = array (
		    				"post" => array (
		    						"sucesso" => true
		    				)
		    		);
		    	} catch (Exception $e) {
		    		$posts [] = array (
		    				"post" => array (
		    						"erro" => true,
		    						"codigo" => 1
		    				)
		    		);
		    	}
    		}
    		else {
    			$posts [] = array (
    					"post" => array (
    							"erro" => true,
    							"codigo" => 2
    					)
    			);
    		}
		    	 
	    	header ( 'Content-type: application/json; charset=utf-8' );
	    	echo json_encode ( array (
	    			'posts' => $posts
	    	) );
    	}
    }


}



