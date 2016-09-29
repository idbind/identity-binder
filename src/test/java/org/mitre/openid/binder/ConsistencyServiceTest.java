/*******************************************************************************
 * Copyright 2016 The MITRE Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mitre.openid.binder;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.mitre.openid.connect.binder.model.MultipleIdentity;
import org.mitre.openid.connect.binder.model.SingleIdentity;
import org.mitre.openid.connect.binder.service.ConsistencyService;
import org.mitre.openid.connect.binder.service.ConsistencyServiceDefault;
import org.mitre.openid.connect.binder.service.IdentityService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
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
public class ConsistencyServiceTest {
	
	@Mock
	OIDCAuthenticationToken token1;
	@Mock
	OIDCAuthenticationToken token2;
	@Mock
	OIDCAuthenticationToken token3;
	
	@Mock
	IdentityService idService;
	
	@InjectMocks
	ConsistencyService consService = new ConsistencyServiceDefault();
	
	// test data
	SingleIdentity identity1;
	SingleIdentity identity2;
	SingleIdentity identity3;
	MultipleIdentity multi1;
	
	@Before
	public void setUp() {
		
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
		
		// unbound identity
		identity3.setSubject("bind me");
		identity3.setIssuer("www.bindme.com");
		
		// token stubs
		Mockito.when(token1.getSub()).thenReturn("user1");
		Mockito.when(token1.getIssuer()).thenReturn("www.example.com");
		Mockito.when(token2.getSub()).thenReturn("user2");
		Mockito.when(token2.getIssuer()).thenReturn("www.example.com");
		Mockito.when(token3.getSub()).thenReturn("bind me");
		Mockito.when(token3.getIssuer()).thenReturn("www.bindme.com");
		
		// idService2 (for ConsistencyService) stubs
		Mockito.when(idService.getMultipleBySubjectIssuer("user1", "www.example.com")).thenReturn(multi1);
		Mockito.when(idService.getMultipleBySubjectIssuer("user2", "www.example.com")).thenReturn(multi1);
		Mockito.when(idService.convertTokenIdentity(token1)).thenReturn(identity1);
		Mockito.when(idService.convertTokenIdentity(token2)).thenReturn(identity2);
		Mockito.when(idService.convertTokenIdentity(token3)).thenReturn(identity3);
		Mockito.when(idService.saveMultipleIdentity(Mockito.any(MultipleIdentity.class))).thenAnswer(new Answer<MultipleIdentity>() {
		    @Override
		    public MultipleIdentity answer(InvocationOnMock invocation) throws Throwable {
		      Object[] args = invocation.getArguments();
		      return (MultipleIdentity) args[0];
		    }
		  });
	}
	
	@Test
	public void testEmptyTokens() {
		
		assertThat(consService.isConsistent(new HashSet<OIDCAuthenticationToken>()), equalTo(true));
	}
	
	@Test
	public void testSingleToken() {
		
		assertThat(consService.isConsistent(Sets.newHashSet(token1)), equalTo(true));
	}
	
	@Test
	public void testBoundIdentities() {
		
		assertThat(consService.isConsistent(Sets.newHashSet(token1, token2)), equalTo(true));
	}
	
	@Test
	public void testBoundAndUnboundIdentities() {
		
		assertThat(consService.isConsistent(Sets.newHashSet(token1, token3)), equalTo(false));
	}
	
	@Test
	public void testDifferentIdentities() {
		
		MultipleIdentity multi2 = new MultipleIdentity();
		multi2.setId(2L);
		multi2.setIdentities(Sets.newHashSet(identity3));
		Mockito.when(idService.getMultipleBySubjectIssuer("bind me", "www.bindme.com")).thenReturn(multi2);
		
		assertThat(consService.isConsistent(Sets.newHashSet(token1, token3)), equalTo(false));
	}
}
