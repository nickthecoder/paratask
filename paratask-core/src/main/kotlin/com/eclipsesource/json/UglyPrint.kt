/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
 *
 * Nick The Code's version of EclipseSource's PrettyPrint.
 * Converted from Java to Kotlin.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 * I have placed it in a package "com.eclipsesource.json just to get around the
 * problem of JsonWriter being package private. Grr.
 * This code is NOT part of EclipseSource.
*/
package com.eclipsesource.json

import java.io.Writer

/**
 * Designed to be more compact than the standard PrettyPrint, but still readable by humans.
 * What I wanted, was the same format as PrettyPrint, but simple JsonObject, which only have primitive values
 * to be on one line. Hoever, that isn't possible, because JsonWriter doesn't have any knowledge of the
 * objects it is writing (so it cannot know whether an object only has primitive values).
 *
 * Similarly, I wanted arrays of primative values to be one line, but this is also not possible.
 *
 * So, this is a massive compromise, ending in an ugly result (hence the name!).
 * It isn't even possible to add a new line to the end of the file! Sorry.
 *
 * To produce output the way I want would require writing the whole writer code from scratch,
 * with all the likely bugs that would ensue. Pragmatism won, so the output is ugly.
 */
class UglyPrint(val indentString: String) : WriterConfig() {

    internal override fun createWriter(writer: Writer): JsonWriter {
        return UglyPrintWriter(writer, indentString.toCharArray())
    }

    internal class UglyPrintWriter(writer: Writer, val indentChars: CharArray)

        : JsonWriter(writer) {

        private var indent: Int = 0

        fun write(c: Char) {
            writer.write(c.toInt())
        }

        override fun writeArrayOpen() {
            indent++
            writeNewLine()
            write('[')
            indent++
            writeNewLine()
        }

        override fun writeArrayClose() {
            indent--
            writeNewLine()
            indent--
            write(']')
            writeNewLine()
        }

        override fun writeArraySeparator() {
            write(',')
            writeNewLine()
        }

        override fun writeObjectOpen() {
            write('{')
            write(' ')
        }

        override fun writeObjectClose() {
            write('}')
        }

        override fun writeMemberSeparator() {
            write(':')
            write(' ')
        }

        override fun writeObjectSeparator() {
            write(',')
            write(' ')
        }

        private fun writeNewLine() {
            writer.write("\n")
            for (i in 1..indent) {
                writer.write(indentChars)
            }
        }

    }

}
