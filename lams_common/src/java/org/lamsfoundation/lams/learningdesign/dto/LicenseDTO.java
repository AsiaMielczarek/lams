/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
package org.lamsfoundation.lams.learningdesign.dto;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Data transfer object for sending License details to Flash.
 * @author Fiona Malikoff
 */
public class LicenseDTO implements Serializable{
	
	private Long licenseID;
	private String name;
	private String code;
	private String url;
	private Boolean defaultLicense;
	private String pictureURL;
	
	/** default constructor*/
	public LicenseDTO(){
		
	}
	/** full constructor*/
	public LicenseDTO(Long licenseID,
				   String name,
				   String code,
				   String url,
				   Boolean defaultLicense,
				   String pictureURL,
				   String serverURL){
		setLicenseID(licenseID);
		setName(name);
		setCode(code);
		setUrl(url);
		setDefaultLicense(defaultLicense);
		setPictureURL(pictureURL, serverURL);
	}
	

	public Boolean getDefaultLicense() {
		return defaultLicense;
	}
	private void setDefaultLicense(Boolean defaultLicense) {
		this.defaultLicense = defaultLicense;
	}
	public Long getLicenseID() {
		return licenseID;
	}
	private void setLicenseID(Long licenseID) {
		this.licenseID = licenseID;
	}
	public String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	public String getPictureURL() {
		return pictureURL;
	}
	private void setPictureURL(String newPictureURL, String serverURL) {
		if ( newPictureURL != null && newPictureURL.startsWith("/") )
			this.pictureURL = serverURL
				+ ( serverURL.endsWith("/") ? newPictureURL : ( "/" + newPictureURL ) );
		else 
			this.pictureURL = newPictureURL;
	}
	
	public String getUrl() {
		return url;
	}
	private void setUrl(String url) {
		this.url = url;
	}
	public String getCode() {
		return code;
	}
	private void setCode(String code) {
		this.code = code;
	}
	public String toString() {
		return new ToStringBuilder(this).append("licenseID",licenseID)
			.append("name",name)
			.append("code",code)
			.append("url",url)
			.append("defaultLicense",defaultLicense)
			.append("pictureURL",pictureURL)
			.toString();
	}

	 /** Compare this LicenseDTO against another LicenseDTO
	  * @return true if all the fields are the same;
	   */
	  public boolean equals(Object another)
	  {
		if( !(another instanceof LicenseDTO) )
		  return false;
		
		if (this == another) {
			     return true;
		}

		LicenseDTO al = (LicenseDTO) another;
		boolean equals = false;
		if( !equals ) equals = ObjectUtils.equals(licenseID, al.licenseID);
		if( !equals ) equals = ObjectUtils.equals(name, al.name);
		if( !equals ) equals = ObjectUtils.equals(code, al.code);
		if( !equals ) equals = ObjectUtils.equals(url, al.url);
		if( !equals ) equals = ObjectUtils.equals(defaultLicense, al.defaultLicense);
		if( !equals ) equals = ObjectUtils.equals(pictureURL, al.pictureURL);
		return equals;
	  }

	  public int hashCode()
	  {
		return (licenseID == null ? 0 : licenseID.hashCode());
	  }

}
