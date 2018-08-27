<?php

class Admin_LoginController extends Zend_Controller_Action
{

    public function init()
    {
        $this->view->assign('header', 'Login do Gestor');
        $this->model = new Application_Model_Gestor();
        $this->gestor = new Sigame_Session_Gestor();
    }

    public function indexAction()
    {
        if($_POST){
        	if($this->gestor->login($_POST['id'], $_POST['senha'])){
        		$url = SYSTEM_URL.'admin/motorista/index';
        		header ( "location:$url" );
        	}
        	else{
        		$this->view->assign('erro', 'ID ou senha inválidos.');
        	}
        }
    }

    public function logoutAction()
    {
    	$this->gestor->logout();
    	
    	$url = SYSTEM_URL.'admin/login/index';
    	header ( "location:$url" );
    }


}



