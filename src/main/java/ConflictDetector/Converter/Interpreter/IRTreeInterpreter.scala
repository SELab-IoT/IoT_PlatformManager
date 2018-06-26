package ConflictDetector.Converter.Interpreter

import ConflictDetector.Converter.SyntaxTree._

trait IRTreeInterpreter {

  def interpretPermitRTree(target:Target, condition:CTree):String
  def interpretDenyRTree(target:Target, condition:CTree):String
  def interpretRTree(target: Target, condition: CTree, effect: Mode):String =
    effect match {
      case Permit => interpretPermitRTree(target, condition)
      case Deny => interpretDenyRTree(target, condition)
    }

}
