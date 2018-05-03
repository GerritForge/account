@PLUGIN@ delete
===============

NAME
----
@PLUGIN@ delete - Completely delete an account and all its associated external ids

SYNOPSIS
--------
```
ssh -p @SSH_PORT@ @SSH_HOST@ @PLUGIN@ delete
  [--yes-really-delete <ACCOUNT-NAME>]
  <ACCOUNT-ID>
```

DESCRIPTION
-----------
Deletes an account from the Gerrit installation, removing the associated
external ids.

ACCESS
------
Caller must be a member of a group that is granted the 'Delete Account'
capability (provided by this plugin), or granted the 'Delete Own Account' to
be able to remove its own account.

SCRIPTING
---------
This command is intended to be used in scripts.

OPTIONS
-------

`--yes-really-delete`
:	Actually perform the deletion. If omitted, the command
	will just output information about the deletion and then
	exit. 

EXAMPLES
--------
See if you can delete an account:

```
  $ ssh -p @SSH_PORT@ @SSH_HOST@ @PLUGIN@ delete 100002
```

Completely delete an account:

```
  $ ssh -p @SSH_PORT@ @SSH_HOST@ @PLUGIN@ delete --yes-really-delete "'John Doe'" 100002
```


SEE ALSO
--------

* [Access Control](../../../Documentation/access-control.html)
