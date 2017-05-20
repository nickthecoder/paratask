package uk.co.nickthecoder.paratask.options

class ScriptException(val script: GroovyScript, e: Exception) : RuntimeException(e) {

    override val message = "Error running groovy script.\n\n" + super.message + "\n" + script.source + "\n"

}
