//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.Date;
import java.util.UUID;

public class RefreshToken extends DbEntry {
	private String ID;

	public RefreshToken() throws Exception {
		this.ID = UUID.randomUUID().toString();
	}

	private String __Subject;

	public String getSubject() {
		return __Subject;
	}

	public void setSubject(String value) {
		__Subject = value;
	}

	private String __ClientId;

	public String getClientId() {
		return __ClientId;
	}

	public void setClientId(String value) {
		__ClientId = value;
	}

	private Date __IssuedUtc;

	public Date getIssuedUtc() {
		return __IssuedUtc;
	}

	public void setIssuedUtc(Date value) {
		__IssuedUtc = value;
	}

	private Date __ExpiresUtc;

	public Date getExpiresUtc() {
		return __ExpiresUtc;
	}

	public void setExpiresUtc(Date value) {
		__ExpiresUtc = value;
	}

	private String __ProtectedTicket;

	public String getProtectedTicket() {
		return __ProtectedTicket;
	}

	public void setProtectedTicket(String value) {
		__ProtectedTicket = value;
	}

}
