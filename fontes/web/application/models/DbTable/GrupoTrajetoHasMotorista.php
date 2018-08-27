<?php

class Application_Model_DbTable_GrupoTrajetoHasMotorista extends Zend_Db_Table_Abstract
{

    protected $_name = 'grupo_trajeto_has_motorista';
    protected  $_referenceMap = array(
    	'Motorista' => array(
    		'columns' => array('motorista_id'),
    		'refColumns' =>	array('id_motorista'),
    		'refTableClass' => 'Application_Model_DbTable_Motorista'
    	),
    	'GrupoTrajeto' => array(
    			'columns' => array('grupo_trajeto_id'),
    			'refColumns' =>	array('id_grupo_trajeto'),
    			'refTableClass' => 'Application_Model_DbTable_GrupoTrajeto'
    	)
    );


}

