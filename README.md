ParaTask
========

ParaTask is half way house between a tradional GUI and command line tools.
It contains a set of disparate tools, which can all work together within a single tabbed GUI application.

It includes a file manager, a text editor, a git tool, a terminal, a front end for "grep"...

You could argue that it is a jack of all trades and master of none.
However, the real benefits of ParaTask become apparent when you start customising it.

Every tool has one thing in common, they have "Options". An option is typically a 1 or two character code, which is
used to run a script or a "Task".
Typically a script is just just a single line of code, but could be much longer.
A "Task" is similar to a command line program, but instead of having to remember the command line options,
Task can be prompted in a graphic manner, and their parameters are checked before the task is executed.

ParaTask has become my third most used application (beaten only by Firefox and IntelliJ IDEA), and I'd hate to
be without it :-)

For more information, head over to : http://nickthecoder.co.uk/wiki/view/software/ParaTask

Build
-----

The prerequists are a Java JDK (including javaFX) and gradle v2.0 or higher.

For Debian Linux (as root) :

    apt-get install openjdk-8-jdk libopenjfx-java gradle

Note. If you are running an old version of Debian (e.g. Jessie), you will need to download gradle v2+ manually because
Jessie only supports gradle version 1.5, and I believe version 2.0+ is required.

    git clone https://github.com/nickthecoder/paratask.git
    cd paratask
    gradle installApp

To start ParaTask run the "paratask" script within the build's bin folder.

