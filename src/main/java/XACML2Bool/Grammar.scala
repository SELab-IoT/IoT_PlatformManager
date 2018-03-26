package XACML2Bool
import scala.xml._
/** Boolean Expression의 문법 표현 후 sat4j에 알맞는 형태로 변환 **/

sealed trait Term {
  def parse(term:Elem): Unit
  def toString: String
}

/* PolicySet, Policy, Rule */
case class PolicySet(target: Target , policies: BOperator, policyCombAlg:String) extends Term {
  override def parse(policySet: Elem) = {
    val target = ???
    val policyCombAlg = ???
    val policies = policySet.nonEmptyChildren.foldRight[BOperator](???)(???) //Combining Algorithm에 따라 달라짐

    PolicySet(target, policies, policyCombAlg)
  }
}

case class Policy(target: Target, rules: BOperator, ruleCombAlg:String) extends Term
case class Rule(target: Target, condition: Condition, effect: Boolean) extends Term

/* Target(Subject, Resource, Action 은 언제나 Conjunction 으로 묶인다) */
case class Target(subject: Subject, resource: Resource, action: Action) extends Term
case class Subject(subject: BExpression) extends Term
case class Resource(resource: BExpression) extends Term
case class Action(action: String) extends Term

/* Condition (= BooleanFunction | BooleanExpression) */
sealed trait Condition extends Term
sealed trait BExpression extends Condition //BooleanExpression (equal, greater than, ...)
//case class StringEqual(left:String, right:String) extends BExpression
//case class GraterThan(left:String, right:String) extends BExpression //어차피 표현하는거지 실제로 함수 돌리는게 아님
sealed trait BFunction extends Condition //BooleanFunction (And, Or, Not)
case class And(left:Condition, right:Condition) extends BFunction
case class Or(left:Condition, right:Condition) extends BFunction
case class Not(cond:Condition) extends BFunction
case object CTrue extends BFunction //필요한가?
case object CFalse extends BFunction

/* Boolean Operator (^ V ㄱ 연산자. Boolean Function 하고는 약간 다른녀석) */
sealed trait BOperator extends Term
case class Conjunction(left: Term, right: Term) extends BOperator
case class Disjunction(left: Term, right: Term) extends BOperator
case class Negation(term: Term) extends BOperator
case object True extends BOperator //Conjunction 시 Nil 대용
case object False extends BOperator //Disjunction 시 Nil 대용

class Grammar {

}
