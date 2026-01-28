# Account management plugin for Gerrit Code Review

A plugin that allows accounts to be deleted from Gerrit via an SSH command or
REST API.

## License

This project is licensed under the **Business Source License 1.1** (BSL 1.1).
This is a "source-available" license that balances free, open-source-style access to the code
with temporary commercial restrictions.

* The full text of the BSL 1.1 is available in the [LICENSE](LICENSE) file in this
  repository.
* If your intended use case falls outside the **Additional Use Grant** and you require a
  commercial license, please contact [GerritForge Sales](https://gerritforge.com/contact).

## How to build

To build this plugin you need to have Bazel and Gerrit source tree. See the
[detailed instructions](/src/main/resources/Documentation/build.md) on how to build it.

## Commands

This plugin adds an new [SSH command](/src/main/resources/Documentation/cmd-delete.md)
and a [REST API](/src/main/resources/Documentation/rest-api-accounts.md) for removing
accounts from Gerrit.
