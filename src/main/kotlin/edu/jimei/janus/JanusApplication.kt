package edu.jimei.janus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JanusApplication

fun main(args: Array<String>) {
    runApplication<JanusApplication>(*args)
}
