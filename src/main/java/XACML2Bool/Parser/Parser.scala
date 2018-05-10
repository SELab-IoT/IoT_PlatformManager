package XACML2Bool.Parser

import XACML2Bool.Interpreter.Mode
import XACML2Bool.SyntaxTree._

import scala.xml._

object Parser{

  final case class ParseException(private val message: String="", private val cause: Throwable = None.orNull) extends Exception(message, cause)

  /** Parse whole XML **/
  def parseAll(xml: Elem): SyntaxTree = {
    def label = xml.label.equalsIgnoreCase(_)
    if(label("PolicySet")) parsePolicySet(xml)
    else if (label("Policy")) parsePolicy(xml)
    else throw new ParseException("Not PolicySet or Policy") //Handle Exception
  }

  /** Parse 1 PolicySet Tag **/
  def parsePolicySet(policySet: Elem):PSTree = {
    val algID = PolicyCombiningAlgorithmParser.AlgID
    val policyCombAlg = policySet.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.lastOption.get.text // getOrElse 사용한 예외 처리는 필요 없을 듯
      case None => throw new ParseException("No PolicyCombiningAlg")//Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }
    //TODO: PolicySet이 PolicySet을 포함하는 경우 핸들링
    val target = parseTarget((policySet \ "Target").headOption)
    val policyNode = (policySet \ "Policy")
    val policies= parsePolicyList(policyNode, policyCombAlg)
    PSTree(target, policies)
  }

  /** Parse Policies **/
  def parsePolicyList(policies: NodeSeq, policyCombAlg: String): Combine[PTree] = {
    val terms = policies map parsePolicy
    PolicyCombiningAlgorithmParser.parseCombiningAlgorithm(policyCombAlg, terms:_*)
  }

  /** Parse 1 Policy Tag (parseAll 에서의 호출 때문에 오버로딩함) **/
  def parsePolicy(policy: Elem): PTree =
    parsePolicy(policy.last)

  /** Parse 1 Policy Tag **/
  def parsePolicy(policy: Node): PTree = {
    val algID = RuleCombiningAlgorithmParser.AlgID
    val ruleCombAlg = policy.attribute(algID) match {
      case Some(nodeSeq) => nodeSeq.head.text //문법대로라면 하나밖에 없으니까 복수개의 policyCombiningAlgorithm은 핸들링하지 않음
      case None => throw new ParseException("No RuleCombiningAlg")//Handling No PolicyCombining Algorithm : 디폴트 적용하거나 예외 던지거나.
    }
    val target = parseTarget((policy \ "Target").headOption)
    val rules = parseRuleList((policy \ "Rule"), ruleCombAlg)
    PTree(target, rules)
  }

  /** Parse Rules **/
  def parseRuleList(rules: NodeSeq, ruleCombAlg: String): Combine[RTree] = {
    val terms = rules map parseRule
    RuleCombiningAlgorithmParser.parseCombiningAlgorithm(ruleCombAlg, terms:_*)
  }

  /** Parse 1 Rule Tag **/
  def parseRule(rule: Node): RTree = {
    val target = parseTarget((rule \ "Target").headOption)
    val condition = parseCondition((rule \ "Condition").headOption)
    val effect = Mode toMode (rule \ "@Effect").text
    RTree(target, condition, effect.get)
  }

  /** Parse Target Tag **/
  def parseTarget(target: Option[Node]): Target =
    target match {
      case Some(t) => {
        val anyOfList =
          if(t \ "AnyOf" isEmpty) Any
          else (t \ "AnyOf").map(parseAnyOf).reduce(And(_, _))
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