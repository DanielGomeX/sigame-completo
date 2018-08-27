<?php

class Api_GrupoController extends Zend_Controller_Action
{

    public function init()
    {
        $this->model = new Application_Model_GrupoTrajeto();
    }

    private function validarKey($api_key)
    {
    	if ($api_key == API_KEY) {
    		return TRUE;
    	}
    	
    	return FALSE;
    }

    private function validar($grupo)
    {
    	$erro = FALSE;
    	
    	//valida key
    	if (!$this->validarKey($grupo->api_key)) {
    		$erro = 6;
    	}
		
		if (strlen ( $grupo->nome_grupo_trajeto ) < 1) {
		
			$erro = 2;
		}
			
		if (strlen ( $grupo->local_encontro ) < 1) {
		
			$erro = 3;
		}
		
		if (! preg_match ( '#(\d{4})-(\d{2})-(\d{2})#', $grupo->data_saida )) {
			$erro = 4;
		}
		
		if (! preg_match ( '#(\d{2}):(\d{2})#', $grupo->hora_saida )) {
			$erro = 5;
		}
		
		return $erro;

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
    				$rows = $this->model->getGruposTrajetosByMotoristaEmail(filter_var($params->email_lider, FILTER_VALIDATE_EMAIL), 0);
    				 
    				$posts = array();
    				 
    				foreach ($rows as $row){
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

    public function postAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$grupo = json_decode($json);
    	
	        if (!$codigo = $this->validar ($grupo)) {
				
				try {
					$id = $this->model->cadastrar ($grupo->lider, $grupo->nome_grupo_trajeto, $grupo->local_encontro, $grupo->local_destino, $grupo->data_saida,  $grupo->hora_saida);
					
					$posts [] = array (
							"post" => array (
									"sucesso" => true,
									"id" => $id
							) 
					);
				} catch ( Exception $e ) {
					$posts [] = array (
							"post" => array (
									"erro" => true,
									"codigo" => 1
							) 
					);
				}
			} else {
				$posts [] = array (
						"post" => array (
								"erro" => true,
								"codigo" => $codigo 
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
    		$grupo = json_decode($json);
    		
	        if (!$codigo = $this->validar ($grupo)) {
				
				try {
					
					$this->model->editar ( $grupo->id_grupo_trajeto, $grupo->nome_grupo_trajeto, $grupo->local_encontro, $grupo->local_destino, $grupo->data_saida,  $grupo->hora_saida );
					
					$posts [] = array (
							"post" => array (
									"sucesso" => true
							)
					);
					
				} catch ( Exception $e ) {
					
					$posts [] = array (
							"post" => array (
									"erro" => true,
									"codigo" => 1 
							) 
					);
				}
			} else {
				$posts [] = array (
						"post" => array (
								"erro" => true,
								"codigo" => $codigo 
						) 
				);
			}
			
			header ( 'Content-type: application/json; charset=utf-8' );
			echo json_encode ( array (
					'posts' => $posts
			) );
    	}
    }

    public function getmotoristasAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    	
    		if ($this->validarKey($params->api_key)) {
 		    	try {
  		    		$rows = $this->model->getMotoristas($params->id_grupo, 2, 0);
		    		
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
					echo $e->getMessage();
		    	}
    		}
	    	else{
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

    public function autorizarAction()
    {
        if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    	
    		if ($this->validarKey($params->api_key)) {
		    	try {
		    		$this->model->autorizarMotorista($params->id_motorista, $params->id_grupo);
		    		
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
    		else{
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

    public function joinAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    		 
    		if ($this->validarKey($params->api_key)) {
    			try {
    				$this->model->setMotorista($params->id_motorista, $params->id_grupo_trajeto);
    				
    				$posts [] = array (
    						"post" => array (
    								"sucesso" => true
    						)
    				);
    			} 
    			catch (Exception $e) {
    				$posts [] = array (
    						"post" => array (
    							"erro" => true,
    							"codigo" => 1
    						)
    				);
    			}
    		}
    		else{
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

    public function unjoinAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    		 
    		if ($this->validarKey($params->api_key)) {
    			try {
    				$this->model->excluirMotorista($params->id_motorista, $params->id_grupo_trajeto);
    				
    				$posts [] = array (
    						"post" => array (
    								"sucesso" => true
    						)
    				);
    			}
    			catch (Exception $e) {
    				$posts [] = array (
    						"post" => array (
    								"erro" => true,
    								"codigo" => 1
    						)
    				);
    			}
    		}
    		else{
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

    public function getbynomeAction()
    {
    	if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$params = json_decode($json);
    	
    		if ($this->validarKey($params->api_key)) {
    			try {
    				$rows = $this->model->getGruposTrajetoByNome($params->nome, 0);
    					
    				$posts = array();
    					
    				foreach ($rows as $row){
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

    //retorna grupos onde o motorista é lider ou participa
    public function getByParticipanteAction()
    {
	    if(strcmp('send-json', $_POST['method']) == 0){
	    		$json = utf8_encode($_POST['json']);
	    		$params = json_decode($json);
	    		
	    		if ($this->validarKey($params->api_key)) {
	     			try {
	    				$rowsForLider = $this->model->getGruposTrajetoByLider($params->id_motorista, 0);
	    				$rowForMotorista = $this->model->getGruposTrajetoByMotoristaId($params->id_motorista, 0);
	    				
	    				$posts = array();
	    				
    					foreach ($rowsForLider as $row){
    						$row['autorizado'] = '1';
    						$posts[] = array('post'=>$row);
    					}
    				 
    					foreach ($rowForMotorista as $row){
    						$posts[] = array('post'=>$row);
    					}
	    				
	    			} catch (Exception $e) {
	    				echo $e->getMessage();
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







