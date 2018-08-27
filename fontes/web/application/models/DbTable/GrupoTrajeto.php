<?php

class Application_Model_DbTable_GrupoTrajeto extends Zend_Db_Table_Abstract
{

    protected $_name = 'grupo_trajeto';
    protected $_dependentTables = array('Application_Model_DbTable_GrupoTrajetoHasMotorista', 'Application_Model_DbTable_Motorista',
    	'Application_Model_DbTable_ControleTrajeto');
}

