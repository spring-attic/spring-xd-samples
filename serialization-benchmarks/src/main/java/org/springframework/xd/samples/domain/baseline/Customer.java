/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.xd.samples.domain.baseline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.util.Assert;

/**
 * A customer.
 * 
 * @author Oliver Gierke
 */
public class Customer {

	protected String firstname, lastname;

	protected EmailAddress emailAddress;

	protected Set<Address> addresses = new HashSet<Address>();

	/**
	 * Creates a new {@link Customer} from the given firstname and lastname.
	 * 
	 * @param firstname must not be {@literal null} or empty.
	 * @param lastname must not be {@literal null} or empty.
	 */
	public Customer(String firstname, String lastname) {

		Assert.hasText(firstname);
		Assert.hasText(lastname);

		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * Adds the given {@link Address} to the {@link Customer}.
	 * 
	 * @param address must not be {@literal null}.
	 */
	public void add(Address address) {

		Assert.notNull(address);
		this.addresses.add(address);
	}

	/**
	 * Returns the firstname of the {@link Customer}.
	 * 
	 * @return
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Returns the lastname of the {@link Customer}.
	 * 
	 * @return
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Sets the lastname of the {@link Customer}.
	 * 
	 * @param lastname
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Returns the {@link EmailAddress} of the {@link Customer}.
	 * 
	 * @return
	 */
	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the {@link Customer}'s {@link EmailAddress}.
	 * 
	 * @param emailAddress must not be {@literal null}.
	 */
	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Return the {@link Customer}'s addresses.
	 * 
	 * @return
	 */
	public Set<Address> getAddresses() {
		return Collections.unmodifiableSet(addresses);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Customer)) return false;

		Customer customer = (Customer) o;

		if (addresses != null ? !addresses.equals(customer.addresses) : customer.addresses != null) return false;
		if (emailAddress != null ? !emailAddress.equals(customer.emailAddress) : customer.emailAddress != null)
			return false;
		if (firstname != null ? !firstname.equals(customer.firstname) : customer.firstname != null) return false;
		if (lastname != null ? !lastname.equals(customer.lastname) : customer.lastname != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = firstname != null ? firstname.hashCode() : 0;
		result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
		result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
		result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
		return result;
	}
}
