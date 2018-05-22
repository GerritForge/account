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

package com.gerritforge.gerrit.plugins.account.test;

import static org.mockito.Mockito.*;

import com.gerritforge.gerrit.plugins.account.AccountRemover;
import com.gerritforge.gerrit.plugins.account.DeleteAccount;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeleteAccountTest {

  @Mock private AccountRemover accountRemoverMock;

  @Mock private AccountResource accountResourceMock;

  @Mock private IdentifiedUser userMock;

  @Before
  public void setup() {
    when(accountResourceMock.getUser()).thenReturn(userMock);
  }

  @Test
  public void givenAccountWithPermissions_whenRunningDeleteAccount_thenAccountRemoverIsInvoked()
      throws Exception {
    int accountId = 1;
    DeleteAccount.Input input = new DeleteAccount.Input();
    input.accountName = "First Last";
    mockUserData(accountId, input.accountName);
    when(accountRemoverMock.canDelete(accountId)).thenReturn(true);

    new DeleteAccount(accountRemoverMock).apply(accountResourceMock, input);

    verify(accountRemoverMock).removeAccount(accountId);
  }

  @Test(expected = AuthException.class)
  public void givenAccountWithoutPermissions_whenRunningDeleteAccount_thenThrowAuthException()
      throws Exception {
    int accountId = 1;
    DeleteAccount.Input input = new DeleteAccount.Input();
    input.accountName = "First Last";
    mockUserData(accountId, input.accountName);
    when(accountRemoverMock.canDelete(accountId)).thenReturn(false);

    new DeleteAccount(accountRemoverMock).apply(accountResourceMock, input);

    verify(accountRemoverMock, times(0)).removeAccount(accountId);
  }

  @Test
  public void givenAccountWithNonMatching_whenRunningDeleteAccount_thenAccountRemoverIsNotInvoked()
      throws Exception {
    int accountId = 1;
    DeleteAccount.Input input = new DeleteAccount.Input();
    input.accountName = "NonMatching Name";
    mockUserData(accountId, "First Last");
    when(accountRemoverMock.canDelete(accountId)).thenReturn(true);

    new DeleteAccount(accountRemoverMock).apply(accountResourceMock, input);

    verify(accountRemoverMock, times(0)).removeAccount(accountId);
  }

  private void mockUserData(int accountId, String accountName) {
    when(userMock.getAccountId()).thenReturn(new Account.Id(accountId));
    when(userMock.getName()).thenReturn(accountName);
  }
}
