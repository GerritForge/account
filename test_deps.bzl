"""Build-mode aware test dependencies for the account plugin.

Mirrors the detection used by bazlets' gerrit_plugin.bzl: when the plugin
is built in the Gerrit source tree (Gerrit is the main Bazel module) the
in-tree targets are used, otherwise the dependencies are resolved from
the plugin's own rules_jvm_external repository (standalone build).
"""

def mockito_deps():
    """Return the Mockito dependency for the current build mode."""
    if native.module_name() == "gerrit":
        # In-tree build: use Mockito from the Gerrit source tree.
        return ["//lib/mockito"]

    # Standalone build: use Mockito from the plugin's maven repository.
    return ["@account_plugin_deps//:org_mockito_mockito_core"]
