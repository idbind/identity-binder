package org.mitre.openid.connect.binder.model;

/**
 * Model class for an OpenID Connect identity.
 * 
 * @author wkim
 *
 */
public class Identity {
	
	private String issuer;
	private String subject;
	
	// raw JSON string to facilitate compatibility with differing user info claims sets
	private String userInfoJsonString;
	
	/**
	 * @return the issuer
	 */
	public String getIssuer() {
		return issuer;
	}
	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
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
		result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (userInfoJsonString == null) {
			if (other.userInfoJsonString != null)
				return false;
		} else if (!userInfoJsonString.equals(other.userInfoJsonString))
			return false;
		return true;
	}

	
	
	
}
