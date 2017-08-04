package uk.co.nickthecoder.paratask.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {


    @Test
    fun escapeTest() {
        assertEquals( "Hello\\nWorld", "Hello\nWorld".escapeNL() )
        assertEquals( "Hello\\n\\nWorld", "Hello\n\nWorld".escapeNL() )
        assertEquals( "Hello\\\\World", "Hello\\World".escapeNL() )
    }

    @Test
    fun unescapeNLTest() {
        assertEquals( "Hello\nWorld", "Hello\\nWorld".unescapeNL())
        assertEquals( "Hello\n\nWorld", "Hello\\n\\nWorld".unescapeNL())
        assertEquals( "Hello\\World", "Hello\\\\World".unescapeNL())
    }

    @Test
    fun uncamelTest() {
        assertEquals( "Hello", "hello".uncamel() )
        assertEquals( "Hello World", "helloWorld".uncamel() )
        assertEquals( "Hello World Again", "helloWorldAgain".uncamel() )
        assertEquals( "Url", "url".uncamel() )
        assertEquals( "URL", "URL".uncamel() )
        assertEquals( "URLString", "URLString".uncamel() )
    }
}
