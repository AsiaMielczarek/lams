/******************************************************************************
 * LearningDesignRepositoryService.java
 * 
 * Copyright (c) 2007 LAMS Foundation
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 *****************************************************************************/

/**
 * LearningDesignRepositoryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.lamsfoundation.lams.webservice;

public interface LearningDesignRepositoryService extends javax.xml.rpc.Service {
    public java.lang.String getLearningDesignRepositoryAddress();

    public org.lamsfoundation.lams.webservice.LearningDesignRepository getLearningDesignRepository() throws javax.xml.rpc.ServiceException;

    public org.lamsfoundation.lams.webservice.LearningDesignRepository getLearningDesignRepository(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
