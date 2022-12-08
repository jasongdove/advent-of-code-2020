package adventofcode.year2022

import adventofcode.Day
import cats.effect._
import scala.collection.mutable.ArrayBuffer

object Day7 extends IOApp {

  case class Problem(directories: Map[String, Long])

  case class Context(solve: Problem => Long)

  case object Path {
    def combine(one: String, two: String): String = {
      var l = one
      var r = two
      if (r == "/") {
        r = "";
      }
      if (r == "..") {
        l = l.substring(0, l.lastIndexOf('/'))
      } else {
        if (l == "/") {
          l = s"/${r}"
        } else {
          l = s"${l}/${r}"
        }
      }
      if (l == "") {
        l = "/"
      }
      l
    }
    def parents(path: String): List[String] = {
      var split = path.split('/')
      if (split.length > 0) {
        split = split.tail
      }
      split = "" +: split
      // println(s"Parents: ${split.toList} (len: ${split.length})")
      val result = scala.collection.mutable.ArrayBuffer.empty[String]
      for (i <- 1 to split.length) {
        var dir = split.slice(0, i).toList.mkString("/")
        if (dir == "") {
          dir = "/"
        }
        // println(dir)
        result.addOne(dir)
      }
      result.toList
    }
  }

  object Runner extends Day[Problem, Context, Long](2022, 7) {
    override def transformInput(lines: List[String]): Problem = {
      val result = scala.collection.mutable.Map.empty[String, Long]
      var commands = ArrayBuffer.empty[List[String]]
      var buf = ArrayBuffer.empty[String]
      for (line <- lines) {
        if (line.startsWith("$")) {
          if (buf.length > 0) {
            commands.addOne(buf.toList)
          }
          buf.clear()
        }
        buf.addOne(line)
      }
      if (buf.length > 0) {
        commands.addOne(buf.toList)
      }
      println(commands.toList)

      result.addOne(("/", 0))
      var currentDirectory = "";
      for (command <- commands) {
        if (command(0).startsWith("$ cd")) {
          var nextDirectory = command(0).split(" ")(2);
          currentDirectory = Path.combine(currentDirectory, nextDirectory)
          // println(currentDirectory)
        } else if (command(0).startsWith("$ ls")) {
          for (ls <- command.tail) {
            if (ls.startsWith("dir")) {
              val nextDirectory = ls.split(" ")(1)
              val newDirectory = Path.combine(currentDirectory, nextDirectory)
              // println(s"adding a directory... ${newDirectory}")
              if (!result.contains(newDirectory)) {
                result.addOne((newDirectory, 0))
              }
            } else {
              val size = ls.split(" ")(0).toLong
              // println(size)
              val parents = Path.parents(currentDirectory)
              for (parent <- parents) {
                result.update(parent, result(parent) + size)
              }
            }
          }
        }
      }
      println(result)
      Problem(result.toMap)
    }

    override def partOneContext(): Option[Context] =
      Some(Context(l => {
        l.directories.filter(_._2 < 100_000).map(_._2).sum
      }))

    override def partTwoContext(): Option[Context] =
      Some(Context(l => {
        val totalSpace: Long = 70000000
        val unusedSpace: Long = 30000000
        val usedSpace: Long = l.directories("/")
        // println(usedSpace)
        val currentUnusedSpace = totalSpace - usedSpace
        // println(currentUnusedSpace)
        val toDelete = unusedSpace - currentUnusedSpace
        // println(toDelete)
        val target = l.directories.filter(_._2 >= toDelete).minBy(_._2)
        // println(target)
        target._2
      }))

    override def process(input: Problem, context: Option[Context]): Option[Long] = {
      context.map(_.solve(input))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = Runner.run(args)
}