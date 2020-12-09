package adventofcode.year2020

import weaver._
import cats.effect._
import _root_.adventofcode.PartNumber

object Day7Suite extends SimpleIOSuite {
  import Day7._

  simpleTest("part 1 example") {
    for {
      input <- exampleInput(PartNumber.One)
      result <- IO(process(input, partOneContext()))
      _ <- expect(result == Some(4)).failFast
    } yield success
  }

  simpleTest("part 1 solution") {
    for {
      input <- realInput()
      result <- IO(process(input, partOneContext()))
      _ <- expect(result == Some(229)).failFast
    } yield success
  }

  simpleTest("part 2 example") {
    for {
      input <- exampleInput(PartNumber.Two)
      result <- IO(process(input, partTwoContext()))
      _ <- expect(result == Some(126)).failFast
    } yield success
  }

  simpleTest("part 2 solution") {
    for {
      input <- realInput()
      result <- IO(process(input, partTwoContext()))
      _ <- expect(result == Some(6683)).failFast
    } yield success
  }
}