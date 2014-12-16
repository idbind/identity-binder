package org.mitre.openid.binder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Sets;

/**
 * Simple save and retrieval tests to verify Spring data repository magic is working.
 * 
 * @author wkim
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BinderApplication.class)
@WebAppConfiguration
public class RepositoryTest {

	@Autowired
	private SingleIdentityRepository singleIdentityRepository;
	
	@Autowired
	private MultipleIdentityRepository multipleIdentityRepository;
	
	@Before
	public void setUp() {
		singleIdentityRepository.deleteAll();
		multipleIdentityRepository.deleteAll();
	}
	
	// SingleIdentity tests
	
	@Test
	public void testSingleIdentityRoundTripSuccess() {
		SingleIdentity identity = new SingleIdentity();
		identity.setSubject("user");
		identity.setIssuer("www.example.com");
		
		singleIdentityRepository.save(identity);
		
		SingleIdentity result = singleIdentityRepository.findBySubjectAndIssuer("user", "www.example.com");
		
		assertThat(result, equalTo(identity));
	}
	
	@Test
	public void testSingleIdentityRoundTripFailure() {
		SingleIdentity identity = new SingleIdentity();
		identity.setSubject("user");
		identity.setIssuer("www.example.com");
		
		singleIdentityRepository.save(identity);
		
		identity.setUserInfoJsonString("{}");
		
		assertThat(singleIdentityRepository.findBySubjectAndIssuer("user", "www.example.com"), not(equalTo(identity)));
	}

	
	// MultipleIdentity tests
	
	@Test
	public void testMultipleIdentityRoundTripSuccess() {
		SingleIdentity identity1 = new SingleIdentity();
		identity1.setId(1L);
		identity1.setSubject("user1");
		identity1.setIssuer("www.example.com");
		
		SingleIdentity identity2 = new SingleIdentity();
		identity2.setId(2L);
		identity2.setSubject("user2");
		identity2.setIssuer("www.example.com");
		
		MultipleIdentity multi = new MultipleIdentity();
		multi.setId(1L);
		
		multi.setIdentities(Sets.newHashSet(identity1, identity2));
		
		multipleIdentityRepository.save(multi);
		
		MultipleIdentity multiFromDB = multipleIdentityRepository.findOne(1L);
		
		assertThat(multiFromDB, equalTo(multi));
	}
	
	@Test
	public void testMultipleToSingleCascadeSave() {
		SingleIdentity identity1 = new SingleIdentity();
		identity1.setSubject("user1");
		identity1.setIssuer("www.example.com");
		
		SingleIdentity identity2 = new SingleIdentity();
		identity2.setSubject("user2");
		identity2.setIssuer("www.example.com");
		
		MultipleIdentity multi = new MultipleIdentity();
		multi.setId(1L);
		
		multi.setIdentities(Sets.newHashSet(identity1, identity2));
		
		multipleIdentityRepository.save(multi);

		
		// test cascade
		SingleIdentity singleResult1 = singleIdentityRepository.findBySubjectAndIssuer("user1", "www.example.com");
		identity1.setId(singleResult1.getId());
		assertThat(singleResult1, equalTo(identity1));
		SingleIdentity singleResult2 = singleIdentityRepository.findBySubjectAndIssuer("user2", "www.example.com");
		identity2.setId(singleResult2.getId());
		assertThat(singleResult2, equalTo(identity2));
	}
	
	/* TODO test custom repository query once it exists..
	@Test
	public void testMultipleIdentityQueryBySingleIdentity() {
		SubjectIssuer subjectIssuer1 = new SubjectIssuer("user1", "www.example.com");
		SingleIdentity identity1 = new SingleIdentity();
		identity1.setSubjectIssuer(subjectIssuer1);
		
		SubjectIssuer subjectIssuer2 = new SubjectIssuer("user2", "www.example.com");
		SingleIdentity identity2 = new SingleIdentity();
		identity2.setSubjectIssuer(subjectIssuer2);
		
		MultipleIdentity multi = new MultipleIdentity();
		multi.setId(1L);
		
		identity1.setMultipleIdentity(multi);
		identity2.setMultipleIdentity(multi);
		
		multi.setIdentities(Sets.newHashSet(identity1, identity2));
		
		multipleIdentityRepository.save(multi);
		
		// test success case
		MultipleIdentity multiFromDB1 = multipleIdentityRepository.findBySingleIdentity(identity1);
		MultipleIdentity multiFromDB2 = multipleIdentityRepository.findBySingleIdentity(identity2);
		
		assertThat(multiFromDB1, equalTo(multi));
		assertThat(multiFromDB2, equalTo(multi));		
	}
	*/
}
