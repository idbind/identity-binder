package org.mitre.openid.connect.binder.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Model class for an OpenID Connect identity.
 * 
 * @author wkim
 *
 */
@Entity
@Table(name = "identity")
public class SingleIdentity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	// TODO set a uniqueness constraint on these

	@Column(name = "issuer")
	private String issuer;

	@Column(name = "subject")
	private String subject;
	
	// implemented as a raw JSON string to facilitate compatibility with differing user info claims sets
	@Column(name = "user_info_json_string")
	private String userInfoJsonString;

	// the multiple identity object this is a part of
	@ManyToOne
	@JoinColumn(name = "multiple_identity_id", referencedColumnName = "id")
	private MultipleIdentity multipleIdentity;

	@Column(name = "first_used")
	private Date firstUsed;

	@Column(name = "last_used")
	private Date lastUsed;
	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

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

	/**
	 * @return the multipleIdentity
	 */
	public MultipleIdentity getMultipleIdentity() {
		return multipleIdentity;
	}

	/**
	 * @param multipleIdentity the multipleIdentity to set
	 */
	public void setMultipleIdentity(MultipleIdentity multipleIdentity) {
		this.multipleIdentity = multipleIdentity;
	}

	/**
	 * @return the firstUsed
	 */
	public Date getFirstUsed() {
		return firstUsed;
	}

	/**
	 * @param firstUsed the firstUsed to set
	 */
	public void setFirstUsed(Date firstUsed) {
		this.firstUsed = firstUsed;
	}

	/**
	 * @return the lastUsed
	 */
	public Date getLastUsed() {
		return lastUsed;
	}

	/**
	 * @param lastUsed the lastUsed to set
	 */
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstUsed == null) ? 0 : firstUsed.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
		result = prime * result + ((lastUsed == null) ? 0 : lastUsed.hashCode());
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
		SingleIdentity other = (SingleIdentity) obj;
		if (firstUsed == null) {
			if (other.firstUsed != null)
				return false;
		} else if (!firstUsed.equals(other.firstUsed))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (issuer == null) {
			if (other.issuer != null)
				return false;
		} else if (!issuer.equals(other.issuer))
			return false;
		if (lastUsed == null) {
			if (other.lastUsed != null)
				return false;
		} else if (!lastUsed.equals(other.lastUsed))
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

