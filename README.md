# Account management plugin for Gerrit Code Review

A plugin that allows accounts to be deleted from Gerrit via an SSH command or
REST API.

## How to build

To build this plugin you need to have Bazel and Gerrit source tree. See the
[detailed instructions](/src/main/resources/Documentation/build.md) on how to build it.

## Commands

This plugin adds an new [SSH command](/src/main/resources/Documentation/cmd-delete.md)
and a [REST API](/src/main/resources/Documentation/rest-api-accounts.md) for removing
accounts from Gerrit.
