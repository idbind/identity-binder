package org.mitre.openid.connect.binder.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Model class for representing a user with one or more OpenID Connect
 * identities.
 * 
 * @author wkim
 *
 */
@Entity
@Table(name = "multiple_identity")
public class MultipleIdentity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "multipleIdentity")
	@Cascade(CascadeType.ALL)
	private Set<SingleIdentity> identities;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the identities
	 */
	public Set<SingleIdentity> getIdentities() {
		return identities;
	}

	/**
	 * @param identities
	 *            the identities to set
	 */
	public void setIdentities(Set<SingleIdentity> identities) {

		if (identities != null) {
			for (SingleIdentity identity : identities) {
				if (identity != null) {
					identity.setMultipleIdentity(this);
				}
			}
		}
		this.identities = identities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((identities == null) ? 0 : identities.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (identities == null) {
			if (other.identities != null)
				return false;
		} else if (!identities.equals(other.identities))
			return false;
		return true;
	}

}
