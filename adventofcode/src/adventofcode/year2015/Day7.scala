package adventofcode.year2015

import adventofcode.Day

sealed abstract class Source

case class Wire(identifier: String) extends Source
case class Value(n: Int) extends Source

sealed trait Gate extends Source
object Gate {
  case class And(left: Source, right: Source) extends Gate
  case class Or(left: Source, right: Source) extends Gate
  case class LeftShift(left: Source, value: Value) extends Gate
  case class RightShift(left: Source, value: Value) extends Gate
  case class Not(left: Source) extends Gate
}

case class Node(wire: Wire, source: Source)

object Node {
  val assignmentPattern = "([a-z]+) -> ([a-z]+)".r
  val valuePattern = "(\\d+) -> ([a-z]+)".r
  val andPattern = "([a-z]+) AND ([a-z]+) -> ([a-z]+)".r
  val valueAndPattern = "(\\d+) AND ([a-z]+) -> ([a-z]+)".r
  val orPattern = "([a-z]+) OR ([a-z]+) -> ([a-z]+)".r
  val leftShiftPattern = "([a-z]+) LSHIFT (\\d+) -> ([a-z]+)".r
  val rightShiftPattern = "([a-z]+) RSHIFT (\\d+) -> ([a-z]+)".r
  val notPattern = "NOT ([a-z]+) -> ([a-z]+)".r

  def from(line: String): Node = {
    line match {
      case assignmentPattern(id1, identifier)      => Node(Wire(identifier), Wire(id1))
      case valuePattern(value, identifier)         => Node(Wire(identifier), Value(value.toInt))
      case andPattern(id1, id2, identifier)        => Node(Wire(identifier), Gate.And(Wire(id1), Wire(id2)))
      case valueAndPattern(value, id1, identifier) => Node(Wire(identifier), Gate.And(Value(value.toInt), Wire(id1)))
      case orPattern(id1, id2, identifier)         => Node(Wire(identifier), Gate.Or(Wire(id1), Wire(id2)))
      case leftShiftPattern(id1, value, identifier) =>
        Node(Wire(identifier), Gate.LeftShift(Wire(id1), Value(value.toInt)))
      case rightShiftPattern(id1, value, identifier) =>
        Node(Wire(identifier), Gate.RightShift(Wire(id1), Value(value.toInt)))
      case notPattern(id1, identifier) => Node(Wire(identifier), Gate.Not(Wire(id1)))
    }
  }
}

case class Day7Context(filterCircuit: List[Node] => List[Node])

object Day7 extends Day[List[Node], Day7Context, Long](2015, 7) {
  override def transformInput(lines: List[String]): List[Node] =
    lines.map(Node.from)

  override def partOneContext(): Option[Day7Context] =
    Some(Day7Context(identity))

  override def partTwoContext(): Option[Day7Context] = {
    def overrideB(circuit: List[Node]): List[Node] = {
      circuit.collect { node =>
        // 16076 is the result from part one, haven't refactored to have part two depend on part one
        // perhaps if more days use a similar pattern
        if (node.wire.identifier == "b") Node(Wire(node.wire.identifier), Value(16076))
        else node
      }
    }

    Some(Day7Context(overrideB))
  }

  override def process(input: List[Node], context: Option[Day7Context]): Option[Long] =
    context.map { ctx =>
      val circuit = ctx.filterCircuit(input)
      val output = emulate(circuit)
      output("a").toLong
    }

  private def emulate(nodes: List[Node]) = {
    def iterate(state: Map[String, Int], remaining: Seq[Node]): Map[String, Int] = {
      if (remaining.isEmpty) state
      else {
        val (resolvable, rest) = remaining.partition(isResolvable(state))
        val updated = resolvable.foldLeft(state)(apply)
        iterate(updated, rest)
      }
    }

    def isResolvable(state: Map[String, Int])(node: Node): Boolean = {
      def isResolvedOrValue(source: Source): Boolean = source match {
        case Wire(id) => state.contains(id)
        case Value(_) => true
        case _        => false
      }

      node match {
        case Node(_, Gate.And(s1: Source, s2: Source)) => isResolvedOrValue(s1) && isResolvedOrValue(s2)
        case Node(_, Gate.Or(s1: Source, s2: Source))  => isResolvedOrValue(s1) && isResolvedOrValue(s2)
        case Node(_, Gate.LeftShift(s: Source, _))     => isResolvedOrValue(s)
        case Node(_, Gate.RightShift(s: Source, _))    => isResolvedOrValue(s)
        case Node(_, Gate.Not(s: Source))              => isResolvedOrValue(s)
        case Node(_, s: Source)                        => isResolvedOrValue(s)
        case _                                         => false
      }
    }

    def apply(state: Map[String, Int], node: Node): Map[String, Int] = {
      def getValue(source: Source): Int = source match {
        case Wire(identifier) => state(identifier)
        case Value(value)     => value
        case _                => 0
      }

      node match {
        case Node(Wire(id), Gate.And(s1: Source, s2: Source)) => state.updated(id, getValue(s1) & getValue(s2))
        case Node(Wire(id), Gate.Or(s1: Source, s2: Source))  => state.updated(id, getValue(s1) | getValue(s2))
        case Node(Wire(id), Gate.LeftShift(source: Source, value: Value)) =>
          state.updated(id, getValue(source) << value.n)
        case Node(Wire(id), Gate.RightShift(source: Source, value: Value)) =>
          state.updated(id, getValue(source) >> value.n)
        case Node(Wire(id), Gate.Not(source: Source)) => state.updated(id, ~getValue(source) & 0xffff)
        case Node(Wire(id), source: Source)           => state.updated(id, getValue(source))
      }
    }

    iterate(Map.empty, nodes)
  }
}
