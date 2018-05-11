package XACML2Bool

import XACML2Bool.Interpreter.Mode

/** Boolean Expression의 문법 표현 후 sat4j에 알맞는 형태로 변환 **/
/***********************************************************************************************
// Boolean Operator Tree : CNF든 Boolean 식이든 나타내기 위한 최상위 연결 트리
BOTree ::= Conjunction(BOTree, BOTree) | Disjunction(BOTree, BOTree) | Negation(BOTree) | TTree

// TTree : 위 BOTree에서 하나의 항을 나타낸다
TTree ::= PSTree | PTree

// Policy Set Tree : PolicySet의 Target과 하위 Policy들을 연결하는 트리
PSTree ::= BOTree[PTree] | Conjunction(Target, BOTree[PTree])

// Policy Tree : 단일 Policy의 Target과 하위 Rule들을 연결하는 트리
PTree ::= BOTree[RTree] | Conjunction(Target, BOTree[RTree])

// Rule Tree : 단일 Rule의 Target과 Condition을 연결하는 트리
/* 마지막 항은 필요한가? 일단 Effect=Deny를 상정하고 서술하였다. */
RTree ::= CTree | Conjunction(Target, CTree) | Negation(Conjunction(Target, CTree))

// Target : 복수의 Match를 연결하는 리스트
Target ::= CTree /*Subject, Resource, Action의 묶음*/

// Condition Tree : 각 Rule의 조건식을 나타내는 트리, 조건식이 없는 경우 바로 True
CTree ::= Any | BFTree | BETree

// Boolean Function Tree : BOTree와 유사하나 Match와 Condition내에서만 작동한다.
BFTree ::= And(CTree, CTree) | Or(CTree, CTree) | Not(CTree)

// Boolean Expression Tree : Boolean을 반환하는 비교식(Greater Than, Less Than, Equal, ...)을 표현하기 위한 트리
/* 각 비교식은 0차에서는 직접 평가할 필요 없이 a, b, c 등으로 치환 가능하다. */
BETree ::= GreaterThan(Number, Number) | LessThan(Number, Number) | Equal(String, String) | ...
  ***********************************************************************************************/

object SyntaxTree {

  trait SyntaxTree

  sealed trait BOTree[T <: TTree] extends SyntaxTree
  case class Conjunction[T <: TTree](terms: BOTree[T]*) extends BOTree[T]
  case class Disjunction[T <: TTree](terms: BOTree[T]*) extends BOTree[T]
  case class Negation[T <: TTree](term: BOTree[T]) extends BOTree[T]
  case class Term[T <: TTree](term: T) extends BOTree[T]
  case object TrueTerm extends BOTree[RTree]
  case object FalseTerm extends BOTree[RTree]

  //Term의 기준은 각 정책 집합, 정책, 규칙을 하나의 텀으로 본다.
  //Target을 추후 BOTree로 묶기 위해 TTree를 상속시킴(사실 일종의 텀으로 볼 수도 있으므로..).
  sealed trait TTree extends SyntaxTree
  case class PSTree(target: Target, policies: Combine[PTree]) extends TTree
  case class PTree(target: Target, rules: Combine[RTree]) extends TTree
  case class RTree(target: Target, condition: CTree, effect: Mode) extends TTree
  case class Target(matchTree: CTree) extends TTree

  sealed trait Combine[T<:TTree] extends SyntaxTree
  case class PO[T <: TTree](terms:T*) extends Combine[T]
  case class DO[T <: TTree](terms:T*) extends Combine[T]
  case class DuP[T <: TTree](terms:T*) extends Combine[T]
  case class PuD[T <: TTree](terms:T*) extends Combine[T]

  sealed trait CTree extends SyntaxTree
  case object Any extends CTree

  sealed trait BFTree extends CTree
  case class And(left: CTree, right: CTree) extends BFTree
  case class Or(left: CTree, right: CTree) extends BFTree
  case class Not(cond: CTree) extends BFTree

  sealed trait BETree extends CTree
  case class Equal[A, B](a: A, b: B) extends BETree
  case class GreaterThan[A, B](a: A, b: B) extends BETree
  case class AnyBinaryExp[A, B](op: String, a: A, b: B) extends BETree
  //  And so on... in BETree

}