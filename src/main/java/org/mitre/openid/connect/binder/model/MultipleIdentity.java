package org.mitre.openid.connect.binder.model;

import java.util.Set;

/**
 * Model class for representing a user with one or more OpenID Connect identities.
 * 
 * @author wkim
 *
 */
public class MultipleIdentity {
	
	private long id;
	private Set<Identity> identities;
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the identities
	 */
	public Set<Identity> getIdentities() {
		return identities;
	}
	/**
	 * @param identities the identities to set
	 */
	public void setIdentities(Set<Identity> identities) {
		this.identities = identities;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((identities == null) ? 0 : identities.hashCode());
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
		MultipleIdentity other = (MultipleIdentity) obj;
		if (id != other.id)
			return false;
		if (identities == null) {
			if (other.identities != null)
				return false;
		} else if (!identities.equals(other.identities))
			return false;
		return true;
	}
	
	
	
}
