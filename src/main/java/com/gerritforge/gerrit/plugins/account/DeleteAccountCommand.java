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

import com.google.common.annotations.VisibleForTesting;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.gson.Gson;
import com.google.inject.Inject;
import java.io.PrintWriter;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandMetaData(name = "delete", description = "Delete a specific account")
public class DeleteAccountCommand extends SshCommand {
  private static final Logger log = LoggerFactory.getLogger(DeleteAccountCommand.class);

  @Argument(index = 0, required = true, metaVar = "ACCOUNT-ID", usage = "id of the account")
  private int accountId;

  @Option(
    name = "--yes-really-delete",
    metaVar = "ACCOUNT-NAME",
    usage = "confirmation to delete the account"
  )
  private String accountName;

  private final AccountResourceFactory accountFactory;
  private final DeleteAccount deleteAccount;

  @Inject
  public DeleteAccountCommand(AccountResourceFactory accountFactory, DeleteAccount deleteAccount) {
    this.accountFactory = accountFactory;
    this.deleteAccount = deleteAccount;
  }

  @Override
  protected void run() throws UnloggedFailure, Failure, Exception {
    try {
      AccountResource account = accountFactory.create(accountId);

      DeleteAccount.Input input = new DeleteAccount.Input();
      input.accountName = accountName;

      DeleteAccountResponse resp = (DeleteAccountResponse) deleteAccount.apply(account, input);

      @SuppressWarnings("resource")
      PrintWriter out = resp.deleted ? stdout : stderr;
      out.println("Account " + (resp.deleted ? "" : "NOT") + " deleted");
      out.println(new Gson().toJson(resp.accountInfo));
    } catch (Exception e) {
      stderr.printf("FAILED (%s): %s\n", e.getClass().getName(), e.getMessage());
      stderr.flush();
      log.error("Unable to remove account %d", accountId, e);
      die(e);
    }
  }

  @VisibleForTesting
  public void testRun(int accountId, String accountName) throws Exception {
    this.accountId = accountId;
    this.accountName = accountName;
    run();
  }

  @VisibleForTesting
  public void setPrintWriters(PrintWriter out, PrintWriter err) {
    this.stdout = out;
    this.stderr = err;
  }
}
