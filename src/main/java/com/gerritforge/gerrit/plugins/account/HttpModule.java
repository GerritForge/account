
package com.gerritforge.gerrit.plugins.account;

import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.httpd.AllRequestFilter;
import com.google.inject.servlet.ServletModule;

public class HttpModule extends ServletModule {
  
  @Override
  protected void configureServlets() {
    DynamicSet.bind(binder(), AllRequestFilter.class).to(XAuthFilter.class);
  }
}
