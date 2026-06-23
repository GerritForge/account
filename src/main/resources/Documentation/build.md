Build
=====

This plugin is built with Bazel in Gerrit tree mode.

## Build in Gerrit tree

Clone or link this plugin into the Gerrit's /plugins source tree.

```
  git clone https://gerrit.googlesource.com/gerrit
  git clone https://github.com/GerritForge/account.git
  cd gerrit/plugins
  ln -s ../../account .
```

Then issue the bazel build command:

```
  bazel build plugins/account
```

The output is created in

```
  bazel-bin/plugins/account/account.jar
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
