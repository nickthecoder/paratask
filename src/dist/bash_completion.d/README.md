Command Line Completion
=======================

To get command completion working (pressing tab to complete the arguments) for paratask...

Installing the Script
---------------------

You can install the script for all users by adding a file called "paratask" to etc/bash_completion.d.
Either copy the script itself, or create a new file called /etc/bash_completion.d/paratask with the following content :

    . /FULL_PATH_TO_HERE/bash_complete.d/paratask

Note the dot and space before the path.

Don't forget that this file will be executed by root (when you bring up a root terminal), so make very certain that
regular users cannot edit the file, or replace it with a different version!

To install for a single user, add the line :

    . /FULL_PATH_TO_HERE/bash_complete.d/paratask

to ~/.bash_completion

If this file does not exist, you could add it to ~/.bashrc instead.

