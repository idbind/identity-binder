package org.mitre.openid.connect.binder.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class SubjectIssuer implements Serializable {

	/**
	 * auto-generated serial version uid.
	 */
	private static final long serialVersionUID = -5196694871000667262L;
	
	private String subject;
	
	private String issuer;

	
	public SubjectIssuer() {
		// empty default constructor
	}
	
	public SubjectIssuer(String subject, String issuer) {
		this.subject = subject;
		this.issuer = issuer;
	}
	
	/**
	 * @return the subject
	 */
	@Column(name = "subject", nullable = false)
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
	 * @return the issuer
	 */
	@Column(name = "issuer", nullable = false)
	public String getIssuer() {
		return issuer;
	}

	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
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
		SubjectIssuer other = (SubjectIssuer) obj;
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
		return true;
	}
}
