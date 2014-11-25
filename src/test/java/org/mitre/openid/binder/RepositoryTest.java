package org.mitre.openid.binder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.mitre.openid.connect.binder.model.Identity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.mitre.openid.connect.binder.repository.IdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

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
	private IdentityRepository singleIdentityRepository;
	
	@Autowired
	private MultipleIdentityRepository multipleIdentityRepository;
	
	@Test
	public void testRoundTripSuccess() {
		SubjectIssuer subjectIssuer = new SubjectIssuer("user", "www.example.com");
		Identity identity = new Identity();
		identity.setSubjectIssuer(subjectIssuer);
		
		singleIdentityRepository.save(identity);
		
		assertThat(singleIdentityRepository.findOne(subjectIssuer), equalTo(identity));
	}
	
	@Test
	public void testRoundTripFailure() {
		SubjectIssuer subjectIssuer = new SubjectIssuer("user", "www.example.com");
		Identity identity = new Identity();
		identity.setSubjectIssuer(subjectIssuer);
		
		singleIdentityRepository.save(identity);
		
		identity.setUserInfoJsonString("{}");
		
		assertThat(singleIdentityRepository.findOne(subjectIssuer), not(equalTo(identity)));
	}

	@Test
	public void testMultipleIdentity() {
		SubjectIssuer subjectIssuer1 = new SubjectIssuer("user1", "www.example.com");
		Identity identity1 = new Identity();
		identity1.setSubjectIssuer(subjectIssuer1);
		
		SubjectIssuer subjectIssuer2 = new SubjectIssuer("user2", "www.example.com");
		Identity identity2 = new Identity();
		identity2.setSubjectIssuer(subjectIssuer2);
		
		MultipleIdentity multi = new MultipleIdentity();
		multi.setId(1L);
		
		identity1.setMultipleIdentity(multi);
		identity2.setMultipleIdentity(multi);
		
		multi.setIdentities(Sets.newHashSet(identity1, identity2));
		
		multipleIdentityRepository.save(multi);
		
		assertThat(multipleIdentityRepository.findOne(1L), equalTo(multi));
	}
}
