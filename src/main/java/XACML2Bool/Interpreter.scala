package XACML2Bool

import XACML2Bool.SyntaxTree._

// SyntaxTree to SAT String
// Permit or Other version.
object Interpreter{

  var uuid:Long = 0
  var terms:Map[String, Long] = Map()

  final case class InterpretException(private val message: String="", private val cause: Throwable = None.orNull) extends Exception(message, cause)

  def con(l:String, r:String):String = buildWith("*", l, r)
  def dis(l:String, r:String):String = buildWith("+", l, r)
  def neg(n:String):String = "-" + n

  def buildWith(op:String, terms:String*):String = op + joinSeq(terms)

  def wrap(n:String):String = "(" + n + ")"
  def joinAll(n: String*):String = joinSeq(n)
  def joinSeq(n: Seq[String]):String = wrap(n.reduce(_+" "+_))

  def interpretAll(syntaxTree: SyntaxTree):String =
    syntaxTree match {
      case policySet @ PSTree(_, _) => interpretTTree(policySet)
      case policy @ PTree(_, _) => interpretTTree(policy)
      case _ => throw InterpretException("Only PSTree and PTree can be root node")
    }

  def interpretBOTree[T<:TTree](boTree: BOTree[T]):String =
    boTree match {
      case Conjunction(l, r) => con(interpretBOTree(l), interpretBOTree(r))
      case Disjunction(l, r) => dis(interpretBOTree(l), interpretBOTree(r))
      case Negation(n) => neg(interpretBOTree(n))
      case BOLeaf(t) => interpretTTree(t)
    }

  def interpretTTree(tTree: TTree):String = {
    val (left, right) =
      tTree match {
        case PSTree(t, ps) => (interpretTarget(t), interpretBOTree(ps))
        case PTree(t, rs) => (interpretTarget(t), interpretBOTree(rs))
        case RTree(t, cs, effect) => {
          val target = interpretTarget(t)
          val conditions = interpretCTree(cs)
          //later change this --> effect.equals(mode)
          if(effect.equals("Permit")) (target, conditions)
          else return "False"
        }
      }
    wrap(con(left,right))
  }

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

}
