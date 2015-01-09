package org.mitre.openid.connect.binder.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Model class for an OpenID Connect identity.
 * 
 * @author wkim
 *
 */
@Entity
@Table(name = "identity", uniqueConstraints = 
			@UniqueConstraint(columnNames = {"issuer", "subject"}))
public class SingleIdentity {
	
	private Long id;
	
	private String issuer;

	private String subject;
	
	// implemented as a raw JSON string to facilitate compatibility with differing user info claims sets
	private String userInfoJsonString;

	private Date firstUsed;

	private Date lastUsed;
	
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	@JsonIgnore
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
	@Column(name = "issuer")
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
	@Column(name = "subject")
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
	@Column(name = "user_info_json_string", length = 4096)
	@JsonIgnore
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
	 * Helper method for getting the UserInfo as an object
	 */
	@Transient
	@JsonIgnore
	public UserInfo getUserInfo() {
		JsonObject obj = new JsonParser().parse(getUserInfoJsonString()).getAsJsonObject();
		return DefaultUserInfo.fromJson(obj);
	}

	public void setUserInfo(UserInfo u) {
		String s = new Gson().toJson(u.toJson());
		setUserInfoJsonString(s);
	}
	
	/**
	 * @return the firstUsed
	 */
	@Column(name = "first_used")
	@JsonIgnore
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
	@Column(name = "last_used")
	@JsonIgnore
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

