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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gerritforge.gerrit.plugins.account.AccountPersonalInformation;
import com.gerritforge.gerrit.plugins.account.AccountResourceFactory;
import com.gerritforge.gerrit.plugins.account.DeleteAccount;
import com.gerritforge.gerrit.plugins.account.DeleteAccountCommand;
import com.gerritforge.gerrit.plugins.account.DeleteAccountResponse;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.account.AccountResource;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.apache.sshd.server.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeleteAccountCommandTest {

  private DeleteAccountCommand deleteAccountCommand;

  @Mock private AccountResourceFactory accountFactoryMock;
  @Mock private DeleteAccount deleteAccountMock;
  @Mock private Environment envMock;
  @Mock private AccountResource accountResourceMock;
  @Mock private IdentifiedUser userMock;

  @Before
  public void setup() throws Exception {
    deleteAccountCommand = new DeleteAccountCommand(accountFactoryMock, deleteAccountMock);
    deleteAccountCommand.setPrintWriters(
        new PrintWriter(new ByteArrayOutputStream()), new PrintWriter(new ByteArrayOutputStream()));
    DeleteAccountResponse resp =
        new DeleteAccountResponse(true, new AccountPersonalInformation(userMock));
    when(deleteAccountMock.apply(same(accountResourceMock), any(DeleteAccount.Input.class)))
        .thenReturn(resp);
  }

  @Test
  public void givenValidAccount_whenStart_shouldInvokeDeleteAccount() throws Exception {
    int expectedAccountId = 1;
    DeleteAccount.Input expectedInput = new DeleteAccount.Input();
    expectedInput.accountName = "First Last";
    when(accountFactoryMock.create(expectedAccountId)).thenReturn(accountResourceMock);

    deleteAccountCommand.testRun(expectedAccountId, expectedInput.accountName);

    verify(deleteAccountMock).apply(same(accountResourceMock), eq(expectedInput));
  }
}
