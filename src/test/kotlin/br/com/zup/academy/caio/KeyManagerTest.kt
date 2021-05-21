package br.com.zup.academy.caio
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest
class KeyManagerTest(
    val repository: ChavePixRepository
) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Inject
    lateinit var application: EmbeddedApplication<*>

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application.isRunning)
    }

}
