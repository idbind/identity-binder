package org.mitre.openid.binder;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.model.SubjectIssuer;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.mitre.openid.connect.binder.service.IdentityServiceDefault;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;

import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = BinderApplication.class)
public class ServiceTest {

	@Mock
	SingleIdentityRepository singleIdentityRepository;
	
	@Mock
	MultipleIdentityRepository multipleIdentityRepository;
	
	@InjectMocks
	IdentityService service = new IdentityServiceDefault();
	
	// test data
	SingleIdentity identity1 = new SingleIdentity();
	SingleIdentity identity2 = new SingleIdentity();
	MultipleIdentity multi = new MultipleIdentity();
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		Mockito.reset(singleIdentityRepository, multipleIdentityRepository);
		
		SubjectIssuer subjectIssuer1 = new SubjectIssuer("user1", "www.example.com");
		identity1.setSubjectIssuer(subjectIssuer1);
		
		SubjectIssuer subjectIssuer2 = new SubjectIssuer("user2", "www.example.com");
		identity2.setSubjectIssuer(subjectIssuer2);
		
		multi.setId(1L);
		
		identity1.setMultipleIdentity(multi);
		identity2.setMultipleIdentity(multi);
		
		multi.setIdentities(Sets.newHashSet(identity1, identity2));
	}
	
	@Test
	public void testGetSingle() {
		SubjectIssuer subjectIssuer1 = new SubjectIssuer("user1", "www.example.com");
		Mockito.when(singleIdentityRepository.findOne(subjectIssuer1)).thenReturn(identity1);
		
		assertThat(service.getSingleBySubjectIssuer("user1", "www.example.com"), equalTo(identity1));
		
	}
	
	@Test
	public void testGetMultiple() {
		MultipleIdentity multi2 = new MultipleIdentity();
		SubjectIssuer subjectIssuer1 = new SubjectIssuer("user1", "www.example.com");
		Mockito.when(singleIdentityRepository.findOne(subjectIssuer1)).thenReturn(identity1);
		SubjectIssuer subjectIssuer2 = new SubjectIssuer("user2", "www.example.com");
		Mockito.when(singleIdentityRepository.findOne(subjectIssuer2)).thenReturn(identity1);
		Mockito.when(multipleIdentityRepository.findAll()).thenReturn(Sets.newHashSet(multi, multi2));
		
		// success case
		assertThat(service.getMultipleBySubjectIssuer("user1", "www.example.com"), equalTo(multi));
		assertThat(service.getMultipleBySubjectIssuer("user2", "www.example.com"), equalTo(multi));
		
		// failure case
		assertThat(service.getMultipleBySubjectIssuer("mr. shouldn't exist", "www.somewhereelse.net"), nullValue());
	}

}
