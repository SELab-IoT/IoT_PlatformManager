package XACML2Bool.Interpreter

import XACML2Bool.Interpreter.Builder._
import XACML2Bool.SyntaxTree.PTree

abstract class GeneralPolicyCombineInterpreter extends ICombineInterpreter[PTree] with IPTreeInterpreter{

  def interpretPolicies(policies: PTree*) =
    policies.map(policy => interpretPTree(policy.target, policy.rules))
  def disjunctionAll(policies: PTree*):String =
    dis(interpretPolicies(policies:_*):_*)
  def conjunctionAll(policies: PTree*):String =
    con(interpretPolicies(policies:_*):_*)

}
