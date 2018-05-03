load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)

gerrit_plugin(
    name = "account",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: account",
        "Gerrit-Module: com.googlesource.gerrit.plugins.account.Module",
        "Gerrit-SshModule: com.googlesource.gerrit.plugins.account.SshModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "account_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    tags = ["account"],
    deps = [":account__plugin_test_deps"],
)

java_library(
    name = "account__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":account__plugin",
        "@mockito//jar",
    ],
)
