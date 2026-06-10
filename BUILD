load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_tests",
)
load(":test_deps.bzl", "mockito_deps")

gerrit_plugin(
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: account",
        "Gerrit-Module: com.gerritforge.gerrit.plugins.account.Module",
        "Gerrit-SshModule: com.gerritforge.gerrit.plugins.account.SshModule",
        "Gerrit-HttpModule: com.gerritforge.gerrit.plugins.account.HttpModule",
    ],
    plugin = "account",
    resources = glob(["src/main/resources/**/*"]),
)

gerrit_plugin_tests(
    name = "account_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    plugin = "account",
    deps = mockito_deps(),
)
