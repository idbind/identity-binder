package org.mitre.openid.connect.binder.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Model class for an OpenID Connect identity.
 * 
 * @author wkim
 *
 */
@Entity
public class Identity {
	
	@EmbeddedId
	private SubjectIssuer subjectIssuer;
	
	// implemented as a raw JSON string to facilitate compatibility with differing user info claims sets
	@Column(name = "user_info_json_string")
	private String userInfoJsonString;

	
	/**
	 * @return the subjectIssuer
	 */
	public SubjectIssuer getSubjectIssuer() {
		return subjectIssuer;
	}
	/**
	 * @param subjectIssuer the subjectIssuer to set
	 */
	public void setSubjectIssuer(SubjectIssuer subjectIssuer) {
		this.subjectIssuer = subjectIssuer;
	}
	/**
	 * @return the userInfoJsonString
	 */
	public String getUserInfoJsonString() {
		return userInfoJsonString;
	}
	/**
	 * @param userInfoJsonString the userInfoJsonString to set
	 */
	public void setUserInfoJsonString(String userInfoJsonString) {
		this.userInfoJsonString = userInfoJsonString;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subjectIssuer == null) ? 0 : subjectIssuer.hashCode());
		result = prime * result + ((userInfoJsonString == null) ? 0 : userInfoJsonString.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Identity other = (Identity) obj;
		if (subjectIssuer == null) {
			if (other.subjectIssuer != null)
				return false;
		} else if (!subjectIssuer.equals(other.subjectIssuer))
			return false;
		if (userInfoJsonString == null) {
			if (other.userInfoJsonString != null)
				return false;
		} else if (!userInfoJsonString.equals(other.userInfoJsonString))
			return false;
		return true;
	}
	
}

