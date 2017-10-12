/*



 */

package com.xh.dto;

import com.xh.entry.Menu;

public class MenuDto  extends Menu {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 7914634447346022937L;
	
	private transient Boolean createTemplate ;
    
	/**
	 * @return the createTemplate
	 */
 
	public Boolean isCreateTemplate() {
		return createTemplate;
	}
	/**
	 * @param  设置 createTemplate
	 */
	public void setCreateTemplate(Boolean createTemplate) {
		this.createTemplate = createTemplate;
	}
	
	
	
}
