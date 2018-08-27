<?php
class IndexController extends Zend_Controller_Action {
	
	public function init() {
		//atribui session
    	include_once 'Sigame/Session/init_session.php';
	}
	
	public function indexAction() {
		$this->view->headTitle ( 'Siga-me' );
	}
}

