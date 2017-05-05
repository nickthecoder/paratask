package uk.co.nickthecoder.paratask.project.option

import uk.co.nickthecoder.paratask.project.Preferences

class TopLevelOptions(val optionsName: String) {

    // NOTE, we could cache this list, and whenever a FileOptions is saved, throw the cache away
    // 
    private fun buildIncludeList(): List<FileOptions> {

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

                for (include in fileOptions.includes) {
                    add(include)
                }
            }
        }

        add(optionsName)
        add("global")

        return fileOptionsList
    }

    fun find(code: String): Option? {
        println("Finding '${code}' in TopLevelOptions '${optionsName}'")
        // First we build a list of all the FileOptions and then iterate over the list to find the Option
        val fileOptionsList = buildIncludeList()


        for (fileOptions in fileOptionsList) {
            println("Looking in file : ${fileOptions.file}")

            val option = fileOptions.find(code)
            if (option != null) {
                return option
            }
        }
        println("Not found in top-level")
        return null
    }
}
