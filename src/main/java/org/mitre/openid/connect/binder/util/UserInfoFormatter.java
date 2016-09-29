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
package org.mitre.openid.connect.binder.util;

import java.util.ArrayList;
import java.util.List;

import org.mitre.openid.connect.model.UserInfo;

import com.google.common.base.Joiner;

public class UserInfoFormatter {

	
	public static String displayName(UserInfo u) {
		
		if (u == null) {
			// if there's no userinfo, we have to punt to a higher layer
			return null;
		}
		
		if (u.getName() != null) {
			// prefer the existing display name if it exists
			return u.getName();
			
		} else if (u.getFamilyName() != null
					|| u.getGivenName() != null
					|| u.getMiddleName() != null
					|| u.getNickname() != null) {
			// if the name parts exist, build a display name from them	
			
			List<String> parts = new ArrayList<String>();
			
			if (u.getNickname() != null) {
				parts.add(u.getNickname());
			} else {
				parts.add(u.getGivenName());
			}
			
			parts.add(u.getMiddleName());
			parts.add(u.getFamilyName());
			
			return Joiner.on(" ").skipNulls().join(parts);
			
		} else if (u.getPreferredUsername() != null) {
			// if there's a username, use that
			return u.getPreferredUsername();

		} else {
			// otherwise, fall back to the subject
			return u.getSub();
		}

	}
	
}
