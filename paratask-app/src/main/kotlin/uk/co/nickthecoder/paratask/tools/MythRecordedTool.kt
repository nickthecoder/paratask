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
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.sql.Date
import java.sql.DriverManager
import java.text.SimpleDateFormat

/**
 *
 */
class MythRecordedTool : ListTableTool<MythRecordedTool.RecordedLine>() {

    override val taskD = TaskDescription("mythRecorded")

    val serverP = StringParameter("server", value = "giddyserv")

    val databaseP = StringParameter("database", value = "mythconverg")

    val userP = StringParameter("user", value = "mythtv")

    val passwordP = StringParameter("password", value = "mythtv")

    val directoryP = FileParameter("directory", expectFile = false, value = File("/video/myth/"))

    override val rowFilter = RowFilter<RecordedLine>(this, columns, RecordedLine("", "", Date(0), "", "", "", File("")))

    init {
        Class.forName("com.mysql.jdbc.Driver")

        taskD.addParameters(serverP, databaseP, userP, passwordP, directoryP)
        directoryP.aliases.add("direcotry")

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
        val resultSet = statement.executeQuery("SELECT channel.name, channel.chanid, progstart, title, subtitle, description, basename FROM recorded, channel WHERE recorded.chanid = channel.chanid ORDER BY progstart DESC;")
        while (resultSet.next()) {
            val channel = resultSet.getString("name")
            val channelID = resultSet.getString("chanid")
            val start = resultSet.getDate("progstart")
            val title = resultSet.getString("title")
            val subtitle = resultSet.getString("subtitle")
            val description = resultSet.getString("description")
            val basename = resultSet.getString("basename")

            val file = File(directoryP.value!!, basename)

            val line = RecordedLine(channel, channelID, start, title, subtitle, description, file)
            list.add(line)
        }
    }

    private fun encode(str: String) = URLEncoder.encode(str, "UTF-8")

    inner class RecordedLine(
            val channel: String,
            val channelID: String,
            val start: Date,
            val title: String,
            val subtitle: String,
            val description: String,
            val file: File) {

        fun isFile() = true // For "file.json"

        /**
         * Use the myth "services API" to delete a recorded program.
         * See .https://www.mythtv.org/wiki/DVR_Service#DeleteRecording
         *
         * Example POST request :
         * http://BackendServerIP:6544/Dvr/DeleteRecording?StartTime=2011-10-03T19:00:00&ChanId=2066
         */
        fun delete() {

            val url = URL("http://${serverP.value}:6544.")
            println("URL = $url")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formattedStartTime = dateFormat.format(start).replace(' ', 'T')
            val urlParameters = "StartTime=$formattedStartTime&ChanId=$channelID"

            println("Opening connection")
            val connection = url.openConnection() as HttpURLConnection
            println("Opened connection")
            connection.requestMethod = "POST"
            println("Set to post")

            connection.setDoOutput(true)
            println("Setting output stream")
            val wr = DataOutputStream(connection.getOutputStream())
            wr.writeBytes(urlParameters)
            wr.flush()
            wr.close()

            println("Created the connection")
            val responseCode = connection.getResponseCode()
            println("Response code ${responseCode}")

            // We don't care about the results!
            val input = BufferedReader(InputStreamReader(connection.getInputStream()))
            var line = input.readLine()
            while (line != null) {
                println(line)
                line = input.readLine()
            }
            input.close()
        }
    }
}


fun main(args: Array<String>) {
    TaskParser(MythRecordedTool()).go(args)
}
