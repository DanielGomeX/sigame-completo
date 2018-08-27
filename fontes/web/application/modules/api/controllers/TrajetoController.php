<?php

class Api_TrajetoController extends Zend_Controller_Action
{

    public function init()
    {
        $this->model = new Application_Model_ControleTrajeto();
    }

    private function validarKey($api_key)
    {
    	if ($api_key == API_KEY) {
    		return TRUE;
    	}
    	 
    	return FALSE;
    }

    public function indexAction()
    {
    	// action body
    }

    public function getAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    	
    		if ($this->validarKey($params->api_key)) {
    			try {
			    	$rows = $this->model->getSmartphones($params->id_grupo);
    				
			    	$posts = array();
			    	 
			    	foreach ($rows as $row){
			    		if($row['nome_foto']){
			    			$row['nome_foto'] = FOTO_URL.$row['nome_foto'];
			    		}
			    		$posts[] = array('post'=>$row);
			    	}
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

    public function putAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    		 
    		if ($this->validarKey($params->api_key)) {
		        try {
		        	$this->model->setPosicao($params->imei, $params->lat, $params->lng, $params->data);
		        	
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

    public function postAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    		 
    		if ($this->validarKey($params->api_key)) {
		    	try {
		    		$smartphone = new Application_Model_Smartphone();
		    		$smartphone->addSmartphone($params->imei, $params->id_motorista);
		    		$this->model->addControleTrajeto($params->id_grupo, $params->imei);
		    		 
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

    public function getFotosAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    	
    		if ($this->validarKey($params->api_key)) {
    			try {
			    	$rows = $this->model->getFotos($params->id_grupo);
    				
			    	$posts = array();
			    	 
			    	foreach ($rows as $row){
			    		if($row['nome_foto']){
			    			$row['nome_foto'] = FOTO_URL.$row['nome_foto'];
			    		}
			    		$posts[] = array('post'=>$row);
			    	}
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









