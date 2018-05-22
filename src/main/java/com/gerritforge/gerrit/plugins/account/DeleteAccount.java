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

import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.BadRequestException;
import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountResource;
import com.google.inject.Inject;

public class DeleteAccount implements RestModifyView<AccountResource, DeleteAccount.Input> {
  public static class Input {
    public String accountName;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Input other = (Input) obj;
      if (accountName == null) {
        if (other.accountName != null) return false;
      } else if (!accountName.equals(other.accountName)) return false;
      return true;
    }
  }

  private final AccountRemover remover;

  @Inject
  public DeleteAccount(AccountRemover remover) {
    this.remover = remover;
  }

  @Override
  public Object apply(AccountResource resource, DeleteAccount.Input input)
      throws AuthException, BadRequestException, ResourceConflictException, Exception {
    boolean removed = false;

    IdentifiedUser user = resource.getUser();
    AccountPersonalInformation accountInfo = new AccountPersonalInformation(user);
    int accountId = user.getAccountId().get();
    assertDeletePermission(accountId);

    if (input != null && input.accountName != null && user.getName().equals(input.accountName)) {
      remover.removeAccount(accountId);
      removed = true;
    }

    return new DeleteAccountResponse(removed, accountInfo);
  }

  private void assertDeletePermission(int accountId) throws AuthException {
    if (!remover.canDelete(accountId)) {
      throw new AuthException("not allowed to delete account " + accountId);
    }
  }
}
