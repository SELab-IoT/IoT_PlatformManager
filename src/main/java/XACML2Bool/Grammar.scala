package XACML2Bool
import scala.xml._
/** Boolean Expression의 문법 표현 후 sat4j에 알맞는 형태로 변환 **/

/*
*
*  TTREE ::= List[PTREE]
*
*  PTREE ::= (Target, BTREE, CombAlg)
*  PolicySet ::= PTREE
*
*  Target ::= (Subject, Resource, Action)
*  Subject ::= BooleanExpression
*  Resource ::= BooleanExpression
*  Action ::= BooleanExpression
*
*  BTREE ::= Conjunction(TTREE, TTREE) | Disjunction(TTREE, TTREE) | Negation(TTREE) | TRUE | FALSE
*  CTREE ::=
*
* */

sealed trait BooleanTree{
  sealed abstract class BOTree[T<:TTree]
  case class Conjunction[T<:TTree](left: BOTree[T], right: BOTree[T]) extends BOTree[T]
  case class Disjunction[T<:TTree](left: BOTree[T], right: BOTree[T]) extends BOTree[T]
  case class Negation[T<:TTree](term: BOTree[T]) extends BOTree[T]
//  case class BOLeaf(term: TTree) extends BOTree[TTree]

  //Term의 기준은 각 정책 집합, 정책, 규칙을 하나의 텀으로 본다.
  sealed abstract class TTree
  case class PSTree(target:Target, policies:BOTree[PTree]) extends TTree
  case class PTree(target:Target, rules:BOTree[RTree]) extends TTree
  case class RTree(target:Target, condition:CTree) extends TTree

  case class Target(subject:CTree, resource:CTree, action:CTree)

  sealed abstract class CTree
  case object Any extends CTree

  sealed abstract class BFTree extends CTree
  case class And(left: CTree, right: CTree) extends BFTree
  case class Or(left: CTree, right: CTree) extends BFTree
  case class Not(cond: CTree) extends BFTree

  sealed abstract class BETree extends CTree
  case class Equal[A, B](a:A, b:B) extends BETree
  case class GreaterThan[A, B](a:A, b:B) extends BETree
  //  And so on... in BETree
}

object Grammar extends BooleanTree{

  // Parse whole XML
  def parseAll(xml: Elem):BOTree[TTree] = {
    if(xml.label.equalsIgnoreCase("PolicySet")) {
      Unit[BOTree[PSTree]](parsePolicySet(xml)) //뭐야 이거 무서워.. AnyValCompanion 이래..
    }else if(xml.label.equalsIgnoreCase("Policy")){
      Unit[BOTree[PTree]](parsePolicy(xml))
    }else ??? //Handle Exception
  }

  // Parse 1 PolicySet Tag
  def parsePolicySet(policySet: Elem):PSTree = {
    val attrID = "policyCombiningAlgorithm:일단대충씀"
    val policyCombAlg = policySet.attribute(attrID) match {
        case Some(nodeSeq) => nodeSeq.lastOption.get.text //문법대로라면 하나밖에 없으니까 복수개의 policyCombiningAlgorithm은 핸들링하지 않음
        case None => ??? //Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
      }

    val target = parseTarget(???)
    val policies:BOTree[PTree] = parsePolicyList(???, policyCombAlg)

    PSTree(target, policies)
  }

  // Parse Policies
  def parsePolicyList(policies: Seq[Node], policyCombAlg: String):BOTree[PTree] = {
    policyCombAlg match {
      case "Permit Override" =>
        // 여기서 BOTree[RTree]를 BOTree[PTree]로 캐스팅 하거나 맨 마지막에 전부 BOTree[TTree]로 flatten 하거나 하는 작업이 필요함. 아니면 그냥 냅두거나.
        policies.foldRight[BOTree[PTree]](Unit())((n, boTree) => Disjunction(Unit(parsePolicy(n)), boTree))
      case "Deny Override" =>
        policies.foldRight[BOTree[PTree]](Unit())((n, boTree) => Conjunction(Unit(parsePolicy(n)), boTree))
      case "여기 일단 대충 씀" => ???
    }
  }

  // Parse 1 Policy Tag (parseAll 에서의 호출 때문에 오버로딩함)
  def parsePolicy(policy: Elem):BOTree[RTree] = {
    val target = parseTarget(???)
    val rules = parseRuleList(policy.nonEmptyChildren)
    Unit(PTree(target, rules))
    //    parsePolicy(policy.last) //이 last가 child 중 last인지 아니면 지 자신인지 모르겠네
  }

  // Parse 1 Policy Tag
  def parsePolicy(policy: Node):BOTree[RTree] = {
    val target = parseTarget(???)
    val rules = parseRuleList(policy.nonEmptyChildren)
    Unit(PTree(target, rules))
  }

  // Parse Rules
  def parseRuleList(rules: Seq[Node]):BOTree[RTree] = ???

  // Parse Target Tag
  def parseTarget(target: Node):Target = ???



}


//
//
//sealed trait TermTree {
//
//  /* PolicySet, Policy, Rule --- PTREE */
//  case class PolicySet(target: Target , policies: BOperator, policyCombAlg:String) extends Term
//  case class Policy(target: Target, rules: BOperator, ruleCombAlg:String) extends Term
//  case class Rule(target: Target, condition: Condition, effect: Boolean) extends Term
//
//  /* Target(Subject, Resource, Action 은 언제나 Conjunction 으로 묶인다)  --- Target */
//  case class Target(subject: Subject, resource: Resource, action: Action) extends Term
//  case class Subject(subject: BExpression) extends Term
//  case class Resource(resource: BExpression) extends Term
//  case class Action(action: String) extends Term
//
//  /* Condition (= BooleanFunction | BooleanExpression)  --- CTREE */
//  sealed trait Condition extends Term
//  //ETREE
//  sealed trait BExpression extends Condition //BooleanExpression (equal, greater than, ...)
//  //case class StringEqual(left:String, right:String) extends BExpression
//  //case class GraterThan(left:String, right:String) extends BExpression //어차피 표현하는거지 실제로 함수 돌리는게 아님
//  //FTREE
//  sealed trait BFunction extends Condition //BooleanFunction (And, Or, Not)
//  case class And(left:Condition, right:Condition) extends BFunction
//  case class Or(left:Condition, right:Condition) extends BFunction
//  case class Not(cond:Condition) extends BFunction
//  case object CTrue extends BFunction //필요한가?
//  case object CFalse extends BFunction
//
//  /* Boolean Operator (^ V ㄱ 연산자. Boolean Function 하고는 약간 다른녀석)  --- BTREE(XACML 문법상에 있는게 아니라 Boolean Exp로 빌드하기 위해 만듬.) */
//  sealed trait BOperator extends Term
//  case class Conjunction(left: Term, right: Term) extends BOperator
//  case class Disjunction(left: Term, right: Term) extends BOperator
//  case class Negation(term: Term) extends BOperator
//  case object True extends BOperator //Conjunction 시 Nil 대용
//  case object False extends BOperator //Disjunction 시 Nil 대용
//
//}
