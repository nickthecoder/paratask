#!/bin/sh

# Download the jediterm from github. This will create a directory called "jediterm". in the parent directory.
cd ..

# Check if jediterm has already been downloaded
if [ ! -d "jediterm" ] 
then
    git clone https://github.com/JetBrains/jediterm.git
fi

cd jediterm

# Get to a known fixed point. The API may have changed since 2.5, and we need a stable API.
# This will produce a warning if run a second time. Just ignore the warning!
# Note. At the time of writing, there are no branches for different releases, and only a single tag.
git checkout -b my_v2.5 v2.5

# Exclude the MacOS project from being built.
# Without this, the build fails on non-MacOS systems (we don't need this part anyway, even when running on MacOS).
[ ! -f "settings.gradle.orig" ] && cp settings.gradle settings.gradle.orig
grep -v JediTerm settings.gradle.orig > settings.gradle

# Build the project
gradle

# You can test it at this point, by issuing the command "gradle run"
# gradle run
# You should see a white terminal, and when you close it, another will appear testing ssh.
# Closing the second one will end the run.

# Copy the jar files
mkdir -p ../paratask/jediterm
cp build/jediterm-pty-2.2.jar build/jediterm-ssh-2.2.jar build/jediterm-ssh-connector-2.2.jar  ../paratask/jediterm/
cp -r pty/libs/pty4j-0.7.2.jar pty/libs/purejavacomm-0.0.17.jar ../paratask/jediterm/

# Note, there are other jar files included with jediterm, but I've used regular gradle dependencies to download those ones.

# Copy pty's native dll/so files.
# Note, the src/dist directory lets 'gradle installApp' and 'gradle distZip' include them in the distribution.
mkdir -p ../paratask/src/dist/lib
cp -r pty/libs/linux/ pty/libs/macosx/ pty/libs/win/ ../paratask/src/dist/lib/

cp ../paratask

# You can now compile / run paratask
# gradle installApp
# gradle run

