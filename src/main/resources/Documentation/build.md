Build
=====

This plugin is built with Bazel in Gerrit tree mode.

## Build in Gerrit tree

Clone or link this plugin and the account/external_plugin_deps.bzl into the Gerrit's /plugins source
tree.

```
  git clone https://gerrit.googlesource.com/gerrit
  git clone https://github.com/GerritForge/account.git
  cd gerrit/plugin
  ln -s ../../gerrit-account-plugin account
  rm -f external_plugin_deps.bzl
  ln -s account/external_plugin_deps.bzl .
```

Then issue the bazel build command:

```
  bazel build plugins/account
```

The output is created in

```
  bazel-genfiles/plugins/account/account.jar
```

To execute the tests run:

```
  bazel test plugins/account:account_tests
```

or filtering using the comma separated tags:

````
  bazel test --test_tag_filters=account //...
````

This project can be imported into the Eclipse IDE.
Add the plugin name to the `CUSTOM_PLUGINS` set in
Gerrit core in `tools/bzl/plugins.bzl`, and execute:

```
  ./tools/eclipse/project.py
```
