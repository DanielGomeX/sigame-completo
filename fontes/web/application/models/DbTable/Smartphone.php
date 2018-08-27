<?php

class Application_Model_DbTable_Smartphone extends Zend_Db_Table_Abstract
{

    protected $_name = 'smartphone';
    protected $_dependentTables = array('Application_Model_DbTable_ControleTrajeto');

}

