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

package com.googlesource.gerrit.plugins.account;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.access.PluginPermission;
import com.google.gerrit.extensions.api.accounts.AccountApi;
import com.google.gerrit.extensions.api.accounts.Accounts;
import com.google.gerrit.extensions.common.EmailInfo;
import com.google.gerrit.extensions.common.SshKeyInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.server.account.PutName;
import com.google.gerrit.server.account.externalids.ExternalId;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlesource.gerrit.plugins.account.permissions.DeleteAccountCapability;
import com.googlesource.gerrit.plugins.account.permissions.DeleteOwnAccountCapability;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerritAccountRemover implements AccountRemover {
  private static final Logger log = LoggerFactory.getLogger(GerritAccountRemover.class);
  private final Accounts accounts;
  private final PutName putName;
  private final AccountResourceFactory accountFactory;
  private final PermissionBackend permissionBackend;
  private final Provider<CurrentUser> userProvider;
  private final String pluginName;

  @Inject
  public GerritAccountRemover(
      GerritApi api,
      PutName putName,
      AccountResourceFactory accountFactory,
      PermissionBackend permissionBackend,
      Provider<CurrentUser> userProvider,
      @PluginName String pluginName) {
    this.accounts = api.accounts();
    this.putName = putName;
    this.accountFactory = accountFactory;
    this.permissionBackend = permissionBackend;
    this.userProvider = userProvider;
    this.pluginName = pluginName;
  }

  @Override
  public void removeAccount(int accountId) throws Exception {
    AccountApi account = isMyAccount(accountId) ? accounts.self() : accounts.id(accountId);
    removeAccount(account, accountId);
  }

  private boolean isMyAccount(int accountId) {
    return userProvider.get().getAccountId().get() == accountId;
  }

  private AccountResource getAccountResource(int accountId) {
    return isMyAccount(accountId)
        ? new AccountResource(userProvider.get().asIdentifiedUser())
        : accountFactory.create(accountId);
  }

  private void removeAccount(AccountApi account, int accountId) throws Exception {
    removeAccountEmails(account);
    removeAccountSshKeys(account);
    removeExternalIds(account);
    removeFullName(getAccountResource(accountId));
    if (account.getActive()) {
      account.setActive(false);
    }
  }

  @Override
  public boolean canDelete(int accountId) {
    PermissionBackend.WithUser userPermission = permissionBackend.user(userProvider);
    return userPermission.testOrFalse(
            new PluginPermission(pluginName, DeleteAccountCapability.DELETE_ACCOUNT))
        || (userPermission.testOrFalse(
                new PluginPermission(pluginName, DeleteOwnAccountCapability.DELETE_OWN_ACCOUNT))
            && isMyAccount(accountId));
  }

  private void removeFullName(AccountResource userRsc) throws Exception {
    putName.apply(userRsc, new PutName.Input());
  }

  private void removeExternalIds(AccountApi account) throws RestApiException {
    List<String> externalIds =
        account
            .getExternalIds()
            .stream()
            .map(eid -> eid.identity)
            .filter(eid -> !eid.startsWith(ExternalId.SCHEME_USERNAME))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    if (externalIds.size() > 0) {
      account.deleteExternalIds(externalIds);
    }
  }

  private void removeAccountSshKeys(AccountApi account) throws RestApiException {
    List<SshKeyInfo> accountKeys = account.listSshKeys();
    for (SshKeyInfo sshKeyInfo : accountKeys) {
      if (sshKeyInfo != null && sshKeyInfo.valid) {
        account.deleteSshKey(sshKeyInfo.seq);
      }
    }
  }

  private void removeAccountEmails(AccountApi account) throws RestApiException {
    for (EmailInfo email : account.getEmails()) {
      account.deleteEmail(email.email);
    }
  }
}
