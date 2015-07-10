package org.mitre.openid.connect.binder.web;

import java.util.ArrayList;
import java.util.List;

import org.mitre.openid.connect.binder.model.IdentityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	@Value( "#{'${idps.names}'.split(',')}" )
	private List<String> idpNames;
	
	@Value( "#{'${idps.identifiers}'.split(',')}" )
	private List<String> idpIdentifiers;
	
	@RequestMapping(value = "/idps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<IdentityProvider> getIdps() {
		
		List<IdentityProvider> idpList = new ArrayList<IdentityProvider>();
		for( int i=0; i<Math.min(idpNames.size(), idpIdentifiers.size()); ++i ) {
			idpList.add(new IdentityProvider(idpNames.get(i), idpIdentifiers.get(i)));
		}
		
		return idpList;
	}
}
