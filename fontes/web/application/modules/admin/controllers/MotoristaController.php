<?php

class Admin_MotoristaController extends Zend_Controller_Action
{

    public function init()
    {
    	$this->model = new Application_Model_Motorista();
    	$this->gestor = new Sigame_Session_Gestor();
        if(!$this->gestor->getLogado()){
        	$url = SYSTEM_URL.'admin/login/index';
        	header ( "location:$url" );
        }
    }

    public function indexAction()
    {
    	$this->view->assign('searchIcon', SYSTEM_URL.'public/imagens/' . 'ic_menu_search.png');
    	
        if($_GET){
        	if($_GET['filtro'] == 'nome'){
        		try {
        			$rows = $this->model->getMotoristasByNome($_GET["busca"]);
        		} catch (Exception $e) {
        			echo $e->getMessage();
        		}
        	}
        	elseif ($_GET['filtro'] == 'email'){
        		try{
	        		$rows = $this->model->getMotoristaByBuscarEmail($_GET["busca"]);
        		}
        		catch (Exception $e){
        			echo $e->getMessage();
        		}
        	}
        	try {
        		//monta tabela de motoristas
        		$this->view->placeholder('href')->bloquear = SYSTEM_URL.'admin/motorista/bloquear';;
        		$this->view->partialLoop ()->setObjectKey ( 'row' );
        		$tbody = $this->view->partialLoop ( 'motoristas.phtml', $rows );
        		$this->view->assign ( 'tbody', $tbody );
        	} catch (Exception $e) {
        		echo $e->getMessage();
        	}
        }
    }

    public function bloquearAction()
    {
 		$this->view->assign('hrefVoltar', SYSTEM_URL.'admin/motorista/index');
        if($this->getParam('motorista')){
        	try{
        		$this->model->bloquear($this->getParam('motorista'));
        		$this->view->assign('mensagem', 'Motorista alterado com sucesso!');
        	}
        	catch (Exception $e){
        		$this->view->assign('mensagem', 'Falha ao bloquear motorista');
        	}
        }
    }


}



