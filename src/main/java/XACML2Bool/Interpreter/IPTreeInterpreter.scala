package XACML2Bool.Interpreter

import XACML2Bool.SyntaxTree.{Combine, RTree, Target}

trait IPTreeInterpreter {
  def interpretPTree(target: Target, rules: Combine[RTree]): String
}
