<?php

class Api_MotoristaController extends Zend_Controller_Action
{

    public function init()
    {
        $this->model = new Application_Model_Motorista ();
    }
    
    private function validarKey($api_key)
    {
    	if ($api_key == API_KEY) {
    		return TRUE;
    	}
    
    	return FALSE;
    }
    
    /*
     * Função que valida os dados recebidos por _GET
    * Retorno: código do erro ou false caso não haja.
    * Legenda de erros:
    * 2 - Nome, 3 - formato de email, 4 data de nascimento, 5 - e-mail já cadastrado, 6 senha atual inválida (edição), 7 - API KEY
    */
    private function validar($motorista) { //validação de formulários
    	$erro = FALSE;
    	
    	$nome = $motorista->nome;
    	$email = $motorista->email;
    	
    	if($this->validarKey($motorista->api_key) == FALSE){
    		$erro = 7;
    	}
    
    	// verificações comuns para cadastro e edição
    	if (strlen ( $nome ) < 1 || strlen ( $nome ) > 45) {
    		$erro = 2;
    	}
    
    	if (! filter_var ( $email, FILTER_VALIDATE_EMAIL ) || strlen ( $email ) < 1 || strlen ( $email ) > 45) {
    		$erro = 3;
    	}
    
    	// verificações específicas de cadastro e edição
    	if (!$motorista->id) { // se não há sessão de login, valida cadastro
    		$nascimento = $motorista->nascimento;
    			
    		if (! preg_match ( '#(\d{2})/(\d{2})/(\d{4})#', $nascimento )) {
    			$erro = 4;
    		}
    			
    		// verifica se e-mail já está cadastrado
    		
    		$row = $this->model->getMotoristaByEmail ( $email );
    		if ($row) {
    			$erro = 5;
    		}
    		//
    			
    		
    	} else { // valida edição
    		$id = $motorista->id;
    		$senhaAtual = $motorista->senhaAtual;
    			
    		try{
	    		$modelMotorista = $this->model->getMotorista($id);
	    			
	    		$emailCadastro = $modelMotorista->email;
	    		$senhaCadastro = $modelMotorista->senha;
	    			
	    		if (strcmp ( $emailCadastro, $email )) {
	    			// verifica se e-mail já está cadastrado
	    			$row = $this->model->getMotoristaByEmail ( $email );
	    			if ($row) {
	    				$erro = 5;
	    			}
	    			//
	    		}
    		}
    		catch (Exception $e){
    			$erro = 1;
    		}
    			
    		if (strcmp ( $senhaCadastro, $senhaAtual )) {
    			$erro = 6;
    		}
    	}
    	//
    
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
    				$row = $this->model->getMotorista($params->id_motorista);
    				$row->nome_foto = FOTO_URL.$row->nome_foto;
    				$posts [] = array (
    						"post" => $row->toArray ()
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
    		$motorista = json_decode($json);
    	
	        if (!$codigo = $this->validar ($motorista)) {
				// converte data
				$nascimento = preg_replace_callback("#(\d{2})/(\d{2})/(\d{4})#", function($matches){
					return "{$matches[3]}-{$matches[2]}-{$matches[1]}";
					}, $motorista->nascimento);
				
				try {
					$id = $this->model->cadastrar ( $motorista->nome, $nascimento, $motorista->email,  $motorista->senha);
					
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

    public function loginAction()
    {
    	// http://framework.zend.com/apidoc/1.9/Zend_Db/Table/Zend_Db_Table_Rowset_Abstract.html
    	
		if(strcmp('send-json', $_POST['method']) == 0){
    		$json = utf8_encode($_POST['json']);
    		$login = json_decode($json);
    		
			try {
				if ($row = $this->model->getMotoristaByLogin ( filter_var ( $login->email, FILTER_VALIDATE_EMAIL ),  $login->senha  )) {
					
					if($row->bloqueado == 1){
						$posts [] = array (
							"post" => array (
									"erro" => true,
									"codigo" => 2 
							) 
						);
					}
					else{
						$posts [] = array (
								"post" => $row->toArray () 
						);
					}
				} else {
					$posts [] = array (
							"post" => array (
									"erro" => true,
									"codigo" => 1 
							) 
					);
				}
			} catch ( Exception $e ) {
				$posts [] = array (
						"post" => array (
								"erro" => true,
								"codigo" => 3 
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
    		$motorista = json_decode($json);
    	
	        if (!$codigo = $this->validar ($motorista)) {

				$row = $this->model->getMotorista($motorista->id);
				$senha = $row->senha;
				if (strcmp($motorista->senha, "null")) {
					$senha = $motorista->senha;
				}
				
				try {
					$this->model->editar ( $motorista->id, $motorista->nome, $motorista->email, $senha );
					
					$posts [] = array (
							"post" => array (
									"sucesso" => true
							)
					);
					
				} catch ( Exception $e ) {
					$mensagem = $e->getMessage ();
					
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


}









