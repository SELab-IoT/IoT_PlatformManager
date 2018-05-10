package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree.{CTree, Target}

trait IRuleInterpreter {

  def interpretRTree(target: Target, condition: CTree, effect: Mode): String =
    effect match {
      case Permit => interpretPermitRule(target, condition)
      case Deny => interpretDenyRule(target, condition)
    }

  def interpretPermitRule(target:Target, condition:CTree):String
  def interpretDenyRule(target:Target, condition:CTree):String
}
