#!/usr/bin/env bash

# Creates a generalised version of the start-up script.
# The gradle generated start-up script "paratask" always uses the main method in StartTask.kt, whereas
# parataskGeneral can use ANY class's main method. The class name is given as argument #1
# e.g. parataskGeneral uk.co.nickthecoder.paratask.tools.GrepToolKt
# will run the grep tool.

BIN_DIR=build/install/paratask/bin
SOURCE="${BIN_DIR}"/paratask
DEST="${BIN_DIR}"/parataskGeneral

echo -e '#!/usr/bin/env bash\nCLASS=$1\nshift\n\n' > "${DEST}"

sed -e 's/uk.co.nickthecoder.paratask.StartTaskKt/"$CLASS"/' "${SOURCE}" >> "${DEST}"

chmod +x "${DEST}"
