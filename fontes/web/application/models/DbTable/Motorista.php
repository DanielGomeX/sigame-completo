<?php

class Application_Model_DbTable_Motorista extends Zend_Db_Table_Abstract
{

    protected $_name = 'motorista';
    protected $_dependentTables = array('Application_Model_DbTable_GrupoTrajetoHasMotorista');
    
    protected  $_referenceMap = array(
    		'GrupoTrajeto' => array(
    				'columns' => array('id_motorista'),
    				'refColumns' =>	array('lider'),
    				'refTableClass' => 'Application_Model_DbTable_GrupoTrajeto'
    		)
    );


}

