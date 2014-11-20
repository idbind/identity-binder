package org.mitre.openid.binder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.mitre.openid.connect.binder.model.Identity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.mitre.openid.connect.binder.repository.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Simple save and retrieval tests to verify Spring data repository magic is working.
 * 
 * @author wkim
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BinderApplication.class)
public class RepositoryTest {

	@Autowired
	private IdentityRepository repository;
	
	@Test
	public void testRoundTripSuccess() {
		SubjectIssuer subjectIssuer = new SubjectIssuer("user", "www.example.com");
		Identity identity = new Identity();
		identity.setSubjectIssuer(subjectIssuer);
		
		repository.save(identity);
		
		assertThat(repository.findOne(subjectIssuer), equalTo(identity));
	}
	
	@Test
	public void testRoundTripFailure() {
		SubjectIssuer subjectIssuer = new SubjectIssuer("user", "www.example.com");
		Identity identity = new Identity();
		identity.setSubjectIssuer(subjectIssuer);
		
		repository.save(identity);
		
		identity.setUserInfoJsonString("{}");
		
		assertThat(repository.findOne(subjectIssuer), not(equalTo(identity)));
	}

}
