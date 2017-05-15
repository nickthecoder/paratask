#!/usr/bin/env bash

BIN_DIR=build/install/paratask/bin
SOURCE="${BIN_DIR}"/paratask
DEST="${BIN_DIR}"/parataskGeneral

echo -e '#!/usr/bin/env bash\nCLASS=$1\nshift\n\n' > "${DEST}"

sed -e 's/uk.co.nickthecoder.paratask.StartTaskKt/"$CLASS"/' "${SOURCE}" >> "${DEST}"

chmod +x "${DEST}"
