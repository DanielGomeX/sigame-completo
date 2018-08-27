<?php
//atribui session
$this->motorista = new Sigame_Session_Motorista();
//verifica login
if (!$this->motorista->getLogado()) {
	$url = $this->view->link ('login');
	header ( "location:$url" );
}

//credenciais
$this->view->assign ( 'layoutFoto', $this->motorista->getFotoUrl() );
$this->view->assign ( 'layoutNome', $this->motorista->getNome() );