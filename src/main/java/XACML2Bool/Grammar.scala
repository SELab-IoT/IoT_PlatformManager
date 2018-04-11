package XACML2Bool
import scala.collection.immutable.Stream.Empty
import scala.xml._
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

sealed trait BooleanTree{
  sealed abstract class BOTree[T<:TTree]
  case class Conjunction[T<:TTree](left: BOTree[T], right: BOTree[T]) extends BOTree[T]
  case class Disjunction[T<:TTree](left: BOTree[T], right: BOTree[T]) extends BOTree[T]
  case class Negation[T<:TTree](term: BOTree[T]) extends BOTree[T]
  case class BOLeaf[T<:TTree](term: T) extends BOTree[T]

  //Term의 기준은 각 정책 집합, 정책, 규칙을 하나의 텀으로 본다.
  //Target을 추후 BOTree로 묶기 위해 TTree를 상속시킴(사실 일종의 텀으로 볼 수도 있으므로..).
  sealed abstract class TTree
  case class PSTree(target: Target, policies: BOTree[PTree]) extends TTree
  case class PTree(target: Target, rules: BOTree[RTree]) extends TTree
  case class RTree(target: Target, condition: CTree, effect: String) extends TTree
  case class Target(matchTree: CTree) extends TTree

  sealed abstract class CTree
  case object Any extends CTree

  sealed abstract class BFTree extends CTree
  case class And(left: CTree, right: CTree) extends BFTree
  case class Or(left: CTree, right: CTree) extends BFTree
  case class Not(cond: CTree) extends BFTree

  sealed abstract class BETree extends CTree
  case class Equal[A, B](a:A, b:B) extends BETree
  case class GreaterThan[A, B](a:A, b:B) extends BETree
  case class AnyBinaryExp[A, B](op:String, a:A, b:B) extends BETree
  //  And so on... in BETree
}

final case class ParseException(private val message: String="", private val cause: Throwable = None.orNull) extends Exception(message, cause)

object Grammar extends BooleanTree {

  /** Parse whole XML **/
  def parseAll(xml: Elem): BOTree[TTree] = {
    val label = xml.label.equalsIgnoreCase(_)
    if(label("PolicySet")) BOLeaf(parsePolicySet(xml))
    else if (label("Policy")) BOLeaf(parsePolicy(xml))
    else throw new ParseException("Not PolicySet or Policy") //Handle Exception
  }

  /** Parse 1 PolicySet Tag **/
  def parsePolicySet(policySet: Elem):PSTree = {
    val algID = "PolicyCombiningAlgId"
    val policyCombAlg = policySet.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.lastOption.get.text // getOrElse 사용한 예외 처리는 필요 없을 듯
      case None => throw new ParseException("No PolicyCombiningAlg")//Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }
    val target = parseTarget((policySet \ "Target").headOption)
    val policyNode = (policySet \ "Policy")
    val policies: BOTree[PTree] = parsePolicyList(policyNode, policyCombAlg)
    PSTree(target, policies)
  }

  /** Parse Policies **/
  def parsePolicyList(policies: NodeSeq, policyCombAlg: String): BOTree[PTree] = {
    val leaves = policies.map(policy => BOLeaf(parsePolicy(policy)))
    policyCombAlg match {
      case "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides" =>
        leaves.reduce[BOTree[PTree]](Disjunction(_, _))
      case "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides" =>
        leaves.reduce[BOTree[PTree]](Conjunction(_, _))
      case "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit" =>
        leaves.reduce[BOTree[PTree]](Disjunction(_, _)) // Permit 이 하나라도 있으면 Permit 아니면 Deny
      case "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny" =>
        leaves.reduce[BOTree[PTree]](Conjunction(_, _)) // Deny 가 하나라도 있으면 Deny 아니면 Permit
      case _ => throw new ParseException("No Such Policy Combining Algorithm")
    }
  }

  /** Parse 1 Policy Tag (parseAll 에서의 호출 때문에 오버로딩함) **/
  def parsePolicy(policy: Elem): PTree =
    parsePolicy(policy.last)

  /** Parse 1 Policy Tag **/
  def parsePolicy(policy: Node): PTree = {
    val algID = "RuleCombiningAlgId"
    val ruleCombAlg = policy.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.head.text //문법대로라면 하나밖에 없으니까 복수개의 policyCombiningAlgorithm은 핸들링하지 않음
      case None => throw new ParseException("No ruleCombiningAlg")//Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }
    val target = parseTarget((policy \ "Target").headOption)
    val rules = parseRuleList((policy \ "Rule"), ruleCombAlg)
    PTree(target, rules)
  }

  /** Parse Rules **/
  def parseRuleList(rules: NodeSeq, ruleCombAlg: String): BOTree[RTree] = {
    val leaves = rules.map(rule => {
        val rtree = parseRule(rule)
        val leaf = BOLeaf(rtree)
        // Permit이 아닌 경우 Negation 추가
        if(!rtree.effect.equalsIgnoreCase("Permit")) Negation(leaf) else leaf
      }
    )
    
    ruleCombAlg match {
      case "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides" =>
        leaves.reduce[BOTree[RTree]](Disjunction(_, _))
      case "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides" =>
        leaves.reduce[BOTree[RTree]](Conjunction(_, _))
      case "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit" =>
        leaves.reduce[BOTree[RTree]](Disjunction(_, _))
      case "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny" =>
        leaves.reduce[BOTree[RTree]](Conjunction(_, _))
      case _ => throw new ParseException("No Such Rule Combining Algorithm")
    }
  }

  /** Parse 1 Rule Tag **/
  def parseRule(rule: Node): RTree = {
    val target = parseTarget((rule \ "Target").headOption)
    val condition = parseCondition((rule \ "Condition").headOption)
    val effect = (rule \ "@Effect").text
    RTree(target, condition, effect)
  }

  /** Parse Target Tag **/
  def parseTarget(target: Option[Node]): Target =
    target match {
      case Some(t) => {
        val anyOfList = if(t \ "AnyOf" isEmpty) Any else (t \ "AnyOf").map(parseAnyOf).reduce(And(_, _))
        Target(anyOfList)
      }
      case None => Target(Any)
    }

  /** Parse AnyOf Tag **/
  def parseAnyOf(anyOf: Node): CTree =
    (anyOf \ "AllOf").map(parseAllOf).reduce(Or(_, _))

  /** Parse AllOf Tag **/
  def parseAllOf(allOf: Node): CTree =
    (allOf \ "Match").map(parseMatch).reduce(And(_, _))

  /** Parse Match **/
  def parseMatch(mat: Node): CTree = {
    val matchId = (mat \ "@MatchId").toString()
    val param1 = (mat \ "AttributeValue").text
    val param2 = (mat \ "AttributeDesignator" \ "@AttributeId").toString()
    // TODO: 1차에서 MatchId의 종류에 따라 핸들링 할 것임.
    AnyBinaryExp(matchId, param1, param2)
  }

  /** Parse Condition **/
  def parseCondition(cond: Option[Node]): CTree =
    cond match {
      case Some(c) => parseApply(c \ "Apply")
      case None => Any
    }

  def catalog = Map(("function:and", And), ("function:or", Or), ("function:not", Not))
  def parseApply(apply: NodeSeq): CTree = {
    val functionId = (apply \ "@FunctionId").text
    val tuple = catalog.find(f => functionId contains f._1)
    val params = (apply \ "Apply")
    tuple match {
      case Some((_, func)) => {
        func match {
          case Not => Not(parseApply(params))
          case And => {
            val left = parseApply(params.head)
            val right = parseApply(params.last)
            And(left, right)
          }
          case Or => {
            val left = parseApply(params.head)
            val right = parseApply(params.last)
            Or(left, right)
          }
        }
      }
      case None => {
        // TODO: 1차에서 FunctionId의 종류에 따라 핸들링 할 것임.
        val value = (apply \ "AttributeValue").text
        val designator = (apply \\ "AttributeDesignator" \ "@Category")+"::"+(apply \\ "AttributeDesignator" \ "@AttributeId")
        AnyBinaryExp(functionId, designator, value)
      }
    }
  }

}