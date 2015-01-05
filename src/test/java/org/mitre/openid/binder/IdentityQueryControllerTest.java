package org.mitre.openid.binder;

import org.junit.runner.RunWith;
import org.mitre.openid.connect.binder.BinderApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BinderApplication.class)
@WebAppConfiguration
public class IdentityQueryControllerTest {
	
}
