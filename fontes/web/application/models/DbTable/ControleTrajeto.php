<?php

class Application_Model_DbTable_ControleTrajeto extends Zend_Db_Table_Abstract
{

    protected $_name = 'controle_trajeto';
    
    protected  $_referenceMap = array(
    		'Smartphone' => array(
    				'columns' => array('imei'),
    				'refColumns' =>	array('imei'),
    				'refTableClass' => 'Application_Model_DbTable_Smartphone'
    		),
    		'GrupoTrajeto' => array(
    				'columns' => array('grupo_trajeto_id'),
    				'refColumns' =>	array('id_grupo_trajeto'),
    				'refTableClass' => 'Application_Model_DbTable_GrupoTrajeto'
    		)
    );


}

