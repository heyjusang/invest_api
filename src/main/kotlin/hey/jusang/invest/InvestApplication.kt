package hey.jusang.invest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InvestApplication

fun main(args: Array<String>) {
	runApplication<InvestApplication>(*args)
}
