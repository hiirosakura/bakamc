package moe.forpleuvoir.nebula.serialization.test

import org.junit.jupiter.api.Test

class DSLTest {

  @Test
  def test1(): Unit = {
    dsl {
      dsltest
    }
  }
}

class DSL {
  def dsltest(): Unit = {
    println("DSL")
  }
}


def dsl(block: DSL ?=> Unit): DSL = {
  val dsl = new DSL()
  block(using dsl)
  dsl
}

export DSLOps._

object DSLOps {
  def dsltest(using dsl: DSL): Unit = dsl.dsltest()
}
