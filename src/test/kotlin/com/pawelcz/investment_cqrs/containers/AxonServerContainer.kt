package com.pawelcz.investment_cqrs.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait.forLogMessage

object AxonServerContainer : GenericContainer<AxonServerContainer>("axoniq/axonserver") {
    init {
        withExposedPorts(8024, 8124)
        // the container is ready when the below log message is posted
        waitingFor(forLogMessage(".*Started AxonServer.*\\n", 1))
    }

    // this will allow us to know which host/port to connect to
    val servers: String get() = "localhost:${getMappedPort(8124)}"
}