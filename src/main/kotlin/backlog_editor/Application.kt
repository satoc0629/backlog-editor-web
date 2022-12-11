package backlog_editor

import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.io.File

@SpringBootApplication
@EnableScheduling
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            File(".env").bufferedReader().readLines().forEach {
                println(it)
                val record = it.split("=")
                System.setProperty(record[0], record[1])
            }
            runApplication<Application>(*args) {
                setBannerMode(Banner.Mode.OFF)
            }
            logger.info { "http://localhost:8050/login/oauth2/code/backlog/" }
            logger.info { "http://localhost:8050" }
        }
    }
}
// Place definition above class declaration to make field static

private val logger = KotlinLogging.logger() {}


