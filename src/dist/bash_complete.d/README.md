Command Line Completion
=======================

I've implemented two different approaches to command line completion, static and dynamic.
You can use either (but not both!). My personal preference is Static, as speed is important to me.

Static
------

It's quick

However, if tasks/tools have changed since the script was generated, then the script will need to be re-generated.

To re-generate the bash completion script :

    paratask generateCompletion --output PATH_TO_NEW_FILE

Also, for the changes to be used, you'll need to "source" the updated script. i.e. run :
    . PATH_TO_UPDATED_SCRIPT

Or just start a new bash session!

Dynamic
-------

Dynamic runs the paratask application every time you press the tab key, so it is SLOW! (especially the first time).

However, it is always in sync with the tasks and tools available in paratask.
There is never a need to regenerate the script.

It has extra features:

It will only list files with the correct file extensions.

Also, if ChoiceParameter choices are dependant on previous parameters, then the dynamic version will work
as expected. However, the Static version will only use the default set of choices.

Installing the Script
---------------------

You can install the script for all users by adding a file called "paratask" to etc/bash_completion.d.
Either copy the script itself, or create a new file called /etc/bash_completion.d/paratask with the following content :

    . FULL_PATH_TO_THE_SCRIPT

Note the dot and space before the path.

To install for a single user, add the line :

    . FULL_PATH_TO_THE_SCRIPT

to ~/.bash_completion

If this file does not exist, you could add it to ~/.bashrc instead.

Notes
-----

Currently, neither approaches takes notice of a parameter's "hidden" status, but in future versions, the dynamic version
could ignore hidden parameters, whereas the static version cannot.

