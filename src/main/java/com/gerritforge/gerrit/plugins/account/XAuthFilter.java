// Copyright (C) 2018 GerritForge Ltd
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.plugins.account;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.httpd.AllRequestFilter;
import com.google.gerrit.httpd.WebSession;
import com.google.gerrit.server.AccessPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class XAuthFilter extends AllRequestFilter {
  public static final String ALLOWED_URI_SUFFIX = "/a/accounts/self";
  
  private static final Logger log = LoggerFactory.getLogger(XAuthFilter.class);
  
  private DynamicItem<WebSession> webSession;

  @Inject
  public XAuthFilter(DynamicItem<WebSession> webSession) {
    this.webSession = webSession;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String uri = ((HttpServletRequest) request).getRequestURI();
    
    if(uri.endsWith(ALLOWED_URI_SUFFIX)) {
      WebSession session = webSession.get();
      if(session != null && session.isSignedIn() && session.getXGerritAuth() != null) {
        String currentUser = session.getUser().getUserName();
        log.info("REST API URI {} allowed for user {}", uri, currentUser);
        session.setAccessPathOk(AccessPath.REST_API, true);
      }
    }
    
    chain.doFilter(request, response);
  }
}
