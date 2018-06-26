package ConflictDetector.Converter.Interpreter

import ConflictDetector.Converter.SyntaxTree.{Combine, RTree, Target}

trait IPTreeInterpreter {
  def interpretPTree(target: Target, rules: Combine[RTree]): String
}
