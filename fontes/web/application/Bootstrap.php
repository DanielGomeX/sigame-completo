<?php

class Bootstrap extends Zend_Application_Bootstrap_Bootstrap
{
	
	public function _initNamespace()
	{
		Zend_Loader_Autoloader::getInstance()->registerNamespace('Sigame');
	}
	
	protected function _initLayout(){
		$layout = explode('/', $_SERVER['REQUEST_URI']);
	
		if(in_array('api', $layout)){
			return ;
		}
		elseif (in_array('admin', $layout)){
			$layout_dir = 'admin';
		}
		elseif (in_array('login', $layout)){
			$layout_dir = 'login';
		}
		else{
			$layout_dir = 'default';
		}
	
		$options = array(
				'layout'     => 'layout',
				'layoutPath' => APPLICATION_PATH . "/layouts/scripts/".$layout_dir,
		);
	
		Zend_Layout::startMvc($options);
	}
}
