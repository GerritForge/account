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

import com.google.gerrit.common.Nullable;
import com.google.gerrit.extensions.annotations.PluginCanonicalWebUrl;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.httpd.AllRequestFilter;
import com.google.gerrit.httpd.WebSession;
import com.google.gerrit.server.config.CanonicalWebUrl;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class AccountLoginRedirectFilter extends AllRequestFilter {
  public static final String REDIRECT_COOKIE = "account_login_redirect";
  public static final int REDIRECT_COOKIE_TTL = 300;

  private final Provider<String> urlProvider;
  private final Provider<String> pluginUrlProvider;
  private final DynamicItem<WebSession> webSession;

  @Inject
  public AccountLoginRedirectFilter(
      @CanonicalWebUrl @Nullable Provider<String> urlProvider,
      @PluginCanonicalWebUrl Provider<String> pluginUrlProvider,
      DynamicItem<WebSession> webSession) {
    this.urlProvider = urlProvider;
    this.webSession = webSession;
    this.pluginUrlProvider = pluginUrlProvider;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Optional<String> redirectUrl =
        getRedirectUrlFromCookie((HttpServletRequest) request)
            .filter(r -> webSession.get().isSignedIn());
    String requestUri = ((HttpServletRequest) request).getRequestURI();
    String pluginUri = getPluginUri();
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (redirectUrl.isPresent() && webSession.get().isSignedIn()) {
      redirectAndResetCookie(httpResponse, redirectUrl.get());
    } else if (requestUri.startsWith(pluginUri) && !webSession.get().isSignedIn()) {
      Cookie redirectCookie = new Cookie(REDIRECT_COOKIE, requestUri);
      redirectCookie.setPath("/");
      redirectCookie.setMaxAge(REDIRECT_COOKIE_TTL);
      redirectCookie.setSecure(false);
      httpResponse.addCookie(redirectCookie);
      httpResponse.sendRedirect(urlProvider.get() + "login");
    } else {
      chain.doFilter(request, response);
    }
  }

  private String getPluginUri() throws ServletException {
    String pluginUrl = pluginUrlProvider.get();
    try {
      return new URI(pluginUrl).getPath();
    } catch (URISyntaxException e) {
      throw new ServletException("Plugin has an invalid canonical URL " + pluginUrl, e);
    }
  }

  private Optional<String> getRedirectUrlFromCookie(HttpServletRequest httpRequest) {
    Cookie[] cookies = httpRequest.getCookies();
    if (cookies == null) {
      return Optional.empty();
    }

    return Arrays.stream(cookies)
        .filter(c -> c.getName().equals(REDIRECT_COOKIE))
        .findFirst()
        .flatMap(c -> resolveUrl(urlProvider.get(), c.getValue()));
  }

  private Optional<String> resolveUrl(String canonicalUrl, String path) {
    if (path.startsWith(canonicalUrl)) {
      return Optional.of(path);
    }

    if (!path.startsWith("http")) {
      return Optional.of(canonicalUrl + (path.startsWith("/") ? path.substring(1) : path));
    }

    return Optional.empty();
  }

  private void redirectAndResetCookie(HttpServletResponse response, String redirectUrl)
      throws IOException {
    Cookie clearCookie = new Cookie(REDIRECT_COOKIE, "");
    clearCookie.setMaxAge(0);
    response.addCookie(clearCookie);
    response.sendRedirect(redirectUrl);
  }

  @Override
  public void destroy() {}
}
