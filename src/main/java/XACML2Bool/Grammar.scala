package XACML2Bool
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
  //  case class BOLeaf[T<:TTree](term: T) extends BOTree[T]

  //Term의 기준은 각 정책 집합, 정책, 규칙을 하나의 텀으로 본다.
  //Target을 추후 BOTree로 묶기 위해 TTree를 상속시킴(사실 일종의 텀으로 볼 수도 있으므로..).
  sealed abstract class TTree
  case class PSTree(target: Target, policies: BOTree[PTree]) extends TTree
  case class PTree(target: Target, rules: BOTree[RTree]) extends TTree
  case class RTree(target: Target, condition: CTree) extends TTree
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
  //  And so on... in BETree
}

object Grammar extends BooleanTree{

  /** Parse whole XML **/
  def parseAll(xml: Elem):BOTree[TTree] = {
    if(xml.label.equalsIgnoreCase("PolicySet")) {
      Unit[BOTree[PSTree]](parsePolicySet(xml)) //뭐야 이거 무서워.. AnyValCompanion 이래.. 혹시 에러나면 case class BOLeaf 사용.
    }else if(xml.label.equalsIgnoreCase("Policy")){
      Unit[BOTree[PTree]](parsePolicy(xml))
    }else ??? //Handle Exception
  }

  /** Parse 1 PolicySet Tag **/
  def parsePolicySet(policySet: Elem):PSTree = {
    val algID = "PolicySetId"
    val policyCombAlg = policySet.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.lastOption.get.text // getOrElse 사용한 예외 처리는 필요 없을 듯
      case None => ??? //Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }

    val target = parseTarget(???)
    val policies:BOTree[PTree] = parsePolicyList(???, policyCombAlg)

    PSTree(target, policies)
  }

  /** Parse Policies **/
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

  /** Parse 1 Policy Tag (parseAll 에서의 호출 때문에 오버로딩함) **/
  def parsePolicy(policy: Elem):BOTree[RTree] = {

    val algID = "ruleCombiningAlgorithm:일단대충씀"
    val ruleCombAlg = policy.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.lastOption.get.text //문법대로라면 하나밖에 없으니까 복수개의 policyCombiningAlgorithm은 핸들링하지 않음
      case None => ??? //Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }

    val target = parseTarget(???)
    val rules = parseRuleList(policy.nonEmptyChildren, ruleCombAlg)
    Unit(PTree(target, rules))
    //parsePolicy(policy.last) //이 last가 child 중 last인지 아니면 지 자신인지 모르겠네.. 전자라면 위에 싹다지우고 이거 주석 해제
  }

  /** Parse 1 Policy Tag **/
  def parsePolicy(policy: Node):BOTree[RTree] = {

    val algID = "ruleCombiningAlgorithm:일단대충씀"
    val ruleCombAlg = policy.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.lastOption.get.text //문법대로라면 하나밖에 없으니까 복수개의 policyCombiningAlgorithm은 핸들링하지 않음
      case None => ??? //Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }

    val target = parseTarget(???)
    val rules = parseRuleList(policy.nonEmptyChildren, ruleCombAlg)
    Unit(PTree(target, rules))
  }

  /** Parse Rules **/
  def parseRuleList(rules: Seq[Node], ruleCombAlg: String):BOTree[RTree] = {
    ruleCombAlg match {
      case "Permit Override" =>
        // 여기서 BOTree[RTree]를 BOTree[PTree]로 캐스팅 하거나 맨 마지막에 전부 BOTree[TTree]로 flatten 하거나 하는 작업이 필요함. 아니면 그냥 냅두거나.
        rules.foldRight[BOTree[RTree]](Unit())((n, boTree) => Disjunction(Unit(parseRule(n)), boTree))
      case "Deny Override" =>
        rules.foldRight[BOTree[RTree]](Unit())((n, boTree) => Conjunction(Unit(parseRule(n)), boTree))
      case "여기 일단 대충 씀" => ???
    }
  }

  /** Parse 1 Rule Tag **/
  def parseRule(rule: Node):RTree = {
    val target = parseTarget((rule \ "Target").lastOption)
    val condition = parseCondition((rule \ "Condition").lastOption)
    RTree(target, condition)
  }

  /** Parse Target Tag **/
  def parseTarget(target: Option[Node]):Target =
    target match {
      case Some(t) => {
        //        val subject = parseMatchList(t \\ "Subject")
        //        val resource = parseMatchList(t \\ "Resource")
        //        val action = parseMatchList(t \\ "Action")
        //        Target(subject, resource, action)
      }
      case None => Target(Any)
    }

  /** Parse Match **/
  def parseMatchList(matList: NodeSeq):CTree =
    matList.foldRight[CTree](Unit())((m, cTree) => Or(parseMatch(Some(m)), cTree))

  /** Parse Match **/
  def parseMatch(mat: Option[Node]):CTree =
    mat match {
      case Some(_) => ???
      case None => ???
    }


  /** Parse Condition **/
  def parseCondition(cond: Option[Node]):CTree =
    cond match {
      case Some(_) => ???
      case None => ???
    }

}