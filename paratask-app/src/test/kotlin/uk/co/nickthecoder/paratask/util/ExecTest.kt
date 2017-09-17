package uk.co.nickthecoder.paratask.util

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.ListSink
import uk.co.nickthecoder.paratask.util.process.ProcessListener
import uk.co.nickthecoder.paratask.util.process.StringSink
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * To run these tests on Windows, you will need to install Unix style commands, pwd, echo, sleep etc.
 * I haven't tried, but running from cygwin would probably work.
 */
class ExecTest {

    @Test
    fun waitFor() {
        val exec = Exec("sleep", 1)
        val start = System.currentTimeMillis()
        exec.start()
        exec.waitFor()
        val end = System.currentTimeMillis()
        assert(end >= start + 1000)
    }

    @Test
    fun timeout() {
        val exec = Exec("sleep", 5)
        val start = System.currentTimeMillis()
        exec.start()
        exec.waitFor(duration = 100, units = TimeUnit.MILLISECONDS)
        val end = System.currentTimeMillis()
        assert(end <= start + 5000) // Should take less than 5s, because we are timing out after 0.1s
    }

    @Test
    fun unusedTimeout() {
        val exec = Exec("pwd")
        val start = System.currentTimeMillis()
        exec.start()
        exec.waitFor(duration = 1000, units = TimeUnit.MILLISECONDS)
        val end = System.currentTimeMillis()
        assert(end <= start + 500) // Should take very little time, the timeout won't be needed.
    }

    @Test
    fun listSink() {
        val exec = Exec("echo", "-e", "Hello\nWorld")
        val out = ListSink()
        exec.outSink = out
        exec.start()
        exec.waitFor()
        assertEquals(listOf("Hello", "World"), out.list)
    }

    @Test
    fun stringSink() {
        val exec = Exec("echo", "Hello World")
        val out = StringSink()
        exec.outSink = out
        exec.start()
        exec.waitFor()
        assertEquals("Hello World", out.toString())
    }

    @Test
    fun currentDirectory() {
        val exec = Exec("pwd")
        val out = StringSink()
        exec.outSink = out
        exec.start()
        exec.waitFor()
        assertEquals(currentDirectory.path, out.toString())
    }

    @Test
    fun tmpDirectory() {
        val exec = Exec("pwd")
        exec.dir(File("/tmp"))
        val out = StringSink()
        exec.outSink = out
        exec.start()
        exec.waitFor()
        assertEquals("/tmp", out.toString())
    }

    @Test
    fun kill() {
        val exec = Exec("sleep", 10)
        val start = System.currentTimeMillis()
        exec.start()
        exec.kill()
        exec.waitFor()
        val end = System.currentTimeMillis()
        assert(end < start + 10000) // Should take less than 10s to kill the process!

    }

    @Test
    fun listen() {
        val exec = Exec("pwd")
        var endCount = 0

        exec.listeners.add(object : ProcessListener {
            override fun finished(process: Process) {
                endCount++
            }
        })
        exec.start()
        exec.waitFor()
        // The listeners are notified in a different thread, so we may need to wait a little while
        // 1 second should be plenty of time, but hopefully, it will do it after the first iteration.
        for (i in 1..100) {
            Thread.sleep(10)
        }
        assertEquals(1, endCount)
    }

    @Test
    fun listenExceedTimout() {
        val exec = Exec("sleep", 2)
        var endCount = 0

        exec.listeners.add(object : ProcessListener {
            override fun finished(process: Process) {
                endCount++
            }
        })
        exec.start()
        exec.waitFor(duration = 1, units = TimeUnit.MILLISECONDS)
        Thread.sleep(10) // If this test fails, give the Process Listener thread time to send out notifications
        assertEquals(0, endCount) // Will exit waitFor() before the process completes, so count should always be zero
    }

    @Test
    fun listenKilled() {
        val exec = Exec("sleep", 10)
        var endCount = 0

        exec.listeners.add(object : ProcessListener {
            override fun finished(process: Process) {
                endCount++
            }
        })
        exec.start()
        // Kill the task almost immediately
        exec.waitFor(duration = 1, units = TimeUnit.MILLISECONDS, killOnTimeout = true)
        // Wait for upto 1 more second, which is NOT enough for the sleep to complete
        exec.waitFor(duration = 1000, units = TimeUnit.MILLISECONDS)

        // The listeners are notified in a different thread, so we may need to wait a little while
        // 1 second should be plenty of time, but hopefully, it will do it after the first iteration.
        for (i in 1..100) {
            Thread.sleep(10)
        }

        // The process should end ( by being killed ), and so the count should be 1
        assertEquals(1, endCount)
    }

}
