package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree._

trait IRTreeInterpreter {
  def interpretRTree(target: Target, condition: CTree, effect: Mode):String =
    effect match {
      case Permit => interpretPermitRTree(target, condition)
      case Deny => interpretDenyRTree(target, condition)
    }

  def interpretPermitRTree(target:Target, condition:CTree):String
  def interpretDenyRTree(target:Target, condition:CTree):String
}
