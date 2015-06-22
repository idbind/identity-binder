package org.mitre.openid.binder;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.repository.MultipleIdentityRepository;
import org.mitre.openid.connect.binder.repository.SingleIdentityRepository;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.mitre.openid.connect.binder.service.IdentityServiceDefault;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;

import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = BinderApplication.class)
public class IdentityServiceTest {

	@Mock
	SingleIdentityRepository singleIdentityRepository;
	
	@Mock
	MultipleIdentityRepository multipleIdentityRepository;
	
	@InjectMocks
	IdentityService idService = new IdentityServiceDefault();
	
	// test data
	SingleIdentity identity1;
	SingleIdentity identity2;
	SingleIdentity identity3;
	MultipleIdentity multi1;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		Mockito.reset(singleIdentityRepository, multipleIdentityRepository);
		
		identity1 = new SingleIdentity();
		identity2 = new SingleIdentity();
		identity3 = new SingleIdentity();
		multi1 = new MultipleIdentity();
		
		// multi1 object has two single identities 1 & 2.
		identity1.setSubject("user1");
		identity1.setIssuer("www.example.com");
		identity2.setSubject("user2");
		identity2.setIssuer("www.example.com");
		multi1.setId(1L);
		multi1.setIdentities(Sets.newHashSet(identity1, identity2));
		
		// new identity to be bound
		identity3.setSubject("bind me");
		identity3.setIssuer("www.bindme.com");
		
		Mockito.when(multipleIdentityRepository.findAll()).thenReturn(Sets.newHashSet(multi1));
		
		Mockito.when(singleIdentityRepository.findBySubjectAndIssuer("user1", "www.example.com")).thenReturn(identity1);
		Mockito.when(singleIdentityRepository.findBySubjectAndIssuer("user2", "www.example.com")).thenReturn(identity2);
		Mockito.when(singleIdentityRepository.findBySubjectAndIssuer("bind me", "www.bindme.com")).thenReturn(identity3);
		
		// have save function simply return same object that was passed in.
		Mockito.when(multipleIdentityRepository.save(Mockito.any(MultipleIdentity.class))).thenAnswer(new Answer<MultipleIdentity>() {
		    @Override
		    public MultipleIdentity answer(InvocationOnMock invocation) throws Throwable {
		      Object[] args = invocation.getArguments();
		      return (MultipleIdentity) args[0];
		    }
		  });
	}
	
	@Test
	public void testGetSingle() {
		
		assertThat(idService.getSingleBySubjectIssuer("user1", "www.example.com"), equalTo(identity1));
		
	}
	
	@Test
	public void testGetMultiple() {
		
		MultipleIdentity multi2 = new MultipleIdentity();
		Mockito.when(multipleIdentityRepository.findAll()).thenReturn(Sets.newHashSet(multi1, multi2));
		
		// success case
		assertThat(idService.getMultipleBySubjectIssuer("user1", "www.example.com"), equalTo(multi1));
		assertThat(idService.getMultipleBySubjectIssuer("user2", "www.example.com"), equalTo(multi1));
		
		// failure case
		assertThat(idService.getMultipleBySubjectIssuer("mr. shouldn't exist", "www.somewhereelse.net"), nullValue());
	}
	
	@Test
	public void testUnbind() {
		
		assertThat(multi1.getIdentities(), hasItems(identity1, identity2));
		
		idService.unbindBySubjectIssuer(multi1, "user1", "www.example.com");
		
		assertThat(multi1.getIdentities(), not(hasItem(identity1)));
	}

}
