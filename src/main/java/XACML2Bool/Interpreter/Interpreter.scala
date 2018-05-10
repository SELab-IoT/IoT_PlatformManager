package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree._
import Builder._

// SyntaxTree to SAT String
abstract class Interpreter extends IRTreeInterpreter{

  var uuid:Long = 0
  var terms:Map[String, Long] = Map()

  final case class InterpretException(private val message: String="", private val cause: Throwable = None.orNull) extends Exception(message, cause)

  def interpretAll(syntaxTree: SyntaxTree):String =
    syntaxTree match {
      case policySet@PSTree(_, _) => interpretTTree(policySet)
      case policy@PTree(_, _) => interpretTTree(policy)
      case _ => throw InterpretException("Only PSTree and PTree can be root node")
    }


  def interpretTTree(tTree: TTree):String =
    tTree match {
      case PSTree(t, ps) => interpretPSTree(t, ps)
      case PTree(t, rs) => interpretPTree(t, rs)
      case RTree(t, cs, eff) => interpretRTree(t, cs, eff)
    }

  def interpretPSTree(target: Target, policies: Combine[PTree]): String
  def interpretPTree(target: Target, rules: Combine[RTree]): String
  def interpretTarget(target: Target):String = interpretCTree(target.matchTree)

  def interpretCTree(cTree: CTree):String =
    cTree match {
      //BFTree
      case and @ And(_, _) => interpretBFTree(and)
      case or @ Or(_, _) => interpretBFTree(or)
      case not @ Not(_) => interpretBFTree(not)
      //BETree
      case anyExp @ AnyBinaryExp(_, _, _) => interpretBETree(anyExp)
      //Any
      case Any => "True"
    }

  def interpretBFTree(bfTree: BFTree):String =
    bfTree match {
      case And(l, r) => con(interpretCTree(l), interpretCTree(r))
      case Or(l, r) => dis(interpretCTree(l), interpretCTree(r))
      case Not(n) => neg(interpretCTree(n))
    }

  def interpretBETree(beTree: BETree):String = {
    val term = terms.getOrElse(beTree.toString, uuid+1)
    if(uuid+1 == term) {
      terms += (beTree.toString -> term)
      uuid = term
    }
    term.toString
  }


  //이거 실제로 필요한지 나중에 보고 필요없으면 BOTree 째로 지울 듯
  def interpretBOTree[T<:TTree](boTree: BOTree[T]):String =
    boTree match {
      case Conjunction(terms @ _*) => con(terms map interpretBOTree:_*)
      case Disjunction(terms @ _*) => dis(terms map interpretBOTree:_*)
      case Negation(term) => neg(interpretBOTree(term))
      case Term(term) => interpretTTree(term)
    }

}
