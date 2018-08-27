<?php

class Zend_View_Helper_Link extends Zend_View_Helper_Abstract
{
  public function link($controller = 'index', $action = 'index', $args = array())
  {
    $link = $this->view->baseUrl() . '/';
    $link .= $controller . '/';
    $link .= $action;

    if (!empty($args))
    {
      foreach($args as $arg => $value)
      {
        $link .= '/' . $arg . '/' . $value;
      }
    }

    return $link;
  }
}