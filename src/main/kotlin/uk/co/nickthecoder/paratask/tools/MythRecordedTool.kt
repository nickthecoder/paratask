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

package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.Column
import java.io.File
import java.net.URLEncoder
import java.sql.Date
import java.sql.DriverManager

/**
 *
 */
class MythRecordedTool : AbstractTableTool<RecordedLine>() {

    override val taskD = TaskDescription("mythRecorded")

    val serverP = StringParameter("server", value = "giddyserv")

    val databaseP = StringParameter("database", value = "mythconverg")

    val userP = StringParameter("user", value = "mythtv")

    val passwordP = StringParameter("password", value = "mythtv")

    val directoryP = FileParameter("direcotry", expectFile = false, value = File("/video/myth/"))

    init {
        Class.forName("com.mysql.jdbc.Driver")

        taskD.addParameters(serverP, databaseP, userP, passwordP, directoryP)
    }

    override fun createColumns() {
        columns.add(Column<RecordedLine, String>("channel") { it.channel })
        columns.add(Column<RecordedLine, Date>("start") { it.start })
        columns.add(Column<RecordedLine, String>("title") { it.title })
        columns.add(Column<RecordedLine, String>("subtitle") { it.subtitle })
        columns.add(Column<RecordedLine, String>("description") { it.description })
        columns.add(BaseFileColumn<RecordedLine>("file", base = directoryP.value!!) { it.file })
    }

    override fun run() {
        list.clear()

        val server = encode(serverP.value)
        val database = encode(databaseP.value)
        val user = encode(userP.value)
        val password = encode(passwordP.value)

        val connect = DriverManager.getConnection("jdbc:mysql://$server/$database?user=$user&password=$password")

        val statement = connect.createStatement()
        val resultSet = statement.executeQuery("SELECT channel.name, progstart, title, subtitle, description, basename FROM recorded, channel WHERE recorded.chanid = channel.chanid ORDER BY progstart DESC;")
        while (resultSet.next()) {
            val channel = resultSet.getString("name")
            val start = resultSet.getDate("progstart")
            val title = resultSet.getString("title")
            val subtitle = resultSet.getString("subtitle")
            val description = resultSet.getString("description")
            val basename = resultSet.getString("basename")

            val file = File(directoryP.value!!, basename)

            val line = RecordedLine(channel, start, title, subtitle, description, file)
            list.add(line)
        }
    }

    private fun encode(str: String) = URLEncoder.encode(str, "UTF-8")

}

data class RecordedLine(
        val channel: String,
        val start: Date,
        val title: String,
        val subtitle: String,
        val description: String,
        val file: File
)
