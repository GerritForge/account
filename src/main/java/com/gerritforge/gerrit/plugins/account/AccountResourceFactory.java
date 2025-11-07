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

import com.google.gerrit.entities.Account;
import com.google.gerrit.server.IdentifiedUser.GenericFactory;
import com.google.gerrit.server.account.AccountResource;
import com.google.inject.Inject;

public class AccountResourceFactory {
  private final GenericFactory userFactory;

  @Inject
  public AccountResourceFactory(GenericFactory userFactory) {
    this.userFactory = userFactory;
  }

  public AccountResource create(int accountId) {
    return new AccountResource(userFactory.create(Account.id(accountId)));
  }
}
