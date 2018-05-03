Provides the ability to manage accounts.

Gerrit accounts need at time some maintenance for troubleshooting
purposes or for allowing people to "be forgotten" and removing
all their associated identities and personal information.

Allow admins and users to see what Gerrit holds about accounts in its
repository and give the ability to remove any information by preserving
the overall consistency with existing review data.

Limitations
-----------

There are a few caveats:

* Removal of accounts cannot be undone

	This is an irreversible action, and should be taken with extreme
	care. Backups of the accounts repository is strongly recommended.

* You cannot physically delete accounts but make sure they become anonymous.

	The removal of the accounts is only a logical operation. Gerrit will
	keep the account id and some associated external ids. All the other information
	that allows to associate a Gerrit account to a physical person will be removed.
	The only requirement for Gerrit is to have a username with a corresponding account_id,
	which will always be kept in the repository for consistency with existing
	reviews.

Replication of accounts deletions
--------------------------------

This plugin does not explicitly replicate any account deletions, but it triggers
an event when an account is deleted. The [replication plugin]
(https://gerrit-review.googlesource.com/#/admin/projects/plugins/replication)
will do the job by triggering the replication of the associated ref updates on the
All-Users repository.

Access
------

To be allowed to delete arbitrary projects a user must be a member of a
group that is granted the 'Delete Account' capability (provided by this
plugin). Users can be enabled to remove their own accounts if they are member
of a group that is granted the 'Delete Own Account' capability (provided by this
plugin).

