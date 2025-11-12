// Copyright (C) 2025 GerritForge, Inc.
//
// Licensed under the BSL 1.1 (the "License");
// you may not use this file except in compliance with the License.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.plugins.account;

import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.httpd.AllRequestFilter;
import com.google.gerrit.httpd.WebSession;
import com.google.gerrit.server.AccessPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class XAuthFilter extends AllRequestFilter {
  public static final String ALLOWED_GET_URI_SUFFIX = "/a/accounts/self";
  public static final String ALLOWED_DELETE_URI_SUFFIX = "/a/accounts/self/account~";

  private static final Logger log = LoggerFactory.getLogger(XAuthFilter.class);

  private DynamicItem<WebSession> webSession;

  @Inject
  public XAuthFilter(DynamicItem<WebSession> webSession) {
    this.webSession = webSession;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String uri = httpRequest.getRequestURI();
    String method = httpRequest.getMethod();

    if ((method.equals("GET") && uri.endsWith(ALLOWED_GET_URI_SUFFIX))
        || method.equals("DELETE") && uri.endsWith(ALLOWED_DELETE_URI_SUFFIX)) {
      WebSession session = webSession.get();
      if (session != null && session.isSignedIn() && session.getXGerritAuth() != null) {
        session
            .getUser()
            .getUserName()
            .ifPresent(
                currentUser -> {
                  log.info("REST API URI {} allowed for user {}", uri, currentUser);
                  session.setAccessPathOk(AccessPath.REST_API, true);
                });
      } else {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    chain.doFilter(request, response);
  }
}
