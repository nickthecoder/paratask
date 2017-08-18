#!/bin/sh

# Download the jediterm from github. This will create a directory called "jediterm". in the parent directory.
cd ..
#git clone https://github.com/JetBrains/jediterm.git

# Get to a known fixed point. The API may have changed since 2.5, and we need a stable API.
cd jediterm
#git checkout -b my_v2.5 v2.5

# Exclude the MacOS project from being built.
# Without this, the build fails on non-MacOS systems (we don't need the MacOs stuff anyway).
#mv settings.gradle settings.gradle.orig
#grep -v JediTerm settings.gradle.orig > settings.gradle

# Build the project
#gradle

# You can test it at this point, by issuing the command "gradle run"
# gradle run
# You should see a white terminal, and when you close it, another will appear testing using ssh.
# Closing the second one will end the run.

# Copy the jar files and pty's native libraries
mkdir ../paratask/jediterm
cp lib/*.jar ssh/libs/*.jar build/*jar ../paratask/jediterm/
cp -r pty/libs/* ../paratask/jediterm/
