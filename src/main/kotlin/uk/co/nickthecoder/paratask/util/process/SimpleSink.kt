/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.util.process

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

open class SimpleSink(val bufferSize: Int = 200) : Sink {

    var reader: Reader? = null

    override fun setStream(stream: InputStream) {
        reader = InputStreamReader(stream)
    }

    override fun run() {

        val reader = this.reader ?: return

        try {
            val buffer = CharArray(bufferSize)

            while ( true ) {
                val amount: Int = reader.read(buffer, 0, bufferSize)
                if ( amount == -1 ) {
                    break
                }
                sink(buffer, amount)
            }
        } catch (e: IOException) {
            sinkError(e)
        } finally {
            reader.close()
        }
    }

    protected open fun sink(buffer: CharArray, len: Int) {
        // Does nothing - throws away the output
    }

    protected fun sinkError(e: IOException) {
        e.printStackTrace()
    }

}