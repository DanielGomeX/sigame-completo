<?php
class GrupoController extends Zend_Controller_Action
{

    public function init()
    {
    	//atribui session
    	include_once 'Sigame/Session/init_session.php';
    	
		$this->model = new Application_Model_GrupoTrajeto();
    }

    public function indexAction()
    {
    	$email_motorista_logado = $this->motorista->getEmail();
    	$id_motorista_logado = $this->motorista->getId();
    	
    	//assigns
    	$this->view->placeholder('src')->viewIcon = SYSTEM_URL.'public/imagens/' . 'ic_menu_view.png';
    	$this->view->assign('searchIcon', SYSTEM_URL.'public/imagens/' . 'ic_menu_search.png');
		$this->view->assign ( 'hrefCadastrar', $this->view->link ( 'grupo', 'cadastrar' ) );
		$this->view->assign ( 'hrefQueParticipo', $this->view->link ( 'grupo', "index/motorista/$id_motorista_logado" ) );
		$this->view->assign ( 'hrefQueCriei', $this->view->link ( 'grupo' ) );
		$this->view->assign ( 'action', $this->view->link ( 'grupo' ) );
		
		//busca
		if($_GET['busca']){
			if ($_GET['filtro'] == 'email') {
				try {
					$rows = $this->model->getGruposTrajetosByMotoristaEmail($_GET['busca']);
				}
				catch (Exception $e){
					echo $e->getMessage();
				}
			}
			elseif ($_GET['filtro'] == 'nome'){
				try {
					$rows = $this->model->getGruposTrajetoByNome($_GET['busca']);
				}
				catch (Exception $e){
					echo $e->getMessage();
				}
			}
			
		}
		//botão "que participo"
		elseif($this->getParam('motorista')){
			try {
				$rows = $this->model->getGruposTrajetoByMotoristaId($this->getParam('motorista'));
			}
			catch (Exception $e){
				echo $e->getMessage();
			}
		}
		//botão que criei ou default
		else{
			try {
				$rows = $this->model->getGruposTrajetoByLider($id_motorista_logado);
			}
			catch (Exception $e){
				echo $e->getMessage();
			}
		}
		
		try {
			//monta tabela de grupos
			$this->view->placeholder('href')->ver = $this->view->link ( 'grupo', 'detalhar' );
			$this->view->partialLoop ()->setObjectKey ( 'row' );
			$tbody = $this->view->partialLoop ( 'grupos.phtml', $rows );
			$this->view->assign ( 'tbody', $tbody );
		} catch (Exception $e) {
			//
		}
    }

    private function validar()
    {
		
		$grupoTrajeto = $_POST ['nomeGrupoTrajeto'];
		$localEncontro = $_POST ['localEncontro'];
		$localDestino = $_POST ['localDestino'];
		$horaSaida = $_POST ['horaSaida'];
		
		if (strlen ( $grupoTrajeto ) < 1) {
		
			$erro = 'Preencher o campo Nome do Grupo de Trajeto';
			$this->view->assign ( 'erroNomeGrupoTrajeto', $erro );
		}
			
		if (strlen ( $localEncontro ) < 1) {
		
			$erro = 'Preencher o campo Local de Encontro';
			$this->view->assign ( 'erroLocalEncontro', $erro );
		}
		
		if (strlen ( $localDestino ) < 1) {
		
			$erro = 'Preencher o campo Local de Destino';
			$this->view->assign ( 'erroLocalDestino', $erro );
		}
		
		if (! preg_match ( '#(\d{2})/(\d{2})/(\d{4})#', $_POST ['dataSaida'] )) {
			$erro = 'Preencher corretamente o campo Data de Saída';
			$this->view->assign ( 'erroDataSaida', $erro );
		}
		
		if (! preg_match ( '#(\d{2}):(\d{2})#', $_POST ['horaSaida'] )) {
			$erro = 'Preencher corretamente o campo Hora de Saída';
			$this->view->assign ( 'erroHoraSaida', $erro );
		}
		
		if ($erro) {
			return false;
		}
		return true;
    }

    public function cadastrarAction()
    {
    	//dependencias do calendario
    	$this->view->headLink()->appendStylesheet ( 'http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css' );
    	$this->view->headScript()->appendFile ( 'http://code.jquery.com/jquery-1.8.2.js' );
    	$this->view->headScript()->appendFile ( 'http://code.jquery.com/ui/1.9.0/jquery-ui.js' );
    	//$this->view->headScript()->appendFile ( 'https://maps.googleapis.com/maps/api/js?v=3.exp' );
    	//$this->view->headScript()->appendFile ( $this->getFrontController ()->getBaseUrl () . '/public/js/default/map.js' );
    	
		$id_motorista = $this->motorista->getId();
		
		$this->view->assign ( 'voltarIcon', SYSTEM_URL.'/public/imagens/ic_menu_back.png' );
		$this->view->assign ( 'hrefRetornar', $this->view->link ( 'grupo' ) );
		
		if ($_POST) { // Se submetido formulário, faz tratamento.
			
			// recupera dados entrados
			$this->view->assign ( 'postNome', $_POST ['nomeGrupoTrajeto'] );
			$this->view->assign ( 'postLocalEncontro', $_POST ['localEncontro'] );
			$this->view->assign ( 'postLocalDestino', $_POST ['localDestino'] );
			$this->view->assign ( 'postData', $_POST ['dataSaida'] );
			$this->view->assign ( 'postHora', $_POST ['horaSaida'] );
			
			if($this->validar()){
				// transforma data de padrão br para padrão sql
				$dataSaida = preg_replace_callback("#(\d{2})/(\d{2})/(\d{4})#", function($matches){
						return "{$matches[3]}-{$matches[2]}-{$matches[1]}";
					}, $_POST['dataSaida']);

				try {
					$id_grupo = $this->model->cadastrar ($id_motorista, $_POST ['nomeGrupoTrajeto'], $_POST ['localEncontro'], $_POST ['localDestino'], $dataSaida, $_POST ['horaSaida'] );
					
					$url = $this->view->link ( 'grupo', "detalhar/grupo/$id_grupo" );
					header ( "location:$url" );
				} catch ( Exception $e ) {
					$mensagem = $e->getMessage ();
				}
				
				$this->view->assign ( 'mensagem', $mensagem );
			}
		}
    }

    public function detalharAction()
    {
    	
        if($this->getParam('grupo')){
        	
        	$this->view->assign ( 'voltarIcon', SYSTEM_URL.'/public/imagens/ic_menu_back.png' );
        	$this->view->assign ( 'hrefRetornar', $this->view->link ( 'grupo' ) );
        	
        	//recupera parametros
			$id_grupo = $this->getParam('grupo');
			$id_motorista_logado = $this->motorista->getId();
			
			try{
				//pega dados do grupo
				$grupo = $this->model->getGrupoTrajeto($id_grupo);
				$this->view->assign('grupo', $grupo->nome_grupo_trajeto);
				
				//pega lider do grupo
				$lider = $this->model->getLider($id_grupo);
				
				//set loop
				$this->view->partialLoop ()->setObjectKey ( 'row' );
				
				//operações
				$this->view->assign('tituloMotoristas', 'Confirmados:');
				if($lider->current()->id_motorista == $id_motorista_logado){ //se lider
					
					//mostra motoristas autorizados
					$this->view->placeholder('href')->autorizar = $this->view->link ( 'grupo', 'autorizar' ) . "/grupo/$id_grupo";
					$autorizados = $this->model->getMotoristas($id_grupo, 1);
					$grupoAutorizados = $this->view->partialLoop ( 'grupo_autorizados.phtml', $autorizados );
					$this->view->assign ( 'autorizados', $grupoAutorizados );
					
					//mostra motoristas desautorizados
					$this->view->assign('tituloDesautorizados', 'Aguardando Autorização:');
					//$this->view->placeholder('href')->autorizar = $this->view->link ( 'grupo', 'autorizar' ) . "/grupo/$id_grupo";
					$desautorizados = $this->model->getMotoristas($id_grupo, 0);
					$grupoDesautorizados = $this->view->partialLoop ( 'grupo_desautorizados.phtml', $desautorizados );
					$this->view->assign ( 'desautorizados', $grupoDesautorizados );
					
					//mostra visualização para lider
					$this->view->assign('renderLider', $this->view->render('grupo_lider.phtml'));
					
					//habilita edição
					$this->view->assign('icon', SYSTEM_URL.'public/imagens/' . 'ic_menu_edit.png');
					$this->view->placeholder('href')->operacao = $this->view->link ( 'grupo', 'editar' );
					$this->view->assign('textOperacao', 'Editar grupo');
				}
				else{
					
					//mostra motoristas
					$motoristas = $this->model->getMotoristas($id_grupo, 1);
					$grupoMotoristas = $this->view->partialLoop ( 'grupo_motoristas.phtml', $motoristas );
					$this->view->assign ( 'motoristas', $grupoMotoristas );
					
					//mostra visualização para motorista
					$this->view->assign('idLider', $lider->current()->id_motorista);
					$this->view->assign('nomeLider', $lider->current()->nome_motorista);
					$this->view->assign('fotoLider', FOTO_URL.$lider->current()->nome_foto);
					$this->view->assign('renderMotorista', $this->view->render('grupo_motorista.phtml'));
					
					//verifica se motorista logado pertence ao grupo
					if(!$this->model->getGruposTrajetosHasMotorista($id_motorista_logado, $id_grupo)){
						//join
						$this->view->assign('icon', SYSTEM_URL.'public/imagens/' . 'ic_menu_join.png');
						$this->view->placeholder('href')->operacao = $this->view->link ( 'grupo', 'join' );
						$this->view->assign('textOperacao', 'Juntar ao grupo');
					}
					else{
						//unjoin
						$this->view->assign('icon', SYSTEM_URL.'public/imagens/' . 'ic_menu_unjoin.png');
						$this->view->placeholder('href')->operacao = $this->view->link ( 'grupo', 'unjoin' );
						$this->view->assign('textOperacao', 'Sair do grupo');
					}
				}
				
				$this->view->assign('id', $id_grupo);
			}
			catch (Exception $e){
				echo $e->getMessage();
			}
        }
    }

    public function joinAction()
    {
    	
    	$id_motorista = $this->motorista->getId();
    	$this->view->assign ( 'voltarIcon', SYSTEM_URL.'/public/imagens/ic_menu_back.png' );
    	$this->view->assign('voltar', $this->view->link('grupo', "detalhar/grupo/$id_grupo"));
    	
    	if($this->getParam('grupo')){
    		$id_grupo = $this->getParam('grupo');
	        try {
	        	$this->model->setMotorista($id_motorista, $id_grupo);
	        	
	        	$mensagem = "Sucesso! Aguarde a aprovação do lider do grupo.";
	        } catch (Exception $e) {
	        	$mensagem = $e->getMessage();
	        }
	        $this->view->assign('mensagem', $mensagem);
    	}
    }

    public function unjoinAction()
    {
    	
    	$id_motorista = $this->motorista->getId();
    	
    	if($this->getParam('grupo')){
    		$id_grupo = $this->getParam('grupo');
	        try {
	        	$this->model->excluirMotorista($id_motorista, $id_grupo);
	        	
	        	//redireciona de volta
	        	$return = $this->view->link('grupo', "detalhar/grupo/$id_grupo");
	        	header("location: $return");
	        } catch (Exception $e) {
	        	$mensagem = $e->getMessage();
	        }
	        $this->view->assign('mensagem', $mensagem);
    	}
    }

    public function editarAction()
    {
        //dependencias do calendario
        $this->view->headLink()->appendStylesheet ( 'http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css' );
        $this->view->headScript()->appendFile ( 'http://code.jquery.com/jquery-1.8.2.js' );
        $this->view->headScript()->appendFile ( 'http://code.jquery.com/ui/1.9.0/jquery-ui.js' );
		
        $id_grupo = $this->getParam('grupo');
        $id_motorista = $this->motorista->getId();
        
        if ($this->model->getLider($id_grupo)->current()->id_motorista != $id_motorista) {
        	$return = $this->view->link('grupo');
        	header("location: $return");
        }
        
        $this->view->assign ( 'voltarIcon', SYSTEM_URL.'/public/imagens/ic_menu_back.png' );
        $this->view->assign ( 'hrefRetornar', $this->view->link ( 'grupo', "detalhar/grupo/$id_grupo" ) );	
        $this->view->assign('icon', SYSTEM_URL.'public/imagens/' . 'ic_menu_delete.png');
        $this->view->placeholder('href')->excluir = $this->view->link ( 'grupo', 'excluir' );
        $this->view->assign('idGrupo', $id_grupo);
        	
        $row = $this->model->getGrupoTrajeto($id_grupo);
        $this->view->assign('nomeGrupo', $row->nome_grupo_trajeto);
        	
        //converte data do banco em formato brasileiro
        $dataSaida = preg_replace_callback("#(\d{4})-(\d{2})-(\d{2})#", function($matches){
        	return "{$matches[3]}/{$matches[2]}/{$matches[1]}";
        	}, $row->data_saida);
        	
        $this->view->assign ( 'postNome', $row->nome_grupo_trajeto );
        $this->view->assign ( 'postLocalEncontro', $row->local_encontro );
        $this->view->assign ( 'postLocalDestino', $row->local_destino );
        $this->view->assign ( 'postData', $dataSaida );
        $this->view->assign ( 'postHora', $row->hora_saida );
		
		if ($_POST) { // Se submetido formulário, faz tratamento.
			
			// recupera dados entrados
			$this->view->assign ( 'postNome', $_POST ['nomeGrupoTrajeto'] );
			$this->view->assign ( 'postLocalEncontro', $_POST ['localEncontro'] );
			$this->view->assign ( 'postLocalDestino', $_POST ['localDestino'] );
			$this->view->assign ( 'postData', $_POST ['dataSaida'] );
			$this->view->assign ( 'postHora', $_POST ['horaSaida'] );
			
			if($this->validar()){
				// transforma data de padrão br para padrão sql
				$dataSaida = preg_replace_callback("#(\d{2})/(\d{2})/(\d{4})#", function($matches){
						return "{$matches[3]}-{$matches[2]}-{$matches[1]}";
					}, $_POST['dataSaida']);
				
				try {
					$this->model->editar ($id_grupo, $_POST ['nomeGrupoTrajeto'], $_POST ['localEncontro'], $_POST ['localDestino'], $dataSaida, $_POST ['horaSaida'] );
					
					$return = $this->view->link('grupo', "detalhar/grupo/$id_grupo");
					header("location: $return");
				} catch ( Exception $e ) {
					$mensagem = $e->getMessage ();
				}
				
				$this->view->assign ( 'mensagem', $mensagem );
			}
		}
    }

    public function excluirAction()
    {
    	
    	$id_motorista = $this->motorista->getId();
    	$id_grupo = $this->getParam('grupo');
    	$this->view->assign('voltar', $this->view->link('grupo'));
    	$this->view->assign ( 'voltarIcon', SYSTEM_URL.'/public/imagens/ic_menu_back.png' );
    	
    	try {
    		if ($this->model->getLider($id_grupo)->current()->id_motorista == $id_motorista) {
    			$this->model->excluirGrupo($id_grupo);
    			$mensagem = 'Grupo excluido com sucesso!';
    		}
    	}
    	catch (Exception $e){
    		$mensagem = $e->getMessage();
    	}
    	$this->view->assign('mensagem', $mensagem);
        
    }

    public function autorizarAction()
    {
    	$id_grupo = $this->getParam('grupo');
    	$id_motorista = $this->getParam('motorista');
    	$id_lider = $this->motorista->getId();
    	
    	try {
    		if ($this->model->getLider($id_grupo)->current()->id_motorista == $id_lider) {
    			$this->model->autorizarMotorista($id_motorista, $id_grupo);
    			
    			//redireciona de volta
    			$return = $this->view->link('grupo', "detalhar/grupo/$id_grupo");
    			header("location: $return");
    			
    		}
    		else{
    			$return = $this->view->link('grupo');
    			header("location: $return");
    		}
    	}
    	catch (Exception $e){
    		$mensagem = $e->getMessage();
    	}
    	$this->view->assign('mensagem', $mensagem);
    }


}

















