@PLUGIN@ - /accounts/ REST API
==============================

This page describes the project related REST endpoints that are added
by the @PLUGIN@.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="project-endpoints"> Account Endpoints
--------------------------------------------

### <a id="delete-project"> Delete Account
_DELETE /accounts/[\{account-id\}](../../../Documentation/rest-api-accounts.html#account-id)_

OR

_POST /accounts/[\{account-id\}](../../../Documentation/rest-api-accounts.html#account-id)/@PLUGIN@~delete_

Deletes an account.

Options for the deletion can be specified in the request body as a
[DeleteOptionsInput](#delete-options-input) entity.

Please note that some proxies prohibit request bodies for _DELETE_
requests. In this case, if you want to specify options, use _POST_
to delete the project.

Caller must be a member of a group that is granted the 'Delete Account'
capability (provided by this plugin), or granted the 'Delete Own Account' to
be able to remove its own account.

#### Request

```
  DELETE /accounts/1000002 HTTP/1.0
  Content-Type: application/json;charset=UTF-8

  {
    "account_name": "John Doe"
  }
```

#### Response

```
  HTTP/1.1 204 No Content
```


<a id="json-entities">JSON Entities
-----------------------------------

### <a id="delete-options-info"></a>DeleteOptionsInfo

The `DeleteOptionsInfo` entity contains options for the deletion of a
project.

* _account_name_ (optional): If set to the account full name, the account is deleted. Otherwise
                             the invocation is only a dry-run that display the account details.

SEE ALSO
--------

* [Accounts related REST endpoints](../../../Documentation/rest-api-accounts.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
