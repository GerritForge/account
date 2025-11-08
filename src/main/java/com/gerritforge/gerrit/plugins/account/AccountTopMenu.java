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
import com.google.gerrit.extensions.client.MenuItem;
import com.google.gerrit.extensions.webui.TopMenu;
import com.google.gerrit.server.CurrentUser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Singleton
public class AccountTopMenu implements TopMenu {

  private final Provider<String> pluginUrl;
  private final Provider<CurrentUser> currentUserProvider;

  @Inject
  public AccountTopMenu(
      @PluginCanonicalWebUrl @Nullable Provider<String> pluginUrl,
      Provider<CurrentUser> currentUserProvider) {
    this.pluginUrl = pluginUrl;
    this.currentUserProvider = currentUserProvider;
  }

  @Override
  public List<MenuEntry> getEntries() {
    if (currentUserProvider.get().isIdentifiedUser()) {
      return Arrays.asList(
          new MenuEntry(
              "Account",
              Arrays.asList(
                  new MenuItem(
                      "Personal Information", pluginUrl.get() + "static/account.html", "_self"))));
    }

    return Collections.emptyList();
  }
}
