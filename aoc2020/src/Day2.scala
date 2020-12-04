package aoc2020

import cats.effect._

abstract class PasswordPolicy {
  def isValid(password: String): Boolean
}

case class PasswordPolicyOne(min: Int, max: Int, letter: Char) extends PasswordPolicy {
  override def isValid(password: String): Boolean = {
    val count = password.count(c => c == letter)
    count >= min && count <= max
  }
}

case class PasswordPolicyTwo(first: Int, second: Int, letter: Char) extends PasswordPolicy {
  override def isValid(password: String): Boolean =
    password(first - 1) == letter ^ password(second - 1) == letter
}

case class PasswordWithPolicy(password: String, policy: PasswordPolicy) {
  def isValid: Boolean = policy.isValid(password)
}

object PasswordWithPolicy {
  val pattern = "([0-9]+)\\-([0-9]+) ([a-z]): ([a-z]+)".r

  def one(input: String) = {
    val pattern(min, max, letter, password) = input
    val policy = PasswordPolicyOne(min.toInt, max.toInt, letter.head)
    new PasswordWithPolicy(password, policy)
  }

  def two(input: String) = {
    val pattern(first, second, letter, password) = input
    val policy = PasswordPolicyTwo(first.toInt, second.toInt, letter.head)
    new PasswordWithPolicy(password, policy)
  }
}

object Day2 extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      inputOne <- readInputOne("day2.txt")
      one <- IO(search(inputOne))
      _ <- printResult(one)
      inputTwo <- readInputTwo("day2.txt")
      two <- IO(search(inputTwo))
      _ <- printResult(two)
    } yield ExitCode.Success
  }

  def readInputOne(resourceName: String): IO[List[PasswordWithPolicy]] =
    IO(os.read(os.resource / resourceName).split("\n").map(PasswordWithPolicy.one).toList)

  def readInputTwo(resourceName: String): IO[List[PasswordWithPolicy]] =
    IO(os.read(os.resource / resourceName).split("\n").map(PasswordWithPolicy.two).toList)

  def search(input: List[PasswordWithPolicy]): Int =
    input.count(_.isValid)

  def printResult(result: Int): IO[Unit] =
    IO(println(s"$result passwords are valid"))
}