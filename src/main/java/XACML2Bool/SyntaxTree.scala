package XACML2Bool

object SyntaxTree {

  trait SyntaxTree

  sealed abstract class BOTree[T <: TTree] extends SyntaxTree
  case class Conjunction[T <: TTree](left: BOTree[T], right: BOTree[T]) extends BOTree[T]
  case class Disjunction[T <: TTree](left: BOTree[T], right: BOTree[T]) extends BOTree[T]
  case class Negation[T <: TTree](term: BOTree[T]) extends BOTree[T]
  case class BOLeaf[T <: TTree](term: T) extends BOTree[T]

  //Term의 기준은 각 정책 집합, 정책, 규칙을 하나의 텀으로 본다.
  //Target을 추후 BOTree로 묶기 위해 TTree를 상속시킴(사실 일종의 텀으로 볼 수도 있으므로..).
  sealed abstract class TTree extends SyntaxTree
  case class PSTree(target: Target, policies: BOTree[PTree]) extends TTree
  case class PTree(target: Target, rules: BOTree[RTree]) extends TTree
  case class RTree(target: Target, condition: CTree, effect: String) extends TTree
  case class Target(matchTree: CTree) extends TTree

  sealed abstract class CTree extends SyntaxTree
  case object Any extends CTree

  sealed abstract class BFTree extends CTree
  case class And(left: CTree, right: CTree) extends BFTree
  case class Or(left: CTree, right: CTree) extends BFTree
  case class Not(cond: CTree) extends BFTree

  sealed abstract class BETree extends CTree
  case class Equal[A, B](a: A, b: B) extends BETree
  case class GreaterThan[A, B](a: A, b: B) extends BETree
  case class AnyBinaryExp[A, B](op: String, a: A, b: B) extends BETree
  //  And so on... in BETree

}