package uk.co.nickthecoder.paratask.options

import uk.co.nickthecoder.paratask.project.Preferences

class TopLevelOptions(val optionsName: String) {

    // NOTE, we could cache this list, and whenever a FileOptions is saved, throw the cache away
    // 
    fun listFileOptions(): List<FileOptions> {

        val fileOptionsList = mutableListOf<FileOptions>()

        val addedOptionsNames = mutableSetOf<String>()

        fun add(optionsName: String) {

            if (addedOptionsNames.contains(optionsName)) {
                return
            }
            addedOptionsNames.add(optionsName)

            for (directory in Preferences.optionsPath) {
                val fileOptions: FileOptions = OptionsManager.getFileOptions(optionsName, directory)
                fileOptionsList.add(fileOptions)

                for (include in fileOptions.listIncludes()) {
                    add(include)
                }
            }
        }

        add(optionsName)
        add("global")

        return fileOptionsList
    }

    fun find(code: String): Option? {

        // First we build a list of all the FileOptions and then iterate over the list to find the Option
        val fileOptionsList = listFileOptions()


        for (fileOptions in fileOptionsList) {

            val option = fileOptions.find(code)
            if (option != null) {
                return option
            }
        }

        return null
    }
}
