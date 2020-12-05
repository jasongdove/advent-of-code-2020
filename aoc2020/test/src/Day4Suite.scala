package aoc2020

import weaver._
import cats.effect._

object Day4Suite extends SimpleIOSuite {
  simpleTest("part 1 example") {
    for {
      input <- Day4.exampleInput()
      result <- IO(Day4.search(input, Day4.part1Required, Day4.optional))
      _ <- expect(result == 2).failFast
    } yield success
  }

  simpleTest("part 1 solution") {
    for {
      input <- Day4.realInput()
      result <- IO(Day4.search(input, Day4.part1Required, Day4.optional))
      _ <- expect(result == 170).failFast
    } yield success
  }

  simpleTest("part 2 example") {
    for {
      inputOne <- Day4.linesOfInput("day4-example-invalid.txt")(Day4.transformInput)
      resultOne <- IO(Day4.search(inputOne, Day4.part2Required, Day4.optional))
      _ <- expect(resultOne == 0).failFast
      inputTwo <- Day4.linesOfInput("day4-example-valid.txt")(Day4.transformInput)
      resultTwo <- IO(Day4.search(inputTwo, Day4.part2Required, Day4.optional))
      _ <- expect(resultTwo == 4).failFast
    } yield success
  }

  simpleTest("part 2 solution") {
    for {
      input <- Day4.realInput()
      result <- IO(Day4.search(input, Day4.part2Required, Day4.optional))
      _ <- expect(result == 103).failFast
    } yield success
  }
}
